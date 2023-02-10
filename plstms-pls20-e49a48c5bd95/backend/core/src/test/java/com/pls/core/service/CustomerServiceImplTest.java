package com.pls.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.BillToDao;
import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.NetworkDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.dao.XmlDetailsAxDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.NetworkListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;
import com.pls.core.domain.bo.XmlDetailsAxBO;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillToThresholdSettingsEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.OrganizationFaxPhoneEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.organization.OrganizationVoicePhoneEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.xml.finance.customer.BillToAddress;
import com.pls.core.domain.xml.finance.customer.CustTable;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.CustomerServiceImpl;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.service.validation.support.Validator;

/**
 * Test cases to check core classes for authorization in test environment.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceImplTest {

    private static final long CURRENT_USER_ID = (long) (Math.random() * 100 + 1L);
    private static final Long CUSTOMER_ID = (long) (Math.random() * 100 + 202L);
    private static final Long NETWORK_ID = 7L;
    private static final long BILL_TO_ID = 1L;
    private static final String FILTER = "A";
    private static final String ANY_STRING = "Hacoona Matata";
    private static final Long ANY_LONG = (long) (Math.random() * 500);
    private static final String CUSTOMER_NUMBER = ((Double) Math.random()).toString();
    private static final Long EXPECTED_MARGIN = 5L;

    @InjectMocks
    private CustomerServiceImpl service;

    @Mock
    private CustomerDao dao;

    @Mock
    private OrganizationLocationEntity locations;

    @Mock
    private UserInfoDao userDao;

    @Mock
    private BillToDao billToDao;

    @Mock
    private NetworkDao networkDao;

    @Mock
    private CustomerService custService;

    @Mock
    private XmlDetailsAxDao xmlDetailsAxDao;

    @Mock
    private Validator<CustomerEntity> validator;

    @Mock
    private Validator<BillToEntity> billToValidator;

    @Mock
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Before
    public void init() {
        SecurityTestUtils.login("Test", CURRENT_USER_ID);
    }

    @Test
    public void shouldUpdateCustomer() throws ApplicationException {
        CustomerEntity expected = buildCustomerEntity();
        expected.setCreditLimit(ANY_LONG);
        BillToEntity billTo = expected.getBillToAddresses().iterator().next();
        Mockito.when(dao.isCustomerChangedForFinance(expected)).thenReturn(true);
        Mockito.when(billToDao.saveOrUpdate(billTo)).thenReturn(getBillTo());
        Mockito.when(xmlDetailsAxDao.getDetails(BILL_TO_ID)).thenReturn(getXmlDetailsAxBO());

        service.updateCustomer(expected);

        verify(billToValidator).validate(eq(expected.getBillTos().iterator().next()));
        verify(validator).validate(eq(expected));
        verify(dao).update(eq(expected));
        verify(dao).isCustomerChangedForFinance(expected);
    }

    @Test
    public void shouldUpdateCustomerModification() throws ApplicationException {
        CustomerEntity customer = buildCustomerEntity();

        service.updateCustomer(customer);

        verify(validator).validate(eq(customer));
        verify(dao).update(eq(customer));
    }

    @Test
    public void shouldFindCustomerById() throws EntityNotFoundException {
        CustomerEntity expectedResult = buildCustomerEntity();
        when(dao.find(CUSTOMER_ID)).thenReturn(expectedResult);

        CustomerEntity actualResult = service.findCustomerById(CUSTOMER_ID);
        assertNotNull(actualResult);
        verify(dao).find(CUSTOMER_ID);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldGetAccountExecutiveByFilter() {
        int limit = 10;
        List<SimpleValue> expectedList = Arrays.asList(new SimpleValue());
        when(dao.findAccountExecutiveList(FILTER, limit)).thenReturn(expectedList);

        List<SimpleValue> actualList = service.getAccountExecutivesByFilter(FILTER);
        assertNotNull(actualList);
        verify(dao, Mockito.atLeastOnce()).findAccountExecutiveList(FILTER, limit);
        assertEquals(expectedList, actualList);
    }

    @Test
    public void shouldGetProductListPrimarySort() {
        ProductListPrimarySort expectedResult = ProductListPrimarySort.PRODUCT_DESCRIPTION;
        when(dao.getProductListPrimarySort(CUSTOMER_ID)).thenReturn(expectedResult);

        ProductListPrimarySort actualResult = service.getProductListPrimarySort(CUSTOMER_ID);
        assertNotNull(actualResult);
        verify(dao).getProductListPrimarySort(CUSTOMER_ID);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldSetProductListPrimarySort() {
        ProductListPrimarySort actualResult = service.getProductListPrimarySort(CUSTOMER_ID);
        service.setProductListPrimarySort(actualResult, CUSTOMER_ID);

        verify(dao).updateProductListPrimarySort(CUSTOMER_ID, actualResult);
    }

    @Test
    public void shouldCheckCustomerNameExists() {
        boolean expected = true;
        when(dao.checkCustomerNameExists(ANY_STRING)).thenReturn(expected);

        boolean actual = service.checkCustomerNameExists(ANY_STRING);
        assertNotNull(actual);
        verify(dao, Mockito.atLeastOnce()).checkCustomerNameExists(ANY_STRING);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldCheckFederalTaxIdExists() {
        boolean expected = true;
        when(dao.checkCustomerFederalTaxIdExists(ANY_STRING)).thenReturn(expected);

        boolean actual = service.checkFederalTaxIdExists(ANY_STRING);
        assertNotNull(actual);
        verify(dao, Mockito.atLeastOnce()).checkCustomerFederalTaxIdExists(ANY_STRING);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldFindCustomerByName() {
        CustomerEntity expectedResult = buildCustomerEntity();
        when(dao.findCustomerByName(ANY_STRING)).thenReturn(expectedResult);

        CustomerEntity actualResult = service.findCustomerByName(ANY_STRING);
        assertNotNull(actualResult);
        verify(dao).findCustomerByName(ANY_STRING);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testIsActiveCustomer() {
        boolean expected = true;
        when(dao.isActiveCustomer(CUSTOMER_ID)).thenReturn(expected);

        boolean actual = service.isActiveCustomer(CUSTOMER_ID);
        assertNotNull(actual);
        verify(dao, Mockito.atLeastOnce()).isActiveCustomer(CUSTOMER_ID);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetUnitCostCenterCodes() {
        UnitAndCostCenterCodesBO expected = new UnitAndCostCenterCodesBO();
        when(networkDao.getUnitAndCostCenterCodes(CUSTOMER_ID)).thenReturn(expected);

        UnitAndCostCenterCodesBO actual = service.getUnitCostCenterCodes(CUSTOMER_ID);
        assertNotNull(actual);
        verify(networkDao, Mockito.atLeastOnce()).getUnitAndCostCenterCodes(CUSTOMER_ID);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetCreditLimit() {
        Long expected = ANY_LONG;
        when(dao.getCreditLimit(CUSTOMER_ID)).thenReturn(expected);

        Long actual = service.getCreditLimit(CUSTOMER_ID);
        assertNotNull(actual);
        verify(dao, Mockito.times(1)).getCreditLimit(CUSTOMER_ID);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetAccountExecutives() {
        List<SimpleValue> expected = Arrays.asList(new SimpleValue());
        when(dao.getAccountExecutives()).thenReturn(expected);

        List<SimpleValue> actual = service.getAccountExecutives();
        assertNotNull(actual);
        assertTrue("AE list size should be more than zero.", actual.size() > 0);
        assertEquals(expected, actual);
        verify(dao, Mockito.times(1)).getAccountExecutives();
    }

    @Test
    public void shouldGetAllNetworks() {
        List<NetworkListItemBO> expected = Arrays.asList(new NetworkListItemBO());
        when(networkDao.getAllNetworks()).thenReturn(expected);

        List<NetworkListItemBO> actual = service.getAllNetworks();
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(networkDao, Mockito.times(1)).getAllNetworks();
    }

    @Test
    public void testGetMarginTolerance() throws EntityNotFoundException {
        BigDecimal expected = new BigDecimal(EXPECTED_MARGIN);
        CustomerEntity newData = buildCustomerEntity();
        newData.setId(CUSTOMER_ID);
        Mockito.when(dao.find(CUSTOMER_ID)).thenReturn(eq(newData));

        BigDecimal actual = service.getMarginTolerance(newData.getId());
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(dao).find(CUSTOMER_ID);
    }

    @Test
    public void testCreateWithNormalCase() throws Exception {
        CustomerEntity newData = buildCustomerEntity();
        newData.getLocations().add(buildLocation());
        BillToEntity billTo = newData.getBillToAddresses().iterator().next();
        Mockito.when(billToDao.saveOrUpdate(billTo)).thenReturn(billTo);
        Mockito.when(xmlDetailsAxDao.getDetails(billTo.getId())).thenReturn(getXmlDetailsAxBO());
        Mockito.when(locations.getActiveAccountExecutive()).thenReturn(newData.getLocations().iterator().next().getActiveAccountExecutive());

        CustomerEntity result = service.createCustomer(newData);
        assertSame(newData, result);
        Mockito.verify(billToValidator).validate(eq(result.getBillTos().iterator().next()));
        Mockito.verify(validator).validate(newData);
        Mockito.verify(dao).persist(newData);
        Mockito.verify(userDao).getUserEntityById(SecurityUtils.getCurrentPersonId());
        Mockito.verify(billToDao, Mockito.times(1)).saveOrUpdate(billTo);
        Mockito.verify(xmlDetailsAxDao, Mockito.times(1)).getDetails(billTo.getId());
        Mockito.verify(sterlingMessageProducer, Mockito.times(1)).publishMessage(getSterlingMessageVO());

    }

    @Test
    public void testSaveBillToAddress() throws ApplicationException, JMSException {
        BillToEntity billTo = getBillTo();
        Mockito.when(billToDao.saveOrUpdate(billTo)).thenReturn(billTo);
        Mockito.when(xmlDetailsAxDao.getDetails(billTo.getId())).thenReturn(getXmlDetailsAxBO());
        service.saveBillTo(billTo);
        Mockito.verify(billToDao, Mockito.times(1)).saveOrUpdate(billTo);
        Mockito.verify(xmlDetailsAxDao, Mockito.times(1)).getDetails(billTo.getId());
        Mockito.verify(sterlingMessageProducer, Mockito.times(1)).publishMessage(getSterlingMessageVO());
    }

    @Test(expected = Exception.class)
    public void testSaveBillToAddressException() throws ApplicationException, JMSException {
        BillToEntity billTo = getBillTo();
        Mockito.when(billToDao.saveOrUpdate(billTo)).thenReturn(billTo);
        Mockito.when(xmlDetailsAxDao.getDetails(billTo.getId())).thenReturn(getXmlDetailsAxBO());
        doThrow(new ApplicationException()).when(sterlingMessageProducer).publishMessage(getSterlingMessageVO());
        try {
            service.saveBillTo(billTo);
            fail("Application exception not thrown");
        } catch (Exception e) {
            Mockito.verify(billToDao, Mockito.times(1)).saveOrUpdate(billTo);
            Mockito.verify(xmlDetailsAxDao, Mockito.times(1)).getDetails(billTo.getId());
            throw new ApplicationException();
        }
    }

    @Test(expected = Exception.class)
    public void testSaveBillToAddressWhenDaoReturnsNullException() throws ApplicationException, JMSException {
        BillToEntity billTo = getBillTo();
        Mockito.when(billToDao.saveOrUpdate(billTo)).thenReturn(billTo);
        Mockito.when(xmlDetailsAxDao.getDetails(billTo.getId())).thenReturn(null);
        try {
            service.saveBillTo(billTo);
            fail("Application exception not thrown");
        } catch (Exception e) {
            Mockito.verify(billToDao, Mockito.times(1)).saveOrUpdate(billTo);
            Mockito.verify(xmlDetailsAxDao, Mockito.times(1)).getDetails(billTo.getId());
            throw new ApplicationException();
        }
    }

    @Test
    public void testGetBillToAddress() throws EntityNotFoundException {
        BillToEntity billTo = new BillToEntity();
        billTo.setId(BILL_TO_ID);
        Mockito.when(billToDao.find(BILL_TO_ID)).thenReturn(billTo);

        BillToEntity actualBillToEntity = service.getBillTo(BILL_TO_ID);
        Mockito.verify(billToDao).find(BILL_TO_ID);

        assertEquals(billTo, actualBillToEntity);
    }

    @Test
    public void shouldGetCustomerListItemBOByNameAndStatus() {
        List<CustomerListItemBO> value = Arrays.asList(new CustomerListItemBO());
        Mockito.when(dao.getByStatusAndName(CURRENT_USER_ID, OrganizationStatus.ACTIVE, "pls", null)).thenReturn(value);
        List<CustomerListItemBO> pls = service.getByStatusAndName(OrganizationStatus.ACTIVE, "pls", null);
        verify(dao).getByStatusAndName(CURRENT_USER_ID, OrganizationStatus.ACTIVE, "pls", null);
        assertEquals(value, pls);
    }

    @Test
    public void shouldCheckEDINumberExists() {
        Boolean expectedResult = Boolean.TRUE;
        Mockito.when(dao.checkEDINumberExists(CUSTOMER_NUMBER)).thenReturn(expectedResult);
        Boolean actualResult = service.checkEDINumberExists(CUSTOMER_NUMBER);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldGetCustomerIdNameTuplesByName() {
        List<SimpleValue> expected = Arrays.asList(new SimpleValue());
        Mockito.when(dao.getCustomerIdNameTuplesByName("PLS", 1, 1, null, ANY_LONG)).thenReturn(expected);

        List<SimpleValue> actual = service.getCustomerIdNameTuplesByName("PLS", 1, 1, null, ANY_LONG);
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(dao).getCustomerIdNameTuplesByName("PLS", 1, 1, null, ANY_LONG);
    }

    private BillToEntity getBillTo() {

        BillToEntity billTo = new BillToEntity();
        billTo.setId(1L);
        BillingInvoiceNodeEntity billingInvoiceNode = new BillingInvoiceNodeEntity();
        billingInvoiceNode.setAddress(new AddressEntity());
        billTo.setBillingInvoiceNode(billingInvoiceNode);
        billTo.setOrganization(new OrganizationEntity());
        billTo.getOrganization().setId(CUSTOMER_ID);
        billTo.getOrganization().setNetworkId(NETWORK_ID);
        BillToThresholdSettingsEntity thresholdSettingsEntity = new BillToThresholdSettingsEntity();
        thresholdSettingsEntity.setMargin(BigDecimal.TEN);
        thresholdSettingsEntity.setThreshold(BigDecimal.TEN);
        thresholdSettingsEntity.setTotalRevenue(BigDecimal.TEN);
        return billTo;
    }

    private XmlDetailsAxBO getXmlDetailsAxBO() {
        XmlDetailsAxBO xmlDetailsAxBO = new XmlDetailsAxBO();
        xmlDetailsAxBO.setCurrency(Currency.USD);
        xmlDetailsAxBO.setAccountNum("213838-299300");
        xmlDetailsAxBO.setGroupCode("N2");
        xmlDetailsAxBO.setUnitCode("N2");
        xmlDetailsAxBO.setCostCenterCode("Pitt1");
        xmlDetailsAxBO.setEmployerNum("98-8958458");
        xmlDetailsAxBO.setOrgId(1L);
        xmlDetailsAxBO.setOrgName("GREAT AMERICAN PAPER");
        xmlDetailsAxBO.setBillToName("TEST BT 1");
        xmlDetailsAxBO.setAddress1("3120 UNIONVILLE RD");
        xmlDetailsAxBO.setAddress2("Building 100");
        xmlDetailsAxBO.setCity("CRANBERRY TWP");
        xmlDetailsAxBO.setStateCode("PA");
        xmlDetailsAxBO.setPostalCode("16066");
        xmlDetailsAxBO.setCountryCode("USA");
        return xmlDetailsAxBO;
    }

    private SterlingIntegrationMessageBO getSterlingMessageVO() {
        SterlingIntegrationMessageBO sterlingMessageVO = new SterlingIntegrationMessageBO(getCustTable(), EDIMessageType.CUSTOMER);
        return sterlingMessageVO;
    }

    private CustTable getCustTable() {
        CustTable custTable = new CustTable();

        custTable.setOperation("CREATE");
        custTable.setAccountNumber("213838-299300");
        custTable.setCurrency(Currency.USD.toString());
        custTable.setCustomerGroup("N2");
        custTable.setBusinessUnit("N2");
        custTable.setCostCenter("Pitt1");
        custTable.setDepartment("SL");
        custTable.setPersonId(SecurityUtils.getCurrentPersonId());
        custTable.setIdentificationNumber("98-8958458");
        custTable.setCustomerOrgId(1L);
        custTable.setName("GREAT AMERICAN PAPER");
        BillToAddress address = new BillToAddress();
        address.setBillToName("TEST BT 1");
        address.setAddress1("3120 UNIONVILLE RD");
        address.setAddress2("Building 100");
        address.setCity("CRANBERRY TWP");
        address.setState("PA");
        address.setPostalCode("16066");
        address.setCountry("USA");
        custTable.setAddress(address);

        return custTable;
    }

    private CustomerEntity buildCustomerEntity() {
        CustomerEntity customer = new CustomerEntity();
        customer.setAddress(buildAddressEntity());
        customer.setPhone(buildPhoneEntity());
        customer.setFax(buildFaxEntity());
        customer.setBillToAddresses(new HashSet<BillToEntity>());
        customer.getBillToAddresses().add(getBillTo());
        customer.setLocations(new HashSet<OrganizationLocationEntity>());
        return customer;
    }

    private OrganizationLocationEntity buildLocation() {
        OrganizationLocationEntity locationEntity = new OrganizationLocationEntity();
        locationEntity.setLocationName("MAIN STEEL");
        locationEntity.setId(222L);
        locationEntity.setCustomerLocationUsers(new HashSet<CustomerUserEntity>());
        locationEntity.setAccountExecutives(new HashSet<AccountExecutiveEntity>());
        return locationEntity;
    }
    private OrganizationVoicePhoneEntity buildPhoneEntity() {
        return new OrganizationVoicePhoneEntity();
    }

    private OrganizationFaxPhoneEntity buildFaxEntity() {
        return new OrganizationFaxPhoneEntity();
    }

    private AddressEntity buildAddressEntity() {
        return new AddressEntity();
    }
}
