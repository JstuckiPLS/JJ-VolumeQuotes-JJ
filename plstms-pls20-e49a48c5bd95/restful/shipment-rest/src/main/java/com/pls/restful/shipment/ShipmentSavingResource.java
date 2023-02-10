package com.pls.restful.shipment;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.CustomerService;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.UploadedDocumentDTO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.quote.service.SavedQuoteService;
import com.pls.restful.util.ShipmentProposalUtils;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LtlPricingProposalsEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.PricingProposalService;
import com.pls.shipment.service.ShipmentDocumentService;
import com.pls.shipment.service.ShipmentNoteService;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.impl.BillingAuditService;

/**
 * Shipment related REST service.
 * 
 * @author Gleb Zgonikov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/shipment")
public class ShipmentSavingResource {

    @Autowired
    private DBUtilityService dbUtilityService;
    @Autowired
    private SavedQuoteService savedQuoteService;
    @Autowired
    private ShipmentDocumentService shipmentDocumentService;
    @Autowired
    private DocumentService documentService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShipmentNoteService shipmentNoteService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private BillingAuditService billingAuditService;
    @Autowired
    private CarrierInvoiceService carrierInvoiceService;

    @Autowired
    private ShipmentSavingService shipmentSavingService;
    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private PricingProposalService pricingProposalService;

    @Autowired
    private ShipmentBuilderHelper shipmentBuilder;

    /**
     * Update shipment with final data when 'Book It' action performed. Change load status to 'Pending Award'.
     * 
     * @param customerId
     *            id of customer
     * @param dto
     *            LTL shipment entity to be updated
     * 
     * @param storedBolId
     *            id of temporary stored BOL in binary data storage
     * @param session
     *            Spring session
     * @return single LtlShipmentEntity.
     * 
     * @throws ApplicationException
     *             if update shipment of inactive customer.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/bookIt", method = RequestMethod.PUT)
    @ResponseBody
    public ShipmentDTO bookShipment(@PathVariable("customerId") Long customerId, @RequestBody ShipmentDTO dto,
            @RequestParam(value = "storedBolId", required = false) Long storedBolId, HttpSession session) throws ApplicationException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.QUOTES_VIEW.name());

        return theBookShipment(customerId, dto, storedBolId, session);
    }

    /**
     * Update shipment entry with final data when 'Book It' action performed. Change load status to 'Pending
     * Award'.
     * 
     * @param customerId
     *            id of customer
     * @param dto
     *            LTL shipment entity to be updated
     * 
     * @param storedBolId
     *            id of temporary stored BOL in binary data storage
     * @param session
     *            Spring session
     * @return single LtlShipmentEntity.
     * 
     * @throws ApplicationException
     *             if update shipment of inactive customer.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/bookShipmentEntry", method = RequestMethod.PUT)
    @ResponseBody
    public ShipmentDTO bookShipmentEntry(@PathVariable("customerId") Long customerId, @RequestBody ShipmentDTO dto,
            @RequestParam(value = "storedBolId", required = false) Long storedBolId, HttpSession session) throws ApplicationException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.ADD_SHIPMENT_ENTRY.name());
        if (isRequiredAddressPhoneMissing(dto)) {
            throw new ApplicationException("Please fill phone number in Address Information section.");
        }
        return theBookShipment(customerId, dto, storedBolId, session);
    }

    /**
     * Method saves sales order.
     * 
     * @param customerId
     *            id of customer
     * @param dto
     *            LTL shipment entity to be updated
     * @param hideCreatedTime
     *            display shipment created time.
     * @return {@link ShipmentDTO}
     * @throws ApplicationException
     *             if update shipment of inactive customer.
     *             - ValidationException when validation checks fail.
     *             - EntityNotFoundException if temporary stored document were not found.
     *             - DocumentSaveException if temporary stored document cannot be saved as permanent.
     *             - EdiProcessingException if edi processing fails
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/saveSalesOrder", method = RequestMethod.POST)
    @ResponseBody
    public ShipmentDTO saveSalesOrder(@PathVariable("customerId") Long customerId, @RequestBody ShipmentDTO dto,
            @RequestParam(value = "hideCreatedTime", required = false, defaultValue = "false") Boolean hideCreatedTime)
            throws ApplicationException {
        if (!(SecurityUtils.isPlsUser() || SecurityUtils.getCapabilities().contains(Capabilities.BOARD_CAN_EDIT_SALES_ORDER.name()))) {
            throw new AccessDeniedException("You aren't PLS user and don't have permissions to fulfill this request.");
        }
        validateCustomer(customerId, dto.getOrganizationId(), dto.getId() == null);

        LoadEntity load = shipmentBuilder.getShipmentDTOBuilder().buildEntity(dto);
        if(load.getId() == null) {
            shipmentService.addOrganizationNotifications(load);
        }

        ShipmentFinancialStatus previousFinStatus = load.getFinalizationStatus();
        ShipmentProposalBO proposal = dto.getSelectedProposition();
        proposal = ShipmentProposalUtils.filterGuaranteedOptions(proposal, dto.getGuaranteedBy());
        updateShipmentFinancialStatus(load);
        dbUtilityService.startCommitMode();

        load = shipmentSavingService.save(load, proposal, SecurityUtils.getCurrentPersonId(), SecurityUtils.getCurrentPersonId());

        documentService.deleteDocuments(dto.getRemovedDocumentsIds());

        for (UploadedDocumentDTO uploadedDocument : dto.getUploadedDocuments()) {
            documentService.moveAndSaveTempDocPermanently(uploadedDocument.getId(), load.getId(), uploadedDocument.getDocType());
        }

        shipmentDocumentService.generateShipmentDocuments(dto.getRegeneratedDocTypes(), load, hideCreatedTime, SecurityUtils.getCurrentPersonId());

        shipmentNoteService.updateNotes(load.getId(), shipmentBuilder.getShipmentNoteDTOBuilder().buildEntityList(dto.getNotes()));
        dbUtilityService.flushSession();
        shipmentService.checkPaperworkRequiredForCustomerInvoice(load);
        if (dto.getId() == null && dto.getMatchedVendorBillId() != null) {
            carrierInvoiceService.match(dto.getMatchedVendorBillId(), load.getId());
        } else {
            carrierInvoiceService.updateFreightBillDate(load);
            billingAuditService.updateBillingAuditReasonForLoad(load, previousFinStatus);
        }
        return shipmentBuilder.getShipmentDTOBuilder().buildDTO(load);
    }

    /**
     * Changed finance status to billing hold if necessary.
     * 
     * @param load
     */
    private void updateShipmentFinancialStatus(LoadEntity load) {
        if (ShipmentFinancialStatus.NONE == load.getFinalizationStatus()
                && ShipmentStatus.DELIVERED == load.getStatus()
                && load.getVendorBillDetails().getCarrierInvoiceDetails() != null
                && load.getVendorBillDetails().getCarrierInvoiceDetails().stream().anyMatch(s -> Status.ACTIVE == s.getStatus())
                && load.getDestination().getDeparture() != null
                && (new Date().getTime() - load.getDestination().getDeparture().getTime()) > TimeUnit.DAYS.toMillis(3)) {
            load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        }
    }

    /**
     * Build and save the entity classes containing the pricing proposals data.
     * 
     * @param dto
     *            - Shipment DTO.
     * @param load
     *            - entity containing the booked load details.
     * @param selectedCarrierId
     *            - Id of the carrier with whom the load was booked.
     */
    private void savePricingProposals(ShipmentDTO dto, LoadEntity load, Long selectedCarrierId) {
        if (dto.getQuoteId() == null) {
            List<LtlPricingProposalsEntity> proposalEntities = shipmentBuilder.getLtlPricingProposalBuilder()
                    .buildPricingProposals(dto, selectedCarrierId);
            for (LtlPricingProposalsEntity ltlPricingProposalsEntity : proposalEntities) {
                ltlPricingProposalsEntity.setLoadId(load.getId());
            }
            pricingProposalService.savePricingProposals(proposalEntities);
        } else {
            pricingProposalService.updateLoadId(load.getId(), dto.getQuoteId());
        }
        pricingProposalService.createPricingProposalMaterials(load);
    }

    private boolean isRequiredAddressPhoneMissing(ShipmentDTO dto) {
        return userPermissionsService.hasCapability(Capabilities.REQUIRE_PHONE_NUMBERS_FOR_ORDERS.name())
                && (dto.getOriginDetails().getAddress().getPhone() == null
                        || dto.getOriginDetails().getAddress().getPhone().getAreaCode() == null
                        || dto.getOriginDetails().getAddress().getPhone().getCountryCode() == null
                        || dto.getOriginDetails().getAddress().getPhone().getNumber() == null

                        || dto.getDestinationDetails().getAddress().getPhone() == null
                        || dto.getDestinationDetails().getAddress().getPhone().getAreaCode() == null
                        || dto.getDestinationDetails().getAddress().getPhone().getCountryCode() == null
                        || dto.getDestinationDetails().getAddress().getPhone().getNumber() == null);
    }

    private void validateCustomer(Long pathParamCustomerId, Long loadCustomerId, boolean newLoad)
            throws ApplicationException {
        if (ObjectUtils.notEqual(pathParamCustomerId, loadCustomerId)) {
            throw new ApplicationException("Incorrect Request");
        }
        userPermissionsService.checkOrganization(loadCustomerId);
        if (newLoad && !customerService.isActiveCustomer(pathParamCustomerId)) {
            throw new ApplicationException("You can not update or save data for inactive customer");
        }
    }

    private ShipmentDTO theBookShipment(Long customerId, ShipmentDTO dto, Long storedBolId, HttpSession session)
            throws ApplicationException {
        validateCustomer(customerId, dto.getOrganizationId(), dto.getId() == null);

        dbUtilityService.startCommitMode();

        LoadEntity load = shipmentBuilder.getShipmentDTOBuilder().buildEntity(dto);
        if(load.getId() == null) {
            shipmentService.addOrganizationNotifications(load);
        }
        ShipmentProposalBO proposal = buildProposal(dto, session, load);
        if (proposal != null && proposal.getPricingDetails() == null) {
            if (dto.getQuoteId() != null) {
                proposal.setPricingDetails(shipmentBuilder.getSavedQuotePricDtlsDTOBuilder()
                        .buildDTO(savedQuoteService.getSavedQuotePricDtls(dto.getQuoteId())));
            } else {
                proposal.setPricingDetails(shipmentBuilder.getPricingDetailItemDTOBuilder()
                        .buildDTO(shipmentService.getShipmentPricingDetails(load.getId())));
            }
        }

        long userId = SecurityUtils.getCurrentPersonId();
        boolean autoDispatch = userPermissionsService.hasCapability(userId,
                Capabilities.ALLOW_SHIPMENT_AUTO_DISPATCH.name());
        load = shipmentSavingService.bookShipment(load, autoDispatch, userId, proposal,
                SecurityUtils.getCurrentPersonId());
        shipmentDocumentService.generateShipmentDocuments(Collections.<DocumentTypes>emptySet(), load, false, SecurityUtils.getCurrentPersonId());
        if (storedBolId != null) {
            documentService.deleteTempDocument(storedBolId);
        }
        dbUtilityService.flushSession();
        if (load.getCarrier() != null && dto.getSelectedProposition() != null
                && dto.getSelectedProposition().getGuid() != null) {
            savePricingProposals(dto, load, load.getCarrier().getId());
        }
        shipmentService.checkPaperworkRequiredForCustomerInvoice(load);
        return shipmentBuilder.getShipmentDTOBuilder().buildDTO(load);
    }

    private ShipmentProposalBO buildProposal(ShipmentDTO dto, HttpSession session, LoadEntity load) {
        ShipmentProposalBO proposal;
        if (userPermissionsService.hasCapability(Capabilities.EDIT_PLS_REVENUE.name())
                && userPermissionsService.hasCapability(Capabilities.EDIT_CARRIER_COST.name())) { // get cost details from UI for PLS User
            proposal = dto.getSelectedProposition();
            proposal = ShipmentProposalUtils.filterGuaranteedOptions(proposal, dto.getGuaranteedBy());
        } else if (dto.getQuoteId() != null) { // get cost details from quote
            SavedQuoteEntity savedQuote = savedQuoteService.getSavedQuoteById(dto.getQuoteId());
            ShipmentDTO savedQuoteDTO = shipmentBuilder.getSavedQuoteDTOBuilder().buildDTO(savedQuote);
            if (ShipmentProposalUtils.isSavedQuoteExpired(savedQuoteDTO.getCreatedDate(),
                    savedQuoteDTO.getFinishOrder().getPickupDate())) {
                proposal = ShipmentProposalUtils.getProposalFromSessionForShipment(session, dto.getGuid(),
                        dto.getSelectedProposition().getGuid(), dto.getGuaranteedBy()); // user must have selected new proposal
            } else {
                // TODO validate that carrier proposal hasn't changed
                proposal = savedQuoteDTO.getSelectedProposition();
                proposal.setGuid(UUID.randomUUID().toString());
                proposal = ShipmentProposalUtils.filterGuaranteedOptions(proposal, dto.getGuaranteedBy());
            }
        } else if (load.getId() != null && dto.getSelectedProposition().getGuid() == null) {
            // customer user edited shipment and didn't select new proposal (didn't open select carrier)
            proposal = null;
        } else { // get cost details from session
            proposal = ShipmentProposalUtils.getProposalFromSessionForShipment(session, dto.getGuid(),
                    dto.getSelectedProposition().getGuid(), dto.getGuaranteedBy());
        }
        return proposal;
    }
}
