package com.pls.core.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.CustomerUserDao;
import com.pls.core.dao.NetworkDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.dao.XmlDetailsAxDao;
import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.NetworkListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;
import com.pls.core.domain.bo.XmlDetailsAxBO;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.domain.xml.finance.customer.BillToAddress;
import com.pls.core.domain.xml.finance.customer.CustTable;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.CustomerService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.service.validation.BillToValidator;
import com.pls.core.service.validation.CustomerValidator;
import com.pls.core.service.validation.support.Validator;

/**
 * {@link com.pls.core.service.CustomerService} implementation.
 *
 * @author Denis Zhupinsky
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private static final int ACCOUNT_EXECUTIVE_LIST_COUNT = 10;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private CustomerUserDao customerUserDao;

    @Autowired
    private BillToDao billToDao;

    @Resource(type = CustomerValidator.class)
    private Validator<CustomerEntity> validator;

    @Resource(type = BillToValidator.class)
    private Validator<BillToEntity> billToValidator;

    @Autowired
    private NetworkDao networkDao;

    @Autowired
    private XmlDetailsAxDao xmlDetailsAxDao;

    @Autowired
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Override
    public boolean checkCustomerNameExists(String name) {
        return customerDao.checkCustomerNameExists(name);
    }

    @Override
    public boolean checkFederalTaxIdExists(String federalTaxId) {
        return customerDao.checkCustomerFederalTaxIdExists(federalTaxId);
    }

    @Override
    public CustomerEntity createCustomer(CustomerEntity customer) throws ApplicationException {
        validator.validate(customer);
        customerDao.persist(customer);

        addCustomerUser(customer, userInfoDao.getUserEntityById(SecurityUtils.getCurrentPersonId()));
        for (OrganizationLocationEntity location : customer.getLocations()) {
            if (location.getActiveAccountExecutive() != null
                    && !SecurityUtils.getCurrentPersonId().equals(location.getActiveAccountExecutive().getUser().getId())) {
                addCustomerUser(customer, userInfoDao.getUserEntityById(location.getActiveAccountExecutive().getUser().getId()));
            }
        }
        SecurityUtils.getOrganizations().add(customer.getId());
        saveBillTo(customer.getBillToAddresses().iterator().next());
        return customer;
    }

    @Override
    public CustomerEntity findCustomerById(Long customerId) {
        return customerDao.find(customerId);
    }

    @Override
    public CustomerEntity findCustomerByName(String name) {
        return customerDao.findCustomerByName(name);
    }

    @Override
    public List<SimpleValue> getAccountExecutivesByFilter(String filter) {
        return customerDao.findAccountExecutiveList(filter, ACCOUNT_EXECUTIVE_LIST_COUNT);
    }

    @Override
    public ProductListPrimarySort getProductListPrimarySort(Long orgId) {
        return customerDao.getProductListPrimarySort(orgId);
    }

    @Override
    public void setProductListPrimarySort(ProductListPrimarySort sort, Long orgId) {
        customerDao.updateProductListPrimarySort(orgId, sort);
    }

    @Override
    public CustomerEntity updateCustomer(CustomerEntity customer) throws ApplicationException {
        validator.validate(customer);
        boolean customerChanged = customerDao.isCustomerChangedForFinance(customer);
        customerDao.update(customer);
        if (customerChanged) {
            for (BillToEntity billTo : customer.getBillToAddresses()) {
                saveBillTo(billTo);
            }
        }
        return customer;
    }

    @Override
    public List<SimpleValue> getCustomerIdNameTuplesByName(String name, int limit, int offset, Boolean status, Long personId) {
        return customerDao.getCustomerIdNameTuplesByName(name, limit, offset, status, personId);
    }

    @Override
    public List<SimpleValue> getAccountExecutives() {
        return customerDao.getAccountExecutives();
    }

    private void addCustomerUser(CustomerEntity customer, UserEntity user) {
        if (user != null) {
            CustomerUserEntity customerUser = new CustomerUserEntity();
            customerUser.setCustomer(customer);
            customerUser.setUser(user);
            customerUserDao.persist(customerUser);
        }
    }

    @Override
    public List<CustomerListItemBO> getByStatusAndName(OrganizationStatus status, String name, String businessUnitName) {
        return customerDao.getByStatusAndName(SecurityUtils.getCurrentPersonId(), status, name, businessUnitName);
    }

    @Override
    public Boolean isActiveCustomer(Long customerId) {
        return customerDao.isActiveCustomer(customerId);
    }

    @Override
    public void saveBillTo(BillToEntity billTo) throws ApplicationException {
        billToValidator.validate(billTo);
        billTo.getBillingInvoiceNode().setNetworkId(billTo.getOrganization().getNetworkId());
        BillToEntity newEntity = billToDao.saveOrUpdate(billTo);

        XmlDetailsAxBO xmlDetailsAxBo = xmlDetailsAxDao.getDetails(newEntity.getId());

        if (xmlDetailsAxBo == null) {
            throw new ApplicationException("Could not retrieve the billing details given the following 'billToId': " + newEntity.getId());
        }

        try {
            SterlingIntegrationMessageBO sterlingMessage = new SterlingIntegrationMessageBO(getCustTable(xmlDetailsAxBo), EDIMessageType.CUSTOMER);
            sterlingMessageProducer.publishMessage(sterlingMessage);
        } catch (JMSException ex) {
            throw new InternalJmsCommunicationException("Exception occurred while publishing message to external integration message queue", ex);
        }
    }

    @Override
    public BillToEntity getBillTo(long billToId) {
        return billToDao.find(billToId);
    }

    @Override
    public BigDecimal getMarginTolerance(long customerId) {
        BigDecimal marginTolerance = new BigDecimal("5");
        CustomerEntity customer = customerDao.find(customerId);
        if (customer != null) {
            BigDecimal micAcceptMargin = getCustomerMinAcceptMargin(customer);
            if (micAcceptMargin != null) {
                marginTolerance = micAcceptMargin;
            }
        }
        return marginTolerance;
    }

    private BigDecimal getCustomerMinAcceptMargin(CustomerEntity customer) {
        BigDecimal micAcceptMargin = null;
        Set<OrganizationPricingEntity> pricingEntities = customer.getOrganizationsPricing();
        if (pricingEntities != null && !pricingEntities.isEmpty()) {
            for (OrganizationPricingEntity pricingEntity : pricingEntities) {
                if (pricingEntity != null && pricingEntity.getMinAcceptMargin() != null) {
                    micAcceptMargin = pricingEntity.getMinAcceptMargin();
                }
            }
        }
        return micAcceptMargin;
    }

    @Override
    public boolean checkEDINumberExists(String ediNumber) {
        return customerDao.checkEDINumberExists(ediNumber);
    }

    @Override
    public List<NetworkListItemBO> getAllNetworks() {
        return networkDao.getAllNetworks();
    }

    @Override
    public UnitAndCostCenterCodesBO getUnitCostCenterCodes(Long orgId) {
        return networkDao.getUnitAndCostCenterCodes(orgId);
    }

    private CustTable getCustTable(XmlDetailsAxBO xmlDetailsAxBO) {
        CustTable custTable = new CustTable();

        custTable.setOperation("CREATE");
        custTable.setAccountNumber(xmlDetailsAxBO.getAccountNum());
        custTable.setCurrency(xmlDetailsAxBO.getCurrency().toString());
        custTable.setCustomerGroup(xmlDetailsAxBO.getGroupCode());
        custTable.setBusinessUnit(xmlDetailsAxBO.getUnitCode());
        custTable.setCostCenter(xmlDetailsAxBO.getCostCenterCode());
        custTable.setDepartment("SL");
        custTable.setPersonId(SecurityUtils.getCurrentPersonId());
        custTable.setIdentificationNumber(xmlDetailsAxBO.getEmployerNum());
        custTable.setCustomerOrgId(xmlDetailsAxBO.getOrgId());
        custTable.setName(xmlDetailsAxBO.getOrgName());

        BillToAddress address = new BillToAddress();
        address.setBillToName(xmlDetailsAxBO.getBillToName());
        address.setAddress1(xmlDetailsAxBO.getAddress1());
        address.setAddress2(xmlDetailsAxBO.getAddress2());
        address.setCity(xmlDetailsAxBO.getCity());
        address.setState(xmlDetailsAxBO.getStateCode());
        address.setPostalCode(xmlDetailsAxBO.getPostalCode());
        address.setCountry(xmlDetailsAxBO.getCountryCode());
        custTable.setAddress(address);

        return custTable;
    }

    @Override
    public Long getCreditLimit(Long customerId) {
        return customerDao.getCreditLimit(customerId);
    }

    @Override
    public boolean getCreditLimitRequited(Long customerId) {
        return customerDao.getCreditLimitRequired(customerId);
    }

    @Override
    public String getInternalNote(Long customerId) {
        return customerDao.getInternalNote(customerId);
    }

    @Override
    public Boolean isPrintBarcode(Long customerId) {
        return customerDao.isPrintBarcode(customerId);
    }
}
