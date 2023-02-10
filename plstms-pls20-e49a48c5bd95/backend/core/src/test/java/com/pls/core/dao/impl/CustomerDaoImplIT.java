package com.pls.core.dao.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.CustomerCreditInfoBO;
import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;

/**
 * Test cases for {@link com.pls.core.dao.impl.CustomerDaoImpl} class.
 * 
 * @author Artem Arapov
 */
public class CustomerDaoImplIT extends AbstractDaoTest {

    private static final long CUSTOMER_ID = 1L;
    private static final String CUSTOMER_NAME = "PLS SHIPPER";
    private static final Long NETWORK_ID = 7L;
    private static final String CUSTOMER_TAX_ID = "20-9548043";
    private static final BigDecimal CUSTOMER_CREDIT_LIMIT = new BigDecimal(10000);
    private static final String INACTIVE_CUSTOMER_EXISTING_EDI_NUMBER = "3803226441";
    private static final String ACTIVE_CUSTOMER_EXISTING_EDI_NUMBER = "BOB BAKER";
    private static final String CUSTOMER_NOT_EXISTING_EDI_NUMBER = "BLABLABLA";
    private static final int MAX_NUMBER_OF_RESULTS = 1;
    private static final String FEDERAL_TAX_ID_PLS_SHIPPER = "20-9548043";
    private static final String FEDERAL_TAX_ID_SAIA = "34-0492671";
    private static final String RANDOM_STRING = "AKUNA MATATA";

    private static final String UPDATE_ORG_USER_STATUS = " UPDATE FLATBED.ORGANIZATION_USERS SET STATUS = :status WHERE PERSON_ID = :personId";
    private static final String UPDATE_NETWORK_USER_STATUS = " UPDATE FLATBED.NETWORK_USERS SET STATUS = :status WHERE PERSON_ID = :personId "
            + "and NETWORK_ID = 7";

    // Check that ORGANIZATION is present in test data set and it is SHIPPER
    private static final Long VALID_CUSTOMER_ID = 1L;

    @Autowired
    private CustomerDao sut;

    @Test
    public void checkCustomerNameExists() throws Exception {
        boolean exist = sut.checkCustomerNameExists(CUSTOMER_NAME);
        boolean doNotExist = sut.checkCustomerNameExists(RANDOM_STRING);
        Assert.assertTrue(exist);
        Assert.assertFalse(doNotExist);
    }

    @Test
    public void checkCustomerNameUniquenessForExistingCustomer() {
        boolean correctCustomer = sut.checkCustomerNameExists(CUSTOMER_ID, NETWORK_ID, CUSTOMER_NAME);
        boolean incorrectCustomer = sut.checkCustomerNameExists(CUSTOMER_ID + 1, NETWORK_ID, CUSTOMER_NAME);
        Assert.assertFalse(correctCustomer);
        Assert.assertTrue(incorrectCustomer);
    }

    @Test
    public void testCheckCustomerExists_AbsentCustomer() throws Exception {
        // test dataset does not have customer with ID equals -20
        Assert.assertFalse(sut.checkCustomerExists(-20L));
    }

    @Test
    public void testCheckCustomerExists_ExistCustomer() throws Exception {
        // test dataset contains customer with ID equals 1
        Assert.assertTrue(sut.checkCustomerExists(1L));
    }

    @Test
    public void testCheckCustomerExists_NullId() throws Exception {
        Assert.assertFalse(sut.checkCustomerExists(null));
    }

    @Test
    public void testCreate() throws Exception {
        CustomerEntity customer = createCustomerEntity();
        sut.persist(customer);

        flushAndClearSession();

        CustomerEntity actual = sut.get(customer.getId());
        validateCustomer(customer, actual);
    }

    @Test
    public void testFind() throws Exception {
        CustomerEntity customer = sut.find(CUSTOMER_ID);

        Assert.assertNotNull(customer);
        Assert.assertSame(CUSTOMER_ID, customer.getId());
    }

    @Test
    public void testFindAccountExecutiveList() throws Exception {
        int expectedCount = 2;
        String filter = "";
        List<SimpleValue> resultList = sut.findAccountExecutiveList(filter, expectedCount);

        Assert.assertNotNull(resultList);
        Assert.assertEquals(expectedCount, resultList.size());
    }

    @Test
    public void shouldFindAllAccountExecutives() throws Exception {
        List<SimpleValue> resultList = sut.getAccountExecutives();

        Assert.assertNotNull(resultList);
        Assert.assertEquals(2, resultList.size());
        Assert.assertEquals(new Long(1), resultList.get(0).getId());
        Assert.assertEquals("admin sysadmin", resultList.get(0).getName());
    }

    @Test
    public void testFindCustomerByName() throws Exception {
        CustomerEntity customer = sut.findCustomerByName(CUSTOMER_NAME);

        Assert.assertNotNull(customer);
        Assert.assertEquals(CUSTOMER_NAME, customer.getName());
    }

    @Test
    public void testGetAll() throws Exception {
        List<CustomerEntity> list = sut.getAll();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void testGetCustomerNetwork() throws Exception {
        CustomerEntity customer = sut.get(CUSTOMER_ID);

        Assert.assertNotNull(customer.getNetworkId());
    }

    @Test
    public void testGetProductListPrimarySort() {
        ProductListPrimarySort sort = sut.getProductListPrimarySort(CUSTOMER_ID);
        Assert.assertNotNull(sort);
        Assert.assertEquals(ProductListPrimarySort.PRODUCT_DESCRIPTION, sort);
    }

    @Test
    public void testUpdate() throws Exception {
        CustomerEntity customer = sut.find(CUSTOMER_ID);

        customer.setName("TEST CUSTOMER");
        customer.setAddress((AddressEntity) getSession().get(AddressEntity.class, 2L));
        customer.setContactFirstName("Firstname");
        customer.setContactLastName("Lastname");
        sut.update(customer);

        flushAndClearSession();

        CustomerEntity actual = (CustomerEntity) getSession().get(CustomerEntity.class, CUSTOMER_ID);
        validateCustomer(customer, actual);
    }

    @Test
    public void testUpdateProductListPrimarySort() {
        ProductListPrimarySort newValue = ProductListPrimarySort.SKU_PRODUCT_CODE;
        ProductListPrimarySort beforeUpdate = sut.getProductListPrimarySort(CUSTOMER_ID);
        Assert.assertNotNull(beforeUpdate);
        Assert.assertNotSame(newValue, beforeUpdate);

        sut.updateProductListPrimarySort(CUSTOMER_ID, newValue);
        flushAndClearSession();

        ProductListPrimarySort afterUpdate = sut.getProductListPrimarySort(CUSTOMER_ID);
        Assert.assertNotNull(afterUpdate);
        Assert.assertEquals(newValue, afterUpdate);
    }

    @Test
    public void testGetCustomerCreditInfo() {
        CustomerCreditInfoBO creditInfo = sut.getCustomerCreditInfo(CUSTOMER_ID);
        Assert.assertNotNull(creditInfo);
        Assert.assertEquals(CUSTOMER_TAX_ID, creditInfo.getTaxId());
        Assert.assertEquals(CUSTOMER_CREDIT_LIMIT, creditInfo.getCreditLimit());
    }

    @Test
    public void testGetByStatusAndName() {
        List<CustomerListItemBO> list = sut.getByStatusAndName(CUSTOMER_ID, OrganizationStatus.ACTIVE, "pls%", null);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("PLS SHIPPER", list.get(0).getName());

        list = sut.getByStatusAndName(CUSTOMER_ID, OrganizationStatus.ACTIVE, "NNN", null);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void shouldGetValidAccountExecutiveWhenMoreThanOneLocation() {
        List<CustomerListItemBO> list = sut.getByStatusAndName(1L, OrganizationStatus.ACTIVE, "PLS%", null);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("PLS SHIPPER", list.get(0).getName());
        Assert.assertEquals("Multiple", list.get(0).getAccountExecutive());
    }

    @Test
    public void shouldGetValidAccountExecutive() {
        List<CustomerListItemBO> list = sut.getByStatusAndName(1L, OrganizationStatus.ACTIVE, "MAC%", null);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("MACSTEEL", list.get(0).getName());
        Assert.assertEquals("admin sysadmin", list.get(0).getAccountExecutive());
    }

    @Test
    public void shouldCheckIfCustomerChangedForFinance() {
        CustomerEntity customer = new CustomerEntity();
        Assert.assertFalse(sut.isCustomerChangedForFinance(customer));

        customer.setId(CUSTOMER_ID);
        Assert.assertTrue(sut.isCustomerChangedForFinance(customer));

        customer.setName(CUSTOMER_NAME);
        Assert.assertTrue(sut.isCustomerChangedForFinance(customer));

        customer.setFederalTaxId(CUSTOMER_TAX_ID);
        Assert.assertFalse(sut.isCustomerChangedForFinance(customer));

        customer.setFederalTaxId(CUSTOMER_TAX_ID + 1);
        Assert.assertTrue(sut.isCustomerChangedForFinance(customer));

        customer.setFederalTaxId(CUSTOMER_TAX_ID);
        customer.setName(CUSTOMER_NAME + 1);
        Assert.assertTrue(sut.isCustomerChangedForFinance(customer));
    }

    @Test
    public void shouldGetCustomerIdNameTuplesByName() {
        List<SimpleValue> result = sut.getCustomerIdNameTuplesByName("SHI", 10, 0, false, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("PLS SHIPPER", result.get(0).getName());
        result = sut.getCustomerIdNameTuplesByName("STEEL", 10, 0, false, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("MACSTEEL", result.get(0).getName());
        Assert.assertEquals("MAIN STEEL", result.get(1).getName());
    }

    @Test
    public void shouldGetCustomerIdNameTuplesByNameActiveStatus() {
        List<SimpleValue> result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, false, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        updateOrganizationStatus(VALID_CUSTOMER_ID, false);
        result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, false, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldGetCustomerIdNameTuplesByNameAllStatus() {
        updateOrganizationStatus(1L, true);
        List<SimpleValue> result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, true, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        updateOrganizationStatus(1L, false);
        result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, true, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        updateOrganizationStatus(1L, true);
    }

    @Test
    public void shouldGetCustomerIdNameTuplesByNameWithoutAssignedCustomers() {
        changeStatusNetworkUser(1L, false);
        List<SimpleValue> result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, true, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        changeStatusCustomerUser(1L, false);
        result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, true, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldGetCustomerIdNameTuplesByNameWithAssignedCustomers() {
        changeStatusNetworkUser(1L, false);
        changeStatusCustomerUser(1L, false);
        List<SimpleValue> result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, true, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
        changeStatusCustomerUser(1L, true);
        result = sut.getCustomerIdNameTuplesByName("PLS", 10, 0, true, 1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void shouldFindCustomerByEDINumber() {
        CustomerEntity actualEntity = sut.findCustomerByEDINumber(ACTIVE_CUSTOMER_EXISTING_EDI_NUMBER);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(ACTIVE_CUSTOMER_EXISTING_EDI_NUMBER, actualEntity.getEdiAccount());
    }

    @Test
    public void shouldNotFindInactiveCustomerByEDINumber() {
        CustomerEntity actualEntity = sut.findCustomerByEDINumber(INACTIVE_CUSTOMER_EXISTING_EDI_NUMBER);
        Assert.assertNull(actualEntity);
    }

    @Test
    public void shouldNotFindCustomerByEDINumber() {
        CustomerEntity actualEntity = sut.findCustomerByEDINumber(CUSTOMER_NOT_EXISTING_EDI_NUMBER);
        Assert.assertNull(actualEntity);
    }

    @Test
    public void shouldCheckEDINumberExists() {
        Boolean actualResult = sut.checkEDINumberExists(INACTIVE_CUSTOMER_EXISTING_EDI_NUMBER);
        Assert.assertTrue(actualResult);
    }

    @Test
    public void shouldCheckEDINumberNotExists() {
        Boolean actualResult = sut.checkEDINumberExists(CUSTOMER_NOT_EXISTING_EDI_NUMBER);
        Assert.assertFalse(actualResult);
    }

    @Test
    public void shouldGetAssociatedCustomerLocations() {
        List<AssociatedCustomerLocationBO> locations = sut.getAssociatedCustomerLocations(1L, 1L, 2L);
        Assert.assertEquals(2, locations.size());
        AssociatedCustomerLocationBO location = getLocationById(locations, 1L);
        Assert.assertNotNull(location);
        Assert.assertEquals("MAIN STEEL", location.getLocationName());
        Assert.assertEquals("admin sysadmin", location.getAccountExecutive());
        Assert.assertEquals("2011/03/22", new SimpleDateFormat("yyyy/MM/dd").format(location.getModifiedDate()));
        Assert.assertEquals("admin sysadmin", location.getModifiedBy());
        Assert.assertTrue(location.isDefaultNode());
        location = getLocationById(locations, 2L);
        Assert.assertNotNull(location);
        Assert.assertEquals("COLUMBIA, SC", location.getLocationName());
        Assert.assertEquals("RICH LITTON", location.getAccountExecutive());
        Assert.assertNull(location.getModifiedDate());
        Assert.assertTrue(StringUtils.isBlank(location.getModifiedBy()));
        Assert.assertNull(location.isDefaultNode());
    }

    @Test
    public void shouldGetAssociatedCustomerLocationsForNewUser() {
        List<AssociatedCustomerLocationBO> locations = sut.getAssociatedCustomerLocations(1L, 1L, null);
        Assert.assertEquals(2, locations.size());
        AssociatedCustomerLocationBO location = getLocationById(locations, 1L);
        Assert.assertNotNull(location);
        Assert.assertEquals("MAIN STEEL", location.getLocationName());
        Assert.assertEquals("admin sysadmin", location.getAccountExecutive());
        Assert.assertNull(location.getModifiedDate());
        Assert.assertTrue(StringUtils.isBlank(location.getModifiedBy()));
        Assert.assertTrue(location.isDefaultNode());
        location = getLocationById(locations, 2L);
        Assert.assertNotNull(location);
        Assert.assertEquals("COLUMBIA, SC", location.getLocationName());
        Assert.assertEquals("RICH LITTON", location.getAccountExecutive());
        Assert.assertNull(location.getModifiedDate());
        Assert.assertTrue(StringUtils.isBlank(location.getModifiedBy()));
        Assert.assertNull(location.isDefaultNode());
    }

    @Test
    public void shouldFindCustomersForUserByName() {
        List<ParentOrganizationBO> actualResult = sut.findCustomersForUserByName(CUSTOMER_ID, CUSTOMER_NAME, MAX_NUMBER_OF_RESULTS);
        Assert.assertNotNull("ParentOrganizationBO list should not be null.", actualResult);
        int parentOrgBOSize = actualResult.size();
        Assert.assertEquals("ParentOrganizationBO list should have 1 value", MAX_NUMBER_OF_RESULTS, parentOrgBOSize);
        for (ParentOrganizationBO p : actualResult) {
            long actualOrgId = p.getOrganizationId();
            String actualOrgName = p.getOrganizationName();
            Assert.assertEquals("Actual result ID should be equal to ID by which we search.", CUSTOMER_ID, actualOrgId);
            Assert.assertEquals("Actual result Name should be equal to name by which we search.", CUSTOMER_NAME, actualOrgName);
        }
    }

    @Test
    public void checkCustomerNameExistsTest() {
        boolean actualResult1 = sut.checkCustomerNameExists(CUSTOMER_NAME);
        Assert.assertNotNull("Result should not be null.", actualResult1);
        Assert.assertTrue("Customer with the name 'Pls Shipper' should exist", actualResult1);

        String falseCustomerName = "Cocojambo";
        boolean actualResult2 = sut.checkCustomerNameExists(falseCustomerName);
        Assert.assertNotNull("Result should not be null.", actualResult2);
        Assert.assertFalse("Customer with the name 'Cocojambo' should not exist", actualResult2);
    }

    @Test
    public void isActiveCustomerTest() {
        boolean actualResult = sut.isActiveCustomer(CUSTOMER_ID);
        Assert.assertNotNull(actualResult);
        Assert.assertTrue("Customer's status with ID 1L(PLS SHIPPER) should be Active.", actualResult);
        long precoatMetalsID = 48L;
        boolean actualResultToBeFalse = sut.isActiveCustomer(precoatMetalsID);
        Assert.assertFalse("Customer's status with ID 48L(PRECOAT METALS) should be Inactive.", actualResultToBeFalse);
    }

    @Test
    public void shouldFindCustomersByFederalTaxId() {
        List<CustomerEntity> actualResult = sut.findCustomersByFederalTaxId(FEDERAL_TAX_ID_PLS_SHIPPER);
        Assert.assertNotNull(actualResult);
        boolean actualSizeIsNotZero = actualResult.size() > 0;
        Assert.assertTrue("Length of Customer Entity list should be more than 0", actualSizeIsNotZero);
        String actualTaxId = "";
        for (CustomerEntity plsShipper : actualResult) {
            actualTaxId = plsShipper.getFederalTaxId();
            Assert.assertEquals("Customer's taxId should be equal to taxId we searched with.", FEDERAL_TAX_ID_PLS_SHIPPER, actualTaxId);
        }
    }

    @Test
    public void getCreditLimitTest() {
        long actualCreditLimit = sut.getCreditLimit(CUSTOMER_ID);
        Assert.assertNotNull(actualCreditLimit);
        long creditLimitFromDB = 10000;
        Assert.assertEquals("Credit limit for PLS SHIPPER should be 10000", creditLimitFromDB, actualCreditLimit);
//        long actualCreditLimitForSAIA = sut.getCreditLimit(68L);
        Assert.assertNull("Credit limit for SAIA sould be null", sut.getCreditLimit(68L));
    }

    @Test
    public void checkCustomerFederalTaxIdExistsTest() {
        boolean actualResult = sut.checkCustomerFederalTaxIdExists(FEDERAL_TAX_ID_PLS_SHIPPER);
        Assert.assertNotNull(actualResult);
        Assert.assertTrue("Federal Tax ID for PLS 'SHIPPER should exist'.", actualResult);
        boolean actualResultForSAIA = sut.checkCustomerFederalTaxIdExists(FEDERAL_TAX_ID_SAIA);
        Assert.assertFalse("There shouldn't be Federal Tax ID for SAIA", actualResultForSAIA);
    }

    @Test
    public void shouldGetCreditLimitRequiredTrue() {
        boolean actualResult = sut.getCreditLimitRequired(CUSTOMER_ID);
        Assert.assertTrue(actualResult);
    }

    @Test
    public void shouldGetCreditLimitRequiredFalse() {
        boolean actualResult = sut.getCreditLimitRequired(2L);
        Assert.assertFalse(actualResult);
    }

    private AssociatedCustomerLocationBO getLocationById(List<AssociatedCustomerLocationBO> locations, long locationId) {
        for (AssociatedCustomerLocationBO location : locations) {
            if (ObjectUtils.equals(locationId, location.getLocationId())) {
                return location;
            }
        }
        return null;
    }

    private void changeStatusCustomerUser(Long personId, boolean status) {
        Query query = getSession().createSQLQuery(UPDATE_ORG_USER_STATUS);
        query.setParameter("personId", personId);
        query.setParameter("status", status ? "A" : "I");
        query.executeUpdate();
    }

    private void changeStatusNetworkUser(Long personId, boolean status) {
        Query query = getSession().createSQLQuery(UPDATE_NETWORK_USER_STATUS);
        query.setParameter("personId", personId);
        query.setParameter("status", status ? "A" : "I");
        query.executeUpdate();
    }

    private CustomerEntity createCustomerEntity() {
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Test customer #" + Math.random());
        customer.setAddress((AddressEntity) getSession().get(AddressEntity.class, 1L));
        customer.setStatus(OrganizationStatus.ACTIVE);
        customer.setContactFirstName("FirstName");
        customer.setContactLastName("LastName");
        customer.setContactEmail("contact@plslogistics.com");
        customer.setBillToAddresses(new HashSet<BillToEntity>(getBillToList()));
        customer.getModification().setCreatedBy(1L);
        customer.getModification().setModifiedBy(1L);

        return customer;
    }

    @SuppressWarnings("unchecked")
    private List<BillToEntity> getBillToList() {
        Criteria criteria = getSession().createCriteria(BillToEntity.class);
        return criteria.list();
    }

    private void validateCustomer(CustomerEntity expected, CustomerEntity actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getNetworkId(), actual.getNetworkId());
        Assert.assertEquals(expected.getAddress().getId(), actual.getAddress().getId());
        Assert.assertEquals(expected.getContactFirstName(), actual.getContactFirstName());
        Assert.assertEquals(expected.getContactLastName(), actual.getContactLastName());
        Assert.assertEquals(expected.getContactEmail(), actual.getContactEmail());
    }
}
