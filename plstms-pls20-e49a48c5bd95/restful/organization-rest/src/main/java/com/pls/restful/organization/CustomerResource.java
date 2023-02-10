package com.pls.restful.organization;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.common.MimeTypes;
import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.CustomerService;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.OrganizationService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.ResponseVO;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.documentmanagement.service.RequiredDocumentService;
import com.pls.dto.organization.CustomerDTO;
import com.pls.dto.organization.CustomerInfoDTO;
import com.pls.dto.organization.CustomerListItemDTO;
import com.pls.dtobuilder.address.RequiredDocumentDTOBuilder;
import com.pls.dtobuilder.organization.CustomerDTOBuilder;
import com.pls.dtobuilder.organization.CustomerInfoDTOBuilder;
import com.pls.dtobuilder.organization.CustomerListItemDTOBuilder;
import com.pls.restful.util.ResourceParamsUtils;
import com.pls.shipment.service.ShipmentDocumentService;
import com.pls.user.service.UserService;

/**
 * Customer REST resource.
 * 
 * @author Denis Zhupinsky
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer")
public class CustomerResource {
    private static final CustomerListItemDTOBuilder CUSTOMER_LIST_ITEM_DTO_BUILDER = new CustomerListItemDTOBuilder();

    private static final CustomerInfoDTOBuilder CUSTOMER_INFO_DTO_BUILDER = new CustomerInfoDTOBuilder();

    private final CustomerDTOBuilder customerBuilder = new CustomerDTOBuilder(
            new CustomerDTOBuilder.CustomerDataProvider() {
                @Override
                public CustomerEntity getCustomerById(Long customerId) {
                    return customerService.findCustomerById(customerId);
                }

                @Override
                public UserEntity findByPersonId(Long personId) {
                    return userService.findByPersonId(personId);
                }

                @Override
                public UnitAndCostCenterCodesBO getUnitAndCostCenterCodes(Long orgId) {
                    return customerService.getUnitCostCenterCodes(orgId);
                }
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
    private RequiredDocumentService requiredDocumentService;

    @Autowired
    private ShipmentDocumentService shipmentDocumentService;

    @Autowired
    private DBUtilityService dbUtilityService;

    @Autowired
    private UserPermissionsService permissionsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    /**
     * Check if customer with following name already exists. Return existing status and if exists, will return
     * contact information of sales representative.
     * 
     * @param customerName
     *            name of customer to check
     * @return result of checking with sales representative contact information if exists
     */
    @RequestMapping(value = "/name/exist", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkCustomerName(@RequestParam("name") String customerName) {
        return customerService.checkCustomerNameExists(customerName);
    }

    /**
     * Check if customer with following federal tax id already exists.
     * 
     * @param federalTaxId
     *            federal tax id of customer to check
     * @return result of checking
     */
    @RequestMapping(value = "/federalTaxId/exist", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkFederalTaxIdExists(@RequestParam("id") String federalTaxId) {
        return customerService.checkFederalTaxIdExists(federalTaxId);
    }

    /**
     * Create customer.
     * 
     * @param customer
     *            customer object to create
     * @return short description of customer with create data
     * @throws ApplicationException
     *             when customer validation fails or information is not sent to finance.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customer) throws ApplicationException {
        permissionsService.checkCapability(Capabilities.CUSTOMER_PROFILE_VIEW.name());
        CustomerEntity customerEntity = customerBuilder.buildEntity(customer);
        if (customerEntity.getId() == null) {
            customerEntity = customerService.createCustomer(customerEntity);
            if (customerEntity.getBillToAddresses() != null && !customerEntity.getBillToAddresses().isEmpty()) {
                BillToEntity billToEntity = customerEntity.getBillToAddresses().iterator().next();
                if (customer.getBillTo().getInvoicePreferences() != null) {
                    List<RequiredDocumentEntity> requiredDocumentEntities = requiredDocumentDTOBuilder.buildEntityList(customer.getBillTo()
                            .getInvoicePreferences().getRequiredDocuments());
                    requiredDocumentService.saveRequiredDocuments(requiredDocumentEntities, billToEntity.getId());
                }
            }
        } else {
            dbUtilityService.startCommitMode();
            customerService.updateCustomer(customerEntity);
        }
        if (customer.getLogoId() != null) {
            saveCustomerLogo(customerEntity.getId(), customer.getLogoId());
        }
        dbUtilityService.flushSession();
        return customerBuilder.buildDTO(customerEntity);
    }

    private void saveCustomerLogo(Long customerId, Long logoId) throws DocumentReadException, EntityNotFoundException, DocumentSaveException {
        LoadDocumentEntity logo = documentService.getDocumentWithStream(logoId);
        LoadDocumentEntity document = new LoadDocumentEntity();
        String extension = FilenameUtils.getExtension(logo.getDocFileName());
        MimeTypes mimeType = ObjectUtils.defaultIfNull(MimeTypes.getByName(extension), MimeTypes.PNG);
        document.setFileType(mimeType);
        document.setDocumentType(DocumentTypes.UNKNOWN.getDbValue());
        documentService.saveDocument(document, logo.getStreamContent(), "orgLogo-" + customerId);
        organizationService.updateLogoForOrganization(customerId, document.getId());
        documentService.deleteTempDocument(logoId);
    }

    /**
     * Get customer by id.
     * 
     * @param customerId
     *            id of customer to get
     * @return customer profiler object
     * @throws EntityNotFoundException Customer was not found.
     */
    @RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public CustomerDTO findCustomerById(@PathVariable("customerId") Long customerId) throws EntityNotFoundException {
        permissionsService.checkCapability(Capabilities.VIEW_ACTIVE_CUSTOMER_PROFILE.name(),
                Capabilities.CUSTOMER_PROFILE_VIEW.name());
        permissionsService.checkOrganization(customerId);
        CustomerEntity customerEntity = customerService.findCustomerById(customerId);
        if (customerEntity != null) {
            return customerBuilder.buildDTO(customerEntity);
        } else {
            throw new EntityNotFoundException("Customer was not found");
        }
    }

    /**
     * Retrieves a list of all available Account Executives.
     *
     * @return {@link List} of {@link SimpleValue}.
     */
    @RequestMapping(value = "/accountExec", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleValue> getAccountExecutives() {
        return customerService.getAccountExecutives();
    }

    /**
     * Retrieves a list of available Account Executives filtered by name.
     *
     * @param filter
     *            - filter word
     * @return {@link List} of {@link SimpleValue}.
     */
    @RequestMapping(value = "/accountExecByFilter", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleValue> getAccountExecutivesByFilter(@RequestParam("filter") String filter) {
        return customerService.getAccountExecutivesByFilter(filter);
    }

    /**
     * Get customer CustomerInfoDTO by id.
     *
     * @param customerId
     *            id of customer to get
     * @return customer CustomerInfoDTO
     * @throws EntityNotFoundException Customer was not found.
     */
    @RequestMapping(value = "/customerLabel/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public CustomerInfoDTO getCustomerLabelById(@PathVariable("customerId") Long customerId) throws EntityNotFoundException {
        permissionsService.checkCapability(Capabilities.VIEW_ACTIVE_CUSTOMER_PROFILE.name(),
                Capabilities.CUSTOMER_PROFILE_VIEW.name());
        permissionsService.checkOrganization(customerId);

        CustomerEntity customerEntity = customerService.findCustomerById(customerId);
        if (customerEntity != null) {
            return CUSTOMER_INFO_DTO_BUILDER.buildDTO(customerEntity);
        } else {
            throw new EntityNotFoundException("Customer was not found");
        }
    }

    /**
     * Get credit limit for customer.
     * 
     * @param customerId
     *            id of customer
     * @return credit limit
     */
    @RequestMapping(value = "/{customerId}/creditLimit", method = RequestMethod.GET)
    @ResponseBody
    public Long getCreditLimit(@PathVariable("customerId") Long customerId) {
        permissionsService.checkCapability(Capabilities.VIEW_ACTIVE_CUSTOMER_PROFILE.name(), Capabilities.CUSTOMER_PROFILE_VIEW.name());
        Long currentCustomerId = customerId;
        if (SecurityUtils.isPlsUser()) {
            permissionsService.checkOrganization(customerId);
        } else {
            currentCustomerId = SecurityUtils.getParentOrganizationId();
        }
        return customerService.getCreditLimit(currentCustomerId);
    }

    /**
     * Retrieves list of customers by status and name for current user.
     * @param status - customer status
     * @param name - name
     * @param businessUnitName - network
     * @return list of customers by status and name for current user
     * @throws ApplicationException if the wrong Customer name of inappropriate wildcard pattern was entered
     */
    @RequestMapping(value = "/list/{status}", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerListItemDTO> getByStatusAndName(@PathVariable("status") String status,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "businessUnitName", required = false) String businessUnitName)
            throws ApplicationException {
        String nameQueryParam = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(name);
        List<CustomerListItemBO> customers = customerService.getByStatusAndName(
                OrganizationStatus.valueOf(status.toUpperCase()), nameQueryParam, businessUnitName);
        return CUSTOMER_LIST_ITEM_DTO_BUILDER.buildList(customers);
    }

    /**
     * Gets the current user settings param which specify sort order of Product List drop down widgets placed
     * anywhere in the app.
     * 
     * @param customerId
     *            id of customer to find it's ProductListPrimarySort value
     * @return sort value as one of {@link ProductListPrimarySort} enum values.
     */
    @RequestMapping(value = "/{customerId}/product-list-sort", method = RequestMethod.GET)
    @ResponseBody
    public ProductListPrimarySort getProductListPrimarySort(@PathVariable("customerId") Long customerId) {
        permissionsService.checkOrganization(customerId);

        ProductListPrimarySort domainSort = customerService.getProductListPrimarySort(customerId);
        return domainSort != null ? domainSort : ProductListPrimarySort.PRODUCT_DESCRIPTION;
    }

    /**
     * Find Customers by name for LookupValue field.
     * 
     * @param name
     *            customer name
     * @param limit
     *            page size
     * @param offset
     *            pages
     * @param status
     *            customer
     * @return list of carriers
     */
    @RequestMapping(value = "/idNameTuples", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleValue> searchCustomerByName(@RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "status", required = false, defaultValue = "false") Boolean status) {
        return customerService.getCustomerIdNameTuplesByName(name, limit, offset, status, SecurityUtils.getCurrentPersonId());
    }

    /**
     * Sets the current user settings param which specify sort order of Product List drop down widgets placed
     * anywhere in the app.
     * 
     * @param customerId
     *            id of customer organization
     * 
     * @param sort
     *            new sort param, one of {@link ProductListPrimarySort} enum values.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{customerId}/product-list-sort", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void setProductListPrimarySort(@PathVariable("customerId") Long customerId,
            @RequestBody String sort) {
        permissionsService.checkOrganization(customerId);
        ProductListPrimarySort domainSort = ProductListPrimarySort.valueOf(sort);
        customerService.setProductListPrimarySort(domainSort, customerId);
    }

    /**
     * Check if {@link CustomerEntity} with specified EDI Number already exists.
     * 
     * @param ediNumber
     *          EDI Number to check
     * @return <code>true</code> if EDI Number already exists, otherwise return <code>false</code>
     */
    @RequestMapping(value = "/ediNumber/exists", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkEDINumberExists(@RequestParam("ediNum") String ediNumber) {
        return customerService.checkEDINumberExists(ediNumber);
    }

    /**
     * Check if Credit Limit Validation is required for specified Customer.
     * 
     * @param customerId - Not <code>null</code>. {@link CustomerEntity#getId()}
     * @return <code>TRUE</code> if validation is required, otherwise returns <code>FALSE</code>
     */
    @RequestMapping(value = "/isCreditLimitRequired", method = RequestMethod.GET)
    @ResponseBody
    public boolean getCreditLimitRequired(@RequestParam("customerId") Long customerId) {
        return customerService.getCreditLimitRequited(customerId);
    }

    /**
     * Get Internal Note for customer.
     * 
     * @param customerId
     *            id of customer
     * @return Internal Note Text
     */
    @RequestMapping(value = "/{customerId}/internal-note", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getInternalNote(@PathVariable("customerId") Long customerId) {
        permissionsService.checkCapability(Capabilities.CAN_VIEW_INTERNAL_NOTES.name());
        return new ResponseVO(customerService.getInternalNote(customerId));
    }
}
