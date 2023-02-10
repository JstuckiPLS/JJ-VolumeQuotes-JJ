package com.pls.restful.quote;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.dto.enums.DateRange;
import com.pls.dto.enums.SavedQuoteExpirationResolution;
import com.pls.dto.quote.SavedQuoteListItemDTO;
import com.pls.dto.quote.SavedShipmentLoadDTO;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.dtobuilder.quote.SavedQuoteListItemDTOBuilder;
import com.pls.dtobuilder.quote.SavedQuoteShipmentGridTooltipDTOBuilder;
import com.pls.dtobuilder.savedquote.SavedQuoteDTOBuilder;
import com.pls.dtobuilder.shipment.LtlPricingProposalEntityBuilder;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.quote.service.SavedQuoteService;
import com.pls.restful.util.ShipmentProposalUtils;
import com.pls.shipment.domain.LtlPricingProposalsEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.service.PricingProposalService;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * Saved Quote REST service.
 * 
 * @author Mikhail Boldinov, 27/03/13
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/saved")
public class SavedQuoteResource {
    private static final LtlPricingProposalEntityBuilder PRICING_PROPOSAL_BUILDER = new LtlPricingProposalEntityBuilder();

    @Autowired
    private SavedQuoteService savedQuoteService;
    @Autowired
    private UserPermissionsService userPermissionsService;
    @Autowired
    private PackageTypeDictionaryService packageTypeDictionaryService;
    @Autowired
    private PricingProposalService pricingProposalService;

    public static final int MAX_HISTORY_RANGE_IN_DAYS = 90;

    private final SavedQuoteDTOBuilder savedQuoteDTOBuilder = new SavedQuoteDTOBuilder(
            new SavedQuoteDTOBuilder.DataProvider() {
                @Override
                public PackageTypeEntity findPackageType(String id) {
                    return packageTypeDictionaryService.getById(id);
                }

                @Override
                public SavedQuoteEntity findSavedQuoteById(Long id) {
                    return savedQuoteService.getSavedQuoteById(id);
                }
            });

    private final SavedQuoteListItemDTOBuilder savedQuoteListItemDTOBuilder = new SavedQuoteListItemDTOBuilder();
    private final SavedQuoteShipmentGridTooltipDTOBuilder savedQuoteTooltipDTOBuilder = new SavedQuoteShipmentGridTooltipDTOBuilder();

    /**
     * Remove specified saved quote from DB and return new list of saved quotes.
     *
     * @param customerId
     *            id of customer
     * @param quoteId
     *            id of saved quote which should be deleted
     * @see #get
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{propositionId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@QueryParam("customerId") Long customerId, @PathVariable("propositionId") Long quoteId) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.QUOTES_VIEW.name());
        savedQuoteService.deleteSavedQuote(quoteId);
    }

    /**
     * Returns ShipmentDTO with mark about its saved quote age in order to decide is this quote required to be
     * re rated.
     *
     * @param customerId
     *            id of customer
     * @param quoteId
     *            id of saved quote
     * @return ShipmentDTO with mark about its saved quote age
     */
    @RequestMapping(value = "/{propositionId}", method = RequestMethod.GET)
    @ResponseBody
    public SavedShipmentLoadDTO getById(@QueryParam("customerId") Long customerId,
            @PathVariable("propositionId") Long quoteId) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.QUOTES_VIEW.name());

        SavedQuoteEntity savedQuote = savedQuoteService.getSavedQuoteById(quoteId);

        ShipmentDTO savedQuoteDTO = savedQuoteDTOBuilder.buildDTO(savedQuote);

        SavedShipmentLoadDTO dto = new SavedShipmentLoadDTO();
        dto.setShipmentDTO(savedQuoteDTO);
        if (!SecurityUtils.isPlsUser()) { // customer user can't see carrier cost detail items
            dto.getShipmentDTO().getSelectedProposition().getCostDetailItems()
                    .removeIf(costDetailItem -> costDetailItem.getCostDetailOwner() == CostDetailOwner.C);
        }
        SavedQuoteExpirationResolution resolution = SavedQuoteExpirationResolution.NORMAL;
        if (ShipmentProposalUtils.isSavedQuoteExpired(savedQuoteDTO.getCreatedDate(),
                savedQuoteDTO.getFinishOrder().getPickupDate())) {
            resolution = SavedQuoteExpirationResolution.EXPIRED;
        }
        // TODO check if carrier is still available and cost is not changed else { resolution =
        // SavedQuoteExpirationResolution.UNAVAILABLE; }

        dto.setResolution(resolution);
        if (savedQuote.getCustomer() != null) {
            dto.setCustomerStatusReason(savedQuote.getCustomer().getStatusReason());
        }
        return dto;
    }

    /**
     * Returns {@link ShipmentGridTooltipDTO} for the saved quote with the specified id.
     *
     * @param customerId
     *            id of customer
     * @param quoteId
     *            id of saved quote
     * @return tooltip info
     */
    @RequestMapping(value = "/{propositionId}/details", method = RequestMethod.GET)
    @ResponseBody
    public ShipmentGridTooltipDTO getDetails(@QueryParam("customerId") Long customerId,
            @PathVariable("propositionId") Long quoteId) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.QUOTES_VIEW.name());

        SavedQuoteEntity savedQuote = savedQuoteService.getSavedQuoteById(quoteId);
        ShipmentGridTooltipDTO dto = savedQuoteTooltipDTOBuilder.buildDTO(savedQuote);
        if (SecurityUtils.isPlsUser()) {
            savedQuoteTooltipDTOBuilder.fillPlsUserRelatedData(dto, savedQuote);
        }
        return dto;
    }

    /**
     * Returns all saved quotes for specified customer within the specified date range.
     *
     * @param customerId
     *            id of customer
     * @param fromDate
     *            from date
     * @param toDate
     *            to date
     * @return list of saved quotes
     *
     * @throws ApplicationException
     *             if anything will happen.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<SavedQuoteListItemDTO> get(@QueryParam("customerId") Long customerId,
            @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) throws ApplicationException {
        userPermissionsService.checkCapability(Capabilities.QUOTES_VIEW.name());
        Date toDt = isNotBlank(toDate) ? DateUtils.getFromDate(DateRange.DEFAULT, toDate) : new Date();
        Date fromDt = isNotBlank(fromDate) ? DateUtils.getFromDate(DateRange.DEFAULT, fromDate) : getDefaultFromDate();
        List<SavedQuoteListItemDTO> quotes = savedQuoteListItemDTOBuilder
                .buildList(savedQuoteService.findSavedQuotes(customerId, fromDt, toDt));
        if (!SecurityUtils.isPlsUser()) { // customer user can't see carrier cost
            for (SavedQuoteListItemDTO dto : quotes) {
                dto.setCarrierCost(null);
            }
        }
        return quotes;
    }

    private Date getDefaultFromDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -MAX_HISTORY_RANGE_IN_DAYS);
        return cal.getTime();
    }

    /**
     * Saves quote.
     *
     * @param customerId
     *            id of customer
     * @param quote
     *            quote to save
     * @param session
     *            Spring session
     * @return saved quote
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ShipmentDTO save(@QueryParam("customerId") Long customerId, @RequestBody ShipmentDTO quote,
            HttpSession session) {
        if (customerId == null) {
            userPermissionsService.checkCapability(Capabilities.QUOTES_VIEW.name());
        } else {
            userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.QUOTES_VIEW.name());
        }

        SavedQuoteEntity savedQuote = savedQuoteDTOBuilder.buildEntity(quote);

        ShipmentProposalBO proposal;
        if (SecurityUtils.isPlsUser()) { // get cost details from UI for PLS User
            proposal = quote.getSelectedProposition();
            proposal = ShipmentProposalUtils.filterGuaranteedOptions(proposal, quote.getGuaranteedBy());
        } else {
            proposal = ShipmentProposalUtils.getProposalFromSessionForShipment(session, quote.getGuid(),
                    quote.getSelectedProposition().getGuid(), quote.getGuaranteedBy());
        }
        savedQuoteService.saveQuote(savedQuote, customerId, proposal);
        savePricingProposals(quote, proposal.getCarrier().getId(), savedQuote.getId());
        return savedQuoteDTOBuilder.buildDTO(savedQuote);
    }

    /**
     * Build and save the entity classes containing the pricing proposals data.
     * 
     * @param quote
     *            - Shipment DTO.
     * @param selectedCarrierId
     *            - Id of the carrier with whom the load was booked.
     * @param savedQuoteId
     *            - Id of the saved quote.
     */
    private void savePricingProposals(ShipmentDTO quote, Long selectedCarrierId, Long savedQuoteId) {
        List<LtlPricingProposalsEntity> proposalEntities = PRICING_PROPOSAL_BUILDER.buildPricingProposals(quote,
                selectedCarrierId);
        for (LtlPricingProposalsEntity ltlPricingProposalsEntity : proposalEntities) {
            ltlPricingProposalsEntity.setQuoteId(savedQuoteId);
        }
        pricingProposalService.savePricingProposals(proposalEntities);
    }

    /**
     * Returns list of load id's associated with the saved quote.
     *
     * @param quoteId
     *            id of the saved quote.
     * @return string containing list of load id's.
     *
     * @throws ApplicationException
     *             if anything will happen.
     */
    @RequestMapping(value = "/{propositionId}/getListOfLoadIds", method = RequestMethod.GET)
    @ResponseBody
    public List<Long> getListOfLoadIds(@PathVariable("propositionId") Long quoteId) throws ApplicationException {
        return savedQuoteService.getListOfLoadIds(quoteId);
    }

}
