package com.pls.restful.address;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.dao.BillToRequiredFieldDao;
import com.pls.core.dao.impl.BillToThresholdSettingsDAOImpl;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.domain.organization.BillToThresholdSettingsEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.BillToInvoiceService;
import com.pls.core.service.CustomerService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.address.BillToService;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;
import com.pls.documentmanagement.service.RequiredDocumentService;
import com.pls.dto.address.BillToDTO;
import com.pls.dtobuilder.address.BillToDTOBuilder;
import com.pls.dtobuilder.address.RequiredDocumentDTOBuilder;
import com.pls.shipment.service.ShipmentDocumentService;

/**
 * Rest resource that process requests for Bill To addresses.
 * 
 * @author Denis Zhupinsky (Team International)
 */

@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/user/{uid}/bill_to")
public class BillToResource {

    private final BillToDTOBuilder billToDTOBuilder = new BillToDTOBuilder(new BillToDTOBuilder.DataProvider() {
        @Override
        public BillToEntity getEntity(Long id) {
            return  customerService.getBillTo(id);
        }

        @Override
        public InvoiceSettingsEntity getInvoicePreferences(Long id) {
            return billToInvoiceService.getInvoiceSettings(id);
        }

        @Override
        public List<RequiredDocumentEntity> getRequiredDocuments(Long billToId) {
            return requiredDocumentService.getRequiredDocumentsOfShipmentTypes(billToId);
        }

        @Override
        public BillToRequiredFieldEntity getRequiredFieldById(Long id) {
            return billToRequiredFieldDao.find(id);
        }

        @Override
        public BillToThresholdSettingsEntity getThresholdSettings(Long id) {
            return billToThresholdSettingsDAO.find(id);
        }

        @Override
        public BillToDefaultValuesEntity getDefaultValues(Long id) {
            return billToService.getDefaultEntityById(id);
        };
    });

    private final RequiredDocumentDTOBuilder requiredDocumentDTOBuilder =
            new RequiredDocumentDTOBuilder(new RequiredDocumentDTOBuilder.DataProvider() {
                @Override
                public RequiredDocumentEntity getDocumentById(Long id) {
                    return requiredDocumentService.getRequiredDocument(id);
                }

                @Override
                public LoadDocumentTypeEntity getDocumentTypeByDocTypeString(String documentType) {
                    return shipmentDocumentService.getDocumentTypeByStringName(documentType);
                }
            });

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BillToService billToService;

    @Autowired
    private BillToInvoiceService billToInvoiceService;

    @Autowired
    private RequiredDocumentService requiredDocumentService;

    @Autowired
    private ShipmentDocumentService shipmentDocumentService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private BillToRequiredFieldDao billToRequiredFieldDao;

    @Autowired
    private BillToThresholdSettingsDAOImpl billToThresholdSettingsDAO;

    /**
     * Get Bill to by id.
     * 
     * @param customerId
     *            id of customer
     * @param billToId
     *            id of bill to address
     * @return Bill To address
     * @throws ApplicationException
     *             if specified incorrect customer in URL
     */
    @RequestMapping(value = "/{billToId}", method = RequestMethod.GET)
    @ResponseBody
    public BillToDTO getBillToById(@PathVariable("customerId") long customerId, @PathVariable("billToId") long billToId)
            throws ApplicationException {
            userPermissionsService.checkOrganization(customerId);
        BillToEntity billToEntity = customerService.getBillTo(billToId);
        if (ObjectUtils.notEqual(customerId, billToEntity.getOrganization().getId())) {
            throw new ApplicationException("Incorrect Customer");
        }
        return billToDTOBuilder.buildDTO(billToEntity);
    }

    /**
     * Save Bill To address for specific customer.
     * 
     * @param customerId
     *            id of customer
     * @param userId
     *            id of user. Isn't used.
     * @param dto
     *            bill to dto
     * @return response entity
     * @throws ApplicationException
     *             if changes can't be sync'ed with finance system
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> save(@PathVariable("customerId") long customerId, @PathVariable("uid") long userId, @RequestBody BillToDTO dto)
            throws ApplicationException {
        OrganizationEntity organization = customerService.findCustomerById(customerId);
        BillToEntity billTo = billToDTOBuilder.buildEntity(dto);
        billTo.setOrganization(organization);
        customerService.saveBillTo(billTo);

        if (dto.getInvoicePreferences() != null && dto.getInvoicePreferences().getRequiredDocuments() != null) {
            List<RequiredDocumentEntity> requiredDocumentEntities =
                    requiredDocumentDTOBuilder.buildEntityList(dto.getInvoicePreferences().getRequiredDocuments());
            requiredDocumentService.saveRequiredDocuments(requiredDocumentEntities, billTo.getId());
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Validates bill to address name duplication.
     * 
     * @param name
     *            - name to be validated
     * @param orgId
     *            - id of organization.
     * @return - true if specified name already exists and false otherwise
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean validateAddressNameDuplication(@RequestParam("nameToBeValidated") String name,
            @PathVariable("customerId") Long orgId) {
        return billToService.validateDuplicateName(name, orgId);
    }

    /**
     * Returns short list of Bill To by specified orgId.
     * 
     * @param orgId - id of organization.
     * @return {@link KeyValueBO}.
     */
    @RequestMapping(value = "/keyValues", method = RequestMethod.GET)
    @ResponseBody
    public List<KeyValueBO> getBillToKeyValueByOrgId(@PathVariable("customerId") Long orgId) {
        return billToService.getIdAndNameByOrgId(orgId);
    }

    /**
     * Returns email list for bill to if present in invoice preferences.
     * 
     * @param billToIds
     *            list of Bill To id.
     * @return {@link KeyValueBO} where key > 0 if email should be sent for Bill To, value is email list.
     */
    @RequestMapping(value = "/emails", method = RequestMethod.GET)
    @ResponseBody
    public List<KeyValueBO> getBillToEmails(@RequestParam("ids") List<Long> billToIds) {
        return billToService.getBillToEmails(billToIds);
    }

    /**
     * Lists user BILL TO address book records.
     *
     * @param customerId
     *            id of customer
     * @return list of bill tos
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<BillToDTO> listUserBillToAddresses(@PathVariable("customerId") Long customerId) {
        userPermissionsService.checkCapability(Capabilities.VIEW_ACTIVE_CUSTOMER_PROFILE.name(), Capabilities.CUSTOMER_PROFILE_VIEW.name());
            userPermissionsService.checkOrganization(customerId);
        return billToDTOBuilder.buildList(billToService.getBillToAddresses(customerId));
        }

}
