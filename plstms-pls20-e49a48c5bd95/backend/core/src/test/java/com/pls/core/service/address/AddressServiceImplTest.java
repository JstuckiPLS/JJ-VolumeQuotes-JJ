package com.pls.core.service.address;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.pls.core.dao.CustomerUserDao;
import com.pls.core.dao.TimeZoneDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.address.impl.AddressServiceImpl;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;

/**
 * Test for {@link AddressServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressServiceImplTest {

    private static final long ORGANIZATION_ID = 1;

    private static final long ORG_USR_ID = 1;

    private static final long USER_ID = 2;

    private static final long MODIFIED_USER_ID = 3;

    private static final String COUNTRY_CODE = "USA";

    private static final String ZIP_CODE = "90026";

    private static final String CITY = "LOS ANGELES";

    @InjectMocks
    private AddressServiceImpl service;

    private List<Long> subordinates;

    @Mock
    private Validator<UserAddressBookEntity> validator;
    @Mock
    private UserAddressBookDao userAddressBookDao;

    @Mock
    private CustomerUserDao customerUserDao;

    @Mock
    TimeZoneDao timeZoneDao;

    @Before
    public void init() throws ValidationException {
        subordinates = new ArrayList<Long>();
        subordinates.add(2L);
        subordinates.add(3L);

        SecurityTestUtils.login("TEst", USER_ID, ORGANIZATION_ID);

        MockitoAnnotations.initMocks(this);

        service = new AddressServiceImpl();

        service.setUserAddressBookDao(userAddressBookDao);
        service.setValidator(validator);
        service.setTimeZoneDao(timeZoneDao);

        final ArrayList<UserAddressBookEntity> addresses = new ArrayList<UserAddressBookEntity>();
        addresses.add(new UserAddressBookEntity());
        when(userAddressBookDao.findCustomerAddressByCountryAndZip(anyLong(), anyLong(), anyString(), anyString(), anyString(),
                eq(Lists.newArrayList()))).thenReturn(addresses);
    }

    @Test
    public void shouldGetAddressesByZip() {
        SecurityTestUtils.login("Test");
        final List<UserAddressBookEntity> result = service.getCustomerAddressBookByCountryAndZip(COUNTRY_CODE,
                ZIP_CODE, CITY, ORGANIZATION_ID, USER_ID, new AddressType[0]);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        verify(userAddressBookDao).findCustomerAddressByCountryAndZip(ORGANIZATION_ID, USER_ID, COUNTRY_CODE, ZIP_CODE, CITY, Lists.newArrayList());
    }

    @Test
    public void shouldSaveAddress() throws ValidationException {
        final UserAddressBookEntity address = new UserAddressBookEntity();
        address.setAddress(new AddressEntity());
        address.setPhone(new PhoneEntity());
        address.setFax(new PhoneEntity());

        service.saveOrUpdate(address, ORGANIZATION_ID, USER_ID, false);

        verify(validator).validate(address);
        verify(userAddressBookDao).saveOrUpdate(argThat(new ArgumentMatcher<UserAddressBookEntity>() {
            @Override
            public boolean matches(final Object argument) {
                final UserAddressBookEntity address = (UserAddressBookEntity) argument;
                return address.getOrgId() == ORGANIZATION_ID
                        && address.getModification().getCreatedBy() == USER_ID
                        && address.getModification().getModifiedBy() == USER_ID
                        && address.getPhone().getModification().getCreatedBy() == USER_ID
                        && address.getPhone().getModification().getModifiedBy() == USER_ID
                        && address.getFax().getModification().getCreatedBy() == USER_ID
                        && address.getFax().getModification().getModifiedBy() == USER_ID;
            }
        }));

    }

    @Test
    public void shouldUpdateAddress() throws ValidationException {
        final UserAddressBookEntity address = createAddressWithCustomerUser(ORG_USR_ID);
        address.setId(1L);
        address.getModification().setCreatedBy(MODIFIED_USER_ID);
        address.getModification().setModifiedBy(MODIFIED_USER_ID);
        address.setAddress(new AddressEntity());
        address.setPhone(new PhoneEntity());
        address.getPhone().getModification().setCreatedBy(MODIFIED_USER_ID);
        address.getPhone().getModification().setModifiedBy(MODIFIED_USER_ID);
        address.setFax(new PhoneEntity());
        address.getFax().getModification().setCreatedBy(MODIFIED_USER_ID);
        address.getFax().getModification().setModifiedBy(MODIFIED_USER_ID);

        service.saveOrUpdate(address, ORGANIZATION_ID, USER_ID, false);

        verify(validator).validate(address);
        verify(userAddressBookDao).saveOrUpdate(argThat(new ArgumentMatcher<UserAddressBookEntity>() {
            @Override
            public boolean matches(final Object argument) {
                final UserAddressBookEntity address = (UserAddressBookEntity) argument;
                return address.getOrgId() == ORGANIZATION_ID;
            }
        }));
    }

    @Test
    // TODO Check logic of this test
    public void shouldUpdateAddressOfAnotherUserWithSharedPrivilege() throws ValidationException {
        final long anotherUser = (long) (Math.random() * 100) + 200;
        final UserAddressBookEntity address = createAddressWithCustomerUser(ORG_USR_ID);
        address.setId(1L);
        address.getModification().setCreatedBy(anotherUser);

        SecurityTestUtils.login("Test", USER_ID, ORGANIZATION_ID,
 Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name());

        service.saveOrUpdate(address, ORGANIZATION_ID, USER_ID, false);

        verify(validator).validate(address);
        verify(userAddressBookDao).saveOrUpdate(argThat(new ArgumentMatcher<UserAddressBookEntity>() {
            @Override
            public boolean matches(final Object argument) {
                final UserAddressBookEntity address = (UserAddressBookEntity) argument;
                return address.getOrgId() == ORGANIZATION_ID && address.getModification().getCreatedBy() == anotherUser
                        && address.getModification().getModifiedBy() == USER_ID;
            }
        }));
    }

    @Test
    public void shouldUpdateAddressOfSubordinateUserWithoutPrivilege() throws ValidationException {
        final long anotherUser = (long) (Math.random() * 100) + 200;
        final UserAddressBookEntity address = createAddressWithCustomerUser(ORG_USR_ID);
        address.setId(1L);
        address.getModification().setCreatedBy(anotherUser);

        service.saveOrUpdate(address, ORGANIZATION_ID, USER_ID, false);

        Mockito.verify(validator).validate(address);
        Mockito.verify(userAddressBookDao).saveOrUpdate(Matchers.argThat(new ArgumentMatcher<UserAddressBookEntity>() {
            @Override
            public boolean matches(final Object argument) {
                final UserAddressBookEntity address = (UserAddressBookEntity) argument;
                return address.getOrgId() == ORGANIZATION_ID && address.getModification().getCreatedBy() == anotherUser
                        && address.getModification().getModifiedBy() == USER_ID;
            }
        }));
    }

    @Test
    public void shouldUpdateAddressOfSubordinateUserWithSelfPrivilege() throws ValidationException {
        final long anotherUser = (long) (Math.random() * 100) + 200;
        final UserAddressBookEntity address = createAddressWithCustomerUser(ORG_USR_ID);
        address.setId(1L);
        address.getModification().setCreatedBy(anotherUser);

        SecurityTestUtils.login("Test", USER_ID, ORGANIZATION_ID, Capabilities.CAN_CREATE_ADDRESSES_WITH_SELF_OPTION.name());

        service.saveOrUpdate(address, ORGANIZATION_ID, USER_ID, false);

        Mockito.verify(validator).validate(address);
        Mockito.verify(userAddressBookDao).saveOrUpdate(Matchers.argThat(new ArgumentMatcher<UserAddressBookEntity>() {
            @Override
            public boolean matches(final Object argument) {
                final UserAddressBookEntity address = (UserAddressBookEntity) argument;
                return address.getOrgId() == ORGANIZATION_ID && address.getModification().getCreatedBy() == anotherUser
                        && address.getModification().getModifiedBy() == USER_ID;
            }
        }));
    }

    @Test
    public void testDeleteAddress() throws EntityNotFoundException {
        final Long addressId = new Random().nextLong();
        final UserAddressBookEntity address = new UserAddressBookEntity();
        Mockito.when(userAddressBookDao.deleteUserAddressBookEntry(addressId, USER_ID)).thenReturn(true);
        Mockito.when(userAddressBookDao.find(addressId)).thenReturn(address);

        SecurityTestUtils.login("Test", Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name());
        Assert.assertTrue(service.deleteAddressBookEntry(addressId, USER_ID));
    }

    @Test
    public void testGetAddressByOrgAndId() {
        final Long addrId = new Random().nextLong();
        final UserAddressBookEntity result = new UserAddressBookEntity();
        Mockito.when(userAddressBookDao.getUserAddressBookEntryById(addrId)).thenReturn(result);
        SecurityTestUtils.login("Test");
        Assert.assertSame(result, service.getCustomerAddressById(addrId));

        SecurityTestUtils.login("Test");
        result.setOrgId(ORGANIZATION_ID);
        Assert.assertSame(result, service.getCustomerAddressById(addrId));

        SecurityTestUtils.login("Test");
        result.setPersonId(null);
        Assert.assertSame(result, service.getCustomerAddressById(addrId));
    }

    @Test
    public void testGetCustomerAddressBook() {
        final List<UserAddressBookEntity> list = new ArrayList<UserAddressBookEntity>();
        Mockito.when(userAddressBookDao.getCustomerAddressBookForUser(ORGANIZATION_ID, USER_ID, true, Lists.newArrayList())).thenReturn(list);
        SecurityTestUtils.login("Test");
        Assert.assertSame(list, service.getCustomerAddressBookForUser(ORGANIZATION_ID, USER_ID, true, new AddressType[0]));
    }

    @Test
    public void shouldFindTimeZone() {
        when(timeZoneDao.findByCountryZip(COUNTRY_CODE, ZIP_CODE)).thenReturn(new TimeZoneEntity());
        service.findTimeZoneByCountryZip(COUNTRY_CODE, ZIP_CODE);
        verify(timeZoneDao).findByCountryZip(COUNTRY_CODE, ZIP_CODE);
    }

    private UserAddressBookEntity createAddressWithCustomerUser(Long orgUsrId) {
        final UserAddressBookEntity address = new UserAddressBookEntity();
        address.setAddress(new AddressEntity());
        address.setPhone(new PhoneEntity());
        address.setPersonId(orgUsrId);
        address.getModification().setCreatedBy(USER_ID);
        return address;
    }
}
