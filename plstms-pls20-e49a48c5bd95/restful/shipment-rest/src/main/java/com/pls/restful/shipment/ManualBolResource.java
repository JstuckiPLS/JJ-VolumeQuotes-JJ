package com.pls.restful.shipment;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.CarrierService;
import com.pls.core.service.CustomerService;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.FreightBillPayToService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.address.BillToService;
import com.pls.core.shared.ResponseVO;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.dto.shipment.ManualBolDTO;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.dtobuilder.shipment.ManualBolDTOBuilder;
import com.pls.dtobuilder.shipment.ManualBolDTOBuilder.DataProvider;
import com.pls.dtobuilder.shipment.ManualBolGridTooltipDTOBuilder;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.service.ManualBolDocumentService;
import com.pls.shipment.service.ManualBolService;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * Manual Bol REST service.
 * 
 * @author Alexander Nalapko
 *
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/manualbol")
public class ManualBolResource {

    @Autowired
    private ManualBolService service;
    @Autowired
    private AddressService addressService;
    @Autowired
    private BillToService billToService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private ManualBolDocumentService manualBolDocumentService;
    @Autowired
    private FreightBillPayToService freightBillPayToService;
    @Autowired
    private UserPermissionsService userPermissionsService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DBUtilityService dbUtilityService;
    @Autowired
    private PackageTypeDictionaryService packageTypeDictionaryService;

    private ManualBolGridTooltipDTOBuilder tooltipBuilder = new ManualBolGridTooltipDTOBuilder();

    private ManualBolDTOBuilder manualBolDTOBuilder = new ManualBolDTOBuilder(new DataProvider() {

        @Override
        public CarrierEntity getCarrier(Long id) {
            return carrierService.findCarrierById(id);
        }

        @Override
        public AddressEntity getAddress(Long id) {
            return addressService.getAddressEntityById(id);
        }

        @Override
        public BillToEntity getBillTo(Long id) {
            return billToService.getBillTo(id);
        }

        @Override
        public ManualBolEntity getManualBol(Long id) {
            return service.find(id);
        }

        @Override
        public FreightBillPayToEntity getDefaultFreightBillPayTo() {
            return freightBillPayToService.getDefaultFreightBillPayTo();
        }

        @Override
        public PackageTypeEntity findPackageType(String id) {
            return packageTypeDictionaryService.getById(id);
        }

        @Override
        public FreightBillPayToEntity getFreightBillPayTo(Long id) {
            return freightBillPayToService.getFreightBillPayTo(id);
        }

        @Override
        public Boolean isPrintBarcode(Long customerId) {
            return customerService.isPrintBarcode(customerId);
        }
    });

    /**
     * Method saves manual BOL.
     * 
     * @param customerId
     *            id of customer
     * @param dto
     *            ManualBolDTO
     * @param storedBolId
     *            id of temporary stored BOL in binary data storage
     * @param storedLabelId
     *            id of temporary stored Shipping Label in binary data storage
     * @return single ManualBolDTO.
     * 
     * @throws ApplicationException
     *             In case of exception.
     */
    @RequestMapping(method = RequestMethod.POST)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseBody
    public ManualBolDTO saveOrUpdate(@PathVariable("customerId") Long customerId, @RequestBody ManualBolDTO dto,
            @RequestParam(value = "storedBolId", required = false) Long storedBolId,
            @RequestParam(value = "storedLabelId", required = false) Long storedLabelId) throws ApplicationException {
        userPermissionsService.checkCapabilityAndOrganization(customerId,
                 Capabilities.CREATE_MANUAL_BOL.name(), Capabilities.EDIT_MANUAL_BOL.name());
        validateCustomer(customerId, dto.getOrganizationId(), dto.getId() == null);

        dbUtilityService.startCommitMode();

        if (isRequiredAddressPhoneMissing(dto)) {
            throw new ApplicationException("Please fill phone number in Address Information section.");
        }

        ManualBolEntity entity = service.saveOrUpdate(manualBolDTOBuilder.buildEntity(dto));

        manualBolDocumentService.createShippingLabelDocument(entity);
        manualBolDocumentService.createBolDocument(entity, false);

        if (storedBolId != null) {
            documentService.deleteTempDocument(storedBolId);
        }
        if (storedLabelId != null) {
            documentService.deleteTempDocument(storedLabelId);
        }

        dbUtilityService.flushSession();

        return manualBolDTOBuilder.buildDTO(entity);
    }

    /**
     * Returns {@link ManualBolDTO} by manualBolId and customerId.
     * 
     * @param customerId - Customer Id
     * @param manualBolId - manual BOL Id
     * @return {@link ManualBolDTO}
     */
    @RequestMapping(value = "/{manualBolId}", method = RequestMethod.GET)
    @ResponseBody
    public ManualBolDTO getManualBol(@PathVariable("customerId") Long customerId, @PathVariable("manualBolId") Long manualBolId) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.CREATE_MANUAL_BOL.name(),
                Capabilities.EDIT_MANUAL_BOL.name(), Capabilities.VIEW_MANUAL_BOL.name());
        return manualBolDTOBuilder.buildDTO(service.find(manualBolId));
    }

    /**
     * Get tooltip data for manual BOL grids by id.
     * 
     * @param customerId
     *            id of customer
     * @param manualBolId
     *            manual BOL id to find
     * @return shipment data object
     */
    @RequestMapping(value = "/{manualBolId}/tooltip", method = RequestMethod.GET)
    @ResponseBody
    public ShipmentGridTooltipDTO getShipmentTooltipData(@PathVariable("customerId") Long customerId, @PathVariable("manualBolId") long manualBolId) {
       return tooltipBuilder.buildDTO(service.find(manualBolId));
    }

    /**
     * Cancel BOL by id.
     * 
     * @param customerId
     *            Customer Id
     * @param manualBolId
     *             manual BOL id
     * @return true if BOL been cancelled.
     * 
     * @throws EntityNotFoundException
     *            - if BOL with <id> not found
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/cancel/{manualBolId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVO cancelShipment(@PathVariable("customerId") Long customerId, @PathVariable("manualBolId") Long manualBolId)
            throws EntityNotFoundException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.CANCEL_MANUAL_BOL.name());
        return new ResponseVO(service.cancel(manualBolId));
    }

    private void validateCustomer(Long pathParamCustomerId, Long loadCustomerId, boolean newLoad) throws ApplicationException {
        if (ObjectUtils.notEqual(pathParamCustomerId, loadCustomerId)) {
            throw new ApplicationException("Incorrect Request");
        }
        if (newLoad && !customerService.isActiveCustomer(pathParamCustomerId)) {
            throw new ApplicationException("You can not update or save data for inactive customer");
        }
    }

    private boolean isRequiredAddressPhoneMissing(ManualBolDTO dto) {
        return userPermissionsService.hasCapability(Capabilities.REQUIRE_PHONE_NUMBERS_FOR_ORDERS.name())
                && (dto.getOrigin().getPhone() == null
                        || dto.getOrigin().getPhone().getAreaCode() == null
                        || dto.getOrigin().getPhone().getCountryCode() == null
                        || dto.getOrigin().getPhone().getNumber() == null

                        || dto.getDestination().getPhone() == null
                        || dto.getDestination().getPhone().getAreaCode() == null
                        || dto.getDestination().getPhone().getCountryCode() == null
                        || dto.getDestination().getPhone().getNumber() == null);
    }
}
