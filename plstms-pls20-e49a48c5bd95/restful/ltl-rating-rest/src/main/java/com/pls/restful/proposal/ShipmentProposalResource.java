package com.pls.restful.proposal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.FreightBillPayToService;
import com.pls.core.shared.AddressVO;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dtobuilder.FreightBillPayToDTOBuilder;
import com.pls.dtobuilder.pricing.LtlThirdPartyInfoToFreightBillPayToDTOBuilder;
import com.pls.dtobuilder.proposal.PricingProposalDTOBuilder;
import com.pls.extint.shared.MileageCalculatorType;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.ltlrating.service.LtlPricingThirdPartyInfoService;
import com.pls.ltlrating.service.LtlRatingEngineService;
import com.pls.ltlrating.service.TerminalService;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.GetTerminalsCO;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.TerminalsVO;
import com.pls.mileage.service.MileageService;
import com.pls.restful.util.ShipmentProposalUtils;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.service.AccessorialTypeService;
import com.pls.shipment.service.ManualBolService;
import com.pls.shipment.service.ShipmentService;

/**
 * Shipment proposal resource.
 * 
 * @author Gleb Zgonikov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/shipment/proposal")
public class ShipmentProposalResource {
    private static final PricingProposalDTOBuilder PROPOSAL_DTO_BUILDER = new PricingProposalDTOBuilder();
    private static final FreightBillPayToDTOBuilder FREIGHT_BILL_PAY_TO_DTO_BUILDER = new FreightBillPayToDTOBuilder();
    private static final LtlThirdPartyInfoToFreightBillPayToDTOBuilder TPI_FREIGHT_BILL_PAY_TO_DTO_BUILDER
            = new LtlThirdPartyInfoToFreightBillPayToDTOBuilder();

    @Autowired
    private AccessorialTypeService accessorialTypeService;

    @Autowired
    private LtlRatingEngineService ratingService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private LtlPricingThirdPartyInfoService ltlPricingThirdPartyInfoService;

    @Autowired
    private FreightBillPayToService freightBillPayToService;

    @Autowired
    private MileageService mileageService;

    @Autowired
    private ManualBolService manualBolService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<String> countriesCodesWithState = Arrays.asList("CAN", "MEX", "USA");

    /**
     * Method findShipmentPropositions is used to find all carriers and their conditions to ship given
     * shipment.
     * 
     * @param order
     *            of type GetOrderRatesCO
     * @param guid
     *            unique generated id of shipment. It doesn't need to be value from sequence.
     * @param session
     *            Spring session
     * @return list of carrier proposals for shipment
     * @throws Exception
     *             Application Exception
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/propositions", method = RequestMethod.POST)
    @ResponseBody
    public List<ShipmentProposalBO> findShipmentPropositions(@RequestBody GetOrderRatesCO order, @RequestParam String guid,
            HttpSession session) throws Exception {

        log.info(" ********* Calling ShipmentProposalResource.findShipmentPropositions from UI to get rates *********** ");

        List<LtlPricingResult> pricingResults = ratingService.getRates(order);
        List<ShipmentProposalBO> propositions = new ArrayList<>();

        if (pricingResults != null) {
            propositions.addAll(PROPOSAL_DTO_BUILDER.buildList(pricingResults));
        }

        Collections.sort(propositions, Comparator.comparing(ShipmentProposalBO::getTotalShipperAmt)
                .thenComparing(ShipmentProposalBO::getTotalCarrierAmt)
                .thenComparing(ShipmentProposalBO::getBlockedFrmBkng)
                .thenComparing(ShipmentProposalBO::getEstimatedTransitTime, Comparator.nullsLast(Comparator.naturalOrder())));

        ShipmentProposalUtils.saveShipmentPropositions(session, guid, propositions);

        return propositions;
    }

	@RequestMapping(value = "/accessorialTypes", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialTypeEntity> getAccessorialTypes() {
        return accessorialTypeService.getAvailableAccessorialTypes();
    }

    /**
     * Retrieves information about origin and destination terminals.
     * 
     * @param co - criteria object for terminals
     * @return value object holding origin/destination addresses and other info about terminals
     * @throws ApplicationException exception
     */
    @ResponseBody
    @RequestMapping(value = "/terminal", method = RequestMethod.POST)
    public TerminalsVO getTerminalInfo(@RequestBody GetTerminalsCO co) throws ApplicationException {
        return terminalService.getTerminalInformation(co);
    }

    /**
     * Retrieves information about origin and destination terminals.
     * 
     * @param id
     *            - shipment id
     * @return value object holding origin/destination addresses and other info about terminals
     * @throws ApplicationException exception
     */
    @ResponseBody
    @RequestMapping(value = "/terminal-info", method = RequestMethod.GET)
    public TerminalsVO getTerminalInfoByShipmentId(@RequestParam("shipmentId") Long id) throws ApplicationException {
        return terminalService.getTerminalInformation(buildTerminalCriteria(id));
    }
 
    /**
     * Retrieves information about origin and destination terminals.
     * 
     * @param id
     *            - manual BOL id
     * @return value object holding origin/destination addresses and other info about terminals
     * @throws ApplicationException exception
     */
    @ResponseBody
    @RequestMapping(value = "/manual-bol-terminal-info", method = RequestMethod.GET)
    public TerminalsVO getTerminalInfoByManualBolId(@RequestParam("manualBol") Long id) throws ApplicationException {
        return terminalService.getTerminalInformation(buildTerminalCriteriaByManualBol(id));
    }

    /**
     * Get Freight Bill Pay To by pricing profile id.
     * 
     * @param pricingProfileId
     *            id of pricing profile or <code>null</code>
     * @return found or default {@link FreightBillPayToDTO}.
     */
    @ResponseBody
    @RequestMapping(value = "/freightBillPayTo", method = RequestMethod.GET)
    public FreightBillPayToDTO getFreightBillPayTo(@RequestParam(value = "pricingProfileId", required = false) Long pricingProfileId) {
        if (pricingProfileId != null) {
            LtlPricingThirdPartyInfoEntity info = ltlPricingThirdPartyInfoService.getActiveByProfileId(pricingProfileId);
            if (info != null && info.getCompany() != null) {
                return TPI_FREIGHT_BILL_PAY_TO_DTO_BUILDER.buildDTO(info);
            } else {
                return null;
            }
        }
        return FREIGHT_BILL_PAY_TO_DTO_BUILDER.buildDTO(freightBillPayToService.getDefaultFreightBillPayTo());
    }

    /**
     * Get total miles for shipment.
     * 
     * @param co
     *            - criteria object for calculating mileage
     * @return total miles
     * @throws ApplicationException exception
     */
    @ResponseBody
    @RequestMapping(value = "/calculateMileage", method = RequestMethod.POST)
    public Integer getShipmentMileage(@RequestBody GetTerminalsCO co) throws ApplicationException {
        if (countriesCodesWithState.contains(co.getOriginAddress().getCountryCode())
                && countriesCodesWithState.contains(co.getDestinationAddress().getCountryCode())) {
            return mileageService.getMileage(co.getOriginAddress(), co.getDestinationAddress(), MileageCalculatorType.MILE_MAKER);
        }
        return 0;
    }

    private GetTerminalsCO buildTerminalCriteria(Long shipmentId) throws EntityNotFoundException {
        GetTerminalsCO criteria = new GetTerminalsCO();
        LoadEntity shipment = shipmentService.findById(shipmentId);

        LoadDetailsEntity origin = shipment.getOrigin();
        if (origin != null) {
            criteria.setOriginAddress(buildAddressVO(origin.getAddress()));
        }
        LoadDetailsEntity destination = shipment.getDestination();
        if (destination != null) {
            criteria.setDestinationAddress(buildAddressVO(destination.getAddress()));
        }
        criteria.setScac(shipment.getCarrier().getScac());
        return criteria;
    }

    private GetTerminalsCO buildTerminalCriteriaByManualBol(Long manualBol) throws EntityNotFoundException {
        GetTerminalsCO criteria = new GetTerminalsCO();
        ManualBolEntity bol = manualBolService.find(manualBol);
        if (bol.getOrigin() != null) {
            criteria.setOriginAddress(buildAddressVO(bol.getOrigin().getAddress()));
        }
        if (bol.getDestination() != null) {
            criteria.setDestinationAddress(buildAddressVO(bol.getDestination().getAddress()));
        }
        criteria.setScac(bol.getCarrier().getScac());
        criteria.setShipmentDate(bol.getPickupDate());
        return criteria;
    }

    private AddressVO buildAddressVO(AddressEntity address) {
        if (address == null) {
            return null;
        }

        AddressVO addressVO = new AddressVO();
        addressVO.setAddress1(address.getAddress1());
        addressVO.setAddress2(address.getAddress2());
        addressVO.setCity(address.getCity());
        addressVO.setStateCode(address.getStateCode());
        addressVO.setPostalCode(address.getZip());
        addressVO.setCountryCode(address.getState().getStatePK().getCountryCode());
        return addressVO;
    }
}
