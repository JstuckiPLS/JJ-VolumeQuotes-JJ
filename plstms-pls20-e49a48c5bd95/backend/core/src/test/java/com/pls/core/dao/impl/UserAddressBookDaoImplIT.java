package com.pls.core.dao.impl;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;

/**
 * Test cases for {@link com.pls.core.dao.impl.UserAddressBookDaoImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class UserAddressBookDaoImplIT extends AbstractDaoTest {
    private static final String COUNTRY_CODE = "USA";

    private static final String STATE_CODE = "PA";

    private static final String ADDRESS_NAME = "Test address name";

    private static final String LOCATION_CODE = "LTL1111111";

    private static final Long ORG_ID = 1L;

    private static final Long ADDR_ID = 1L;

    private static final String ZIP_CODE = "54303";

    private static final String ZIP_CODE2 = "44444";

    private static final String CITY_NAME = "GREEN BAY";

    private static final String CITY_NAME2 = "NEWTON FALLS";

    private static final String TEST_ADDRESS_NAME = "FIRST ADDRESS";

    private static final String TEST_ADDRESS_CODE = "LT-00000001";

    private static final Long USER_ID = 2L;

    private static final String FILTER_WORD = "West";

    @Autowired
    private UserAddressBookDao sut;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    @Test
    public void testPersistEntity() {
        UserAddressBookEntity entity = prepareTestEntity();

        sut.persist(entity);
        flushAndClearSession();

        Assert.assertNotNull(entity.getId());

        UserAddressBookEntity afterSave = (UserAddressBookEntity) getSession().get(UserAddressBookEntity.class, entity.getId());

        Assert.assertNotNull(afterSave);
        Assert.assertEquals(entity, afterSave);
    }

    @Test
    public void testPersistEntityWithoutAddressCode() {
        UserAddressBookEntity entity = prepareTestEntity();
        entity.setAddressCode(null);

        sut.persist(entity);
        flushAndClearSession();

        Assert.assertNotNull(entity.getId());

        UserAddressBookEntity afterSave = (UserAddressBookEntity) getSession().get(UserAddressBookEntity.class, entity.getId());

        Assert.assertNotNull(afterSave);
        Assert.assertTrue(StringUtils.isNotBlank(afterSave.getAddressCode()));
    }

    @Test
    public void testPersistEntityWithoutAddressName() {
        UserAddressBookEntity entity = prepareTestEntity();
        entity.setAddressName(null);

        sut.persist(entity);
        flushAndClearSession();

        Assert.assertNotNull(entity.getId());

        UserAddressBookEntity afterSave = (UserAddressBookEntity) getSession().get(UserAddressBookEntity.class, entity.getId());

        Assert.assertNotNull(afterSave);
        Assert.assertTrue(StringUtils.isNotBlank(afterSave.getAddressName()));
    }

    @Test
    public void testUpdateEntity() throws EntityNotFoundException {
        UserAddressBookEntity entity = sut.get(ADDR_ID);
        String email = "New email";
        entity.setEmail(email);

        UserAddressBookEntity updatedEntity = sut.update(entity);
        flushAndClearSession();

        UserAddressBookEntity afterUpdate = (UserAddressBookEntity) getSession().get(UserAddressBookEntity.class, ADDR_ID);

        Assert.assertNotNull(afterUpdate);
        Assert.assertEquals(updatedEntity, afterUpdate);
        Assert.assertEquals(email, afterUpdate.getEmail());
    }

    @Test
    public void testGetExistingEntity() throws EntityNotFoundException {
        UserAddressBookEntity entity = sut.get(ADDR_ID);
        Assert.assertNotNull(entity);
        Assert.assertEquals(ADDR_ID, entity.getId());

    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetNonExistingEntity() throws EntityNotFoundException {
        sut.get(-1L);
    }

    @Test
    public void testGetCustomerAddressBook() {
        List<UserAddressBookEntity> addressList = sut.getCustomerAddressBookForUser(ORG_ID, USER_ID, true, Arrays.asList(AddressType.values()));
        Assert.assertNotNull(addressList);
        Assert.assertFalse(addressList.isEmpty());
        for (UserAddressBookEntity address : addressList) {
            Assert.assertNotNull(address);
            Assert.assertEquals(Status.ACTIVE, address.getStatus());
        }
    }

@Test
    public void testDeleteCustomerAddress() throws EntityNotFoundException {
        sut.deleteUserAddressBookEntry(1L, 1L);
        UserAddressBookEntity address = sut.get(1L);
        Assert.assertEquals(Status.INACTIVE, address.getStatus());
        Assert.assertEquals(new Long(1L), address.getModification().getModifiedBy());

        Calendar expectedDate = Calendar.getInstance();
        expectedDate.setTime(new Date());
        Calendar actualDate = Calendar.getInstance();
        actualDate.setTime(address.getModification().getModifiedDate());
        Assert.assertEquals(expectedDate.get(Calendar.YEAR), actualDate.get(Calendar.YEAR));
        Assert.assertEquals(expectedDate.get(Calendar.MONTH), actualDate.get(Calendar.MONTH));
        Assert.assertEquals(expectedDate.get(Calendar.DAY_OF_MONTH), actualDate.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testFindCustomerAddressByCountryAndZip() {
        List<UserAddressBookEntity> result = sut.findCustomerAddressByCountryAndZip(ORG_ID, USER_ID, COUNTRY_CODE,
                ZIP_CODE, CITY_NAME, Arrays.asList(AddressType.values()));
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        UserAddressBookEntity userAddressBookEntity = result.get(0);
        Assert.assertNotNull(userAddressBookEntity);
        Assert.assertEquals(ORG_ID, userAddressBookEntity.getOrgId());
        Assert.assertEquals(COUNTRY_CODE, userAddressBookEntity.getAddress().getCountry().getId());
        Assert.assertEquals(ZIP_CODE, userAddressBookEntity.getAddress().getZip());
        Assert.assertTrue(StringUtils.equalsIgnoreCase(CITY_NAME, userAddressBookEntity.getAddress().getCity()));
    }

    @Test
    public void testFindCustomerAddressByCountryAndZipForUser() {
        List<UserAddressBookEntity> result = sut.findCustomerAddressByCountryAndZip(ORG_ID, USER_ID, COUNTRY_CODE, ZIP_CODE2,
                CITY_NAME2, Arrays.asList(AddressType.values()));
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        for (UserAddressBookEntity userAddressBook : result) {
            Assert.assertNotNull(userAddressBook);
            Assert.assertEquals(ORG_ID, userAddressBook.getOrgId());
            Assert.assertEquals(COUNTRY_CODE, userAddressBook.getAddress().getCountry().getId());
            Assert.assertEquals(ZIP_CODE2, userAddressBook.getAddress().getZip());
        }
    }

    @Test
    public void testFindCustomerAddressByCountryAndZipForUserNoData() {
        List<UserAddressBookEntity> result = sut.findCustomerAddressByCountryAndZip(ORG_ID, USER_ID, COUNTRY_CODE, "WRONG_ZIP_CODE",
                CITY_NAME2, Arrays.asList(AddressType.values()));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetListFilteredByWord() {
        List<UserAddressBookEntity> result = sut.listFilteredByWord(ORG_ID, USER_ID, FILTER_WORD);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        for (UserAddressBookEntity userAddress : result) {
            Assert.assertNotNull(userAddress);
            Assert.assertEquals(ORG_ID, userAddress.getOrgId());

            String contactName = StringUtils.defaultString(userAddress.getContactName(), "").toUpperCase();
            String address1 = StringUtils.defaultString(userAddress.getAddress().getAddress1(), "").toUpperCase();
            String address2 = StringUtils.defaultString(userAddress.getAddress().getAddress2(), "").toUpperCase();
            String orgName = StringUtils.defaultString(userAddress.getCustomer().getName(), "").toUpperCase();
            Assert.assertTrue(contactName.contains(FILTER_WORD.toUpperCase()) || address1.contains(FILTER_WORD.toUpperCase())
                    || address2.contains(FILTER_WORD.toUpperCase()) || orgName.contains(FILTER_WORD.toUpperCase()));
        }
    }

    @Test
    public void testGetListFilteredByWordForUser() {
        List<UserAddressBookEntity> result = sut.listFilteredByWord(ORG_ID, USER_ID, FILTER_WORD);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        for (UserAddressBookEntity userAddressBook : result) {
            Assert.assertNotNull(userAddressBook);
            Assert.assertEquals(ORG_ID, userAddressBook.getOrgId());

            String contactName = StringUtils.defaultString(userAddressBook.getContactName(), "").toUpperCase();
            String address1 = StringUtils.defaultString(userAddressBook.getAddress().getAddress1(), "").toUpperCase();
            String address2 = StringUtils.defaultString(userAddressBook.getAddress().getAddress2(), "").toUpperCase();
            String orgName = StringUtils.defaultString(userAddressBook.getCustomer().getName(), "").toUpperCase();
            Assert.assertTrue(contactName.contains(FILTER_WORD.toUpperCase()) || address1.contains(FILTER_WORD.toUpperCase())
                    || address2.contains(FILTER_WORD.toUpperCase()) || orgName.contains(FILTER_WORD.toUpperCase()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindCustomerAddressByCountryAndZipWithoutOrgId() {
        sut.findCustomerAddressByCountryAndZip(null, null, COUNTRY_CODE, ZIP_CODE, CITY_NAME, Arrays.asList(AddressType.values()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindCustomerAddressByCountryAndZipWithoutCountryCode() {
        sut.findCustomerAddressByCountryAndZip(ORG_ID, null, null, ZIP_CODE, CITY_NAME, Arrays.asList(AddressType.values()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindCustomerAddressByCountryAndZipWithoutZipCode() {
        sut.findCustomerAddressByCountryAndZip(ORG_ID, null, COUNTRY_CODE, null, CITY_NAME, Arrays.asList(AddressType.values()));
    }

    @Test
    public void testCheckAddressNameExists() {
        Assert.assertTrue(sut.checkAddressNameExists(ORG_ID, TEST_ADDRESS_NAME));
    }

    @Test
    public void testCheckAddressNameExistsWrongOrgId() {
        long wrongOrgId = -1L;
        Assert.assertFalse(sut.checkAddressNameExists(wrongOrgId, TEST_ADDRESS_NAME));
    }

    @Test
    public void testCheckAddressNameExistsWrongAddressName() {
        String wrongAddrName = getClass().getName();
        Assert.assertFalse(sut.checkAddressNameExists(ORG_ID, wrongAddrName));
    }

    @Test
    public void testGetNextGeneratedAddressNameNumber() {
        BigDecimal number = sut.getNextGeneratedAddressNameNumber();
        Assert.assertNotNull(number);
        Assert.assertTrue(number.longValue() > 0);
    }

    @Test
    public void testGetNextGeneratedAddressCode() {
        BigDecimal number = sut.getNextGeneratedAddressCode();
        Assert.assertNotNull(number);
        Assert.assertTrue(number.longValue() > 0);
    }

    @Test
    public void testCheckAddressCodeExists() {
        Assert.assertTrue(sut.checkAddressCodeExists(ORG_ID, TEST_ADDRESS_CODE));
    }

    @Test
    public void testCheckAddressCodeExistsWrongOrgId() {
        long wrongOrgId = -1L;
        Assert.assertFalse(sut.checkAddressCodeExists(wrongOrgId, TEST_ADDRESS_NAME));
    }

    @Test
    public void testCheckAddressCodeExistsWrongCode() {
        String wrongAddrName = getClass().getName();
        Assert.assertFalse(sut.checkAddressCodeExists(ORG_ID, wrongAddrName));
    }

    @Test
    public void testGetCustomerAddressById() {
        UserAddressBookEntity address = sut.getUserAddressBookEntryById(ADDR_ID);
        Assert.assertNotNull(address);
        Assert.assertEquals(ADDR_ID, address.getId());
        Assert.assertEquals(ORG_ID, address.getOrgId());
        Assert.assertNotNull(address.getAddress());
        Assert.assertNotNull(address.getAddress().getCity());
        Assert.assertNotNull(address.getAddress().getState());
    }

    @Test
    public void testGetCustomerAddressByNameAndCode() {
        UserAddressBookEntity address = sut.getUserAddressBookEntryByNameAndCode("AAA", "219 ADDRESS", ORG_ID);
        Assert.assertNull(address);

        address = sut.getUserAddressBookEntryByNameAndCode("ADDR_NAME220", "219 ADDRESS", ORG_ID);
        Assert.assertNotNull(address);
        Assert.assertEquals("ADDR_NAME220", address.getAddressName());
        Assert.assertEquals("219 ADDRESS", address.getAddressCode());
        Assert.assertNotNull(address.getAddress());
        Assert.assertNotNull(address.getAddress().getCity());
        Assert.assertNotNull(address.getAddress().getState());
    }

    @Test
    public void testGetAddressesByName() {
        final String addressName = "ADDR";
        List<UserAddressBookEntity> addresses = sut.getAddressesByName(ORG_ID, USER_ID, addressName, false);

        Assert.assertNotNull(addresses);
        Assert.assertFalse(addresses.isEmpty());
        for (UserAddressBookEntity address : addresses) {
            Assert.assertNotNull(address);
            Assert.assertEquals(ORG_ID, address.getOrgId());
            Assert.assertTrue(address.getAddressName().toUpperCase().contains(addressName.toUpperCase()));
        }
    }

    @Test
    public void testGetAddressesByNameAndUser() {
        final String addressName = "ADDR";
        List<UserAddressBookEntity> addresses = sut.getAddressesByName(ORG_ID, USER_ID, addressName, false);

        Assert.assertNotNull(addresses);
        Assert.assertFalse(addresses.isEmpty());
        for (UserAddressBookEntity address : addresses) {
            Assert.assertNotNull(address);
            Assert.assertEquals(ORG_ID, address.getOrgId());
            Assert.assertTrue(address.getAddressName().toUpperCase().contains(addressName.toUpperCase()));
        }
    }

    @Test
    public void testGetAddressesByNameNoDataFound() {
        final String addressName = "Non Existing Address";
        List<UserAddressBookEntity> addresses = sut.getAddressesByName(ORG_ID, null, addressName, false);

        Assert.assertNotNull(addresses);
        Assert.assertTrue(addresses.isEmpty());
    }
    @After
    public void tierDown() {
        SecurityTestUtils.logout();
    }
    @Test
    public void testResetAndArchived() {
        SecurityTestUtils.login("Test", USER_ID);

        UserAddressBookEntity address = sut.getUserAddressBookEntryById(ADDR_ID);
        Assert.assertNotNull(address);
        String pickupNotes = "Pickup notes to be reset";
        address.setPickupNotes(pickupNotes);
        sut.resetAndArchived(address, USER_ID);
        flushAndClearSession();
        address = sut.getUserAddressBookEntryById(ADDR_ID);
        Assert.assertNotNull(address);
        Assert.assertNotSame(pickupNotes, address.getPickupNotes());
        Assert.assertEquals(Status.INACTIVE, address.getStatus());
        Assert.assertEquals(USER_ID, address.getModification().getModifiedBy());
    }

    @Test
    public void testGetAddressesByCodeAndOrgId() {
        UserAddressBookEntity address = sut.getCustomerAddressBookEntryByCode(ORG_ID, TEST_ADDRESS_CODE);
        Assert.assertNotNull(address);
        Assert.assertEquals(ORG_ID, address.getOrgId());
        Assert.assertEquals(TEST_ADDRESS_NAME, address.getAddressName());
    }

    private UserAddressBookEntity prepareTestEntity() {
        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode(STATE_CODE);
        statePK.setCountryCode(COUNTRY_CODE);
        state.setStatePK(statePK);

        CountryEntity country = new CountryEntity();
        country.setId(COUNTRY_CODE);


        UserAddressBookEntity entity = new UserAddressBookEntity();
        entity.setStatus(Status.ACTIVE);
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity("Cranberry");
        addressEntity.setCountry(country);
        addressEntity.setState(state);
        addressEntity.setAddress1("3120 Unionville Road");
        addressEntity.setZip("43210");

        entity.setAddress(addressEntity);
        entity.setOrgId(ORG_ID);

        entity.setAddressName(ADDRESS_NAME);
        entity.setContactName("Test contact name");
        entity.setEmail("test@test.com");
        entity.setAddressCode(LOCATION_CODE);

        Date pickupFrom;
        Date pickupTo;

        try {
            pickupFrom = simpleDateFormat.parse("2:30 AM");
            pickupTo = simpleDateFormat.parse("4:00 PM");
        } catch (ParseException e) {
            throw new IllegalStateException("Wrong format of time for ltl address pickup window");
        }
        entity.setPickupFrom(new Time(pickupFrom.getTime()));
        entity.setPickupFrom(new Time(pickupTo.getTime()));
        PhoneEntity phone = new PhoneEntity();

        phone.setAreaCode("724");
        phone.setCountryCode("1");
        phone.setNumber("7099000");
        phone.setType(PhoneType.VOICE);

        entity.setPhone(phone);

        return entity;
    }
}
