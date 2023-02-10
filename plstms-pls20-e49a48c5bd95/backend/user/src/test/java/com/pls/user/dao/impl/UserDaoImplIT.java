package com.pls.user.dao.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.UserSearchType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.user.dao.UserDao;
import com.pls.user.domain.bo.UserCustomerBO;
import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.domain.bo.UserNotificationsBO;

/**
 * Test cases for {@link UserDaoImpl} class.
 *
 * @author Maxim Medvedev
 */
public class UserDaoImplIT extends AbstractDaoTest {
    public static final long CUSTOMER_ID = 1L;

    private static final Long INVALID_USER_ID = -100500L;

    private static final Long TEST_USER_ID = 2L;

    @Autowired
    private UserDao sut;

    @Before
    public void setUp() {
        SecurityTestUtils.login("TestUser", -1L);
    }

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testIsNewUserIdWithNormalCase() {
        String validLogin = getEntity(UserEntity.class, TEST_USER_ID).getUserId();

        Assert.assertTrue(sut.isValidNewUserId(validLogin, TEST_USER_ID));
    }

    @Test
    public void testIsNewUserIdWithNullId() {
        String validLogin = getEntity(UserEntity.class, TEST_USER_ID).getUserId();

        Assert.assertFalse(sut.isValidNewUserId(validLogin, null));
    }


    @Test
    public void testIsNewUserIdWithNullUserId() {
        Assert.assertTrue(sut.isValidNewUserId(null, TEST_USER_ID));
    }

    @Test
    public void testIsNewUserIdWithEmpty() {
        Assert.assertTrue(sut.isValidNewUserId("", TEST_USER_ID));
    }

    @Test
    public void testIsNewUserIdWithDifferentCases() {
        String validLogin = getEntity(UserEntity.class, TEST_USER_ID).getUserId();

        Assert.assertFalse(sut.isValidNewUserId(validLogin.toLowerCase(Locale.ENGLISH), null));
        Assert.assertFalse(sut.isValidNewUserId(validLogin.toUpperCase(Locale.ENGLISH), null));
    }

    @Test
    public void testIsNewUserIdWithInvalidLogin() {
        String validLogin = getEntity(UserEntity.class, TEST_USER_ID).getUserId();

        Assert.assertTrue(sut.isValidNewUserId(validLogin + RandomStringUtils.randomAlphabetic(10), TEST_USER_ID));
    }

    @Test(expected = ConstraintViolationException.class)
    @Ignore // FIXME unless we enable constraints
    public void testChangeStatusWithInvalidCurrentPersonId() {
        sut.changeStatus(TEST_USER_ID, UserStatus.INACTIVE, INVALID_USER_ID);
    }

    @Test
    public void testChangeStatusWithInvalidPersonId() {
        sut.changeStatus(INVALID_USER_ID, UserStatus.INACTIVE, TEST_USER_ID);
    }

    @Test
    public void testChangeStatusWithNormalCase() {
        sut.changeStatus(TEST_USER_ID, UserStatus.INACTIVE, TEST_USER_ID);

        flushAndClearSession();

        UserEntity actual = getSession().get(UserEntity.class, TEST_USER_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(TEST_USER_ID, actual.getId());
        Assert.assertEquals(UserStatus.INACTIVE, actual.getUserStatus());
        Assert.assertEquals(TEST_USER_ID, actual.getModification().getModifiedBy());
    }

    @Test
    public void testChangeStatusWithNullCurrentPersonId() {
        sut.changeStatus(TEST_USER_ID, UserStatus.INACTIVE, null);
    }

    @Test
    public void testChangeStatusWithNullPersonId() {
        sut.changeStatus(null, UserStatus.INACTIVE, TEST_USER_ID);
    }

    @Test
    public void testChangeStatusWithNullStatus() {
        sut.changeStatus(TEST_USER_ID, null, TEST_USER_ID);
    }

    @Test
    public void testCreateUserWithOrganization() {
        UserEntity user = createUser();

        CustomerEntity customer = getSession().get(CustomerEntity.class, 1L);

        CustomerUserEntity customerUser = new CustomerUserEntity();
        customerUser.setCustomer(customer);
        customerUser.setUser(user);
        user.getCustomerUsers().clear();
        user.getCustomerUsers().add(customerUser);

        sut.persist(user);
        flushAndClearSession();
    }

    @Test
    public void testCreateUserWithoutOrganization() {
        UserEntity user = createUser();
        sut.persist(user);
        flushAndClearSession();
    }

    @Test
    public void testFindUserJoinPhones() {
        UserEntity user = sut.find(TEST_USER_ID);
        Assert.assertNotNull(user);
        Assert.assertSame(2L, user.getId());
    }

    @Test
    public void testFindWithInvalidId() {
        UserEntity result = sut.find(Long.MIN_VALUE);

        Assert.assertNull(result);
    }

    @Test
    public void testFindWithNormalCase() {
        UserEntity result = sut.find(TEST_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertSame(TEST_USER_ID, result.getId());
    }

    @Test
    public void testFindWithNullId() {
        UserEntity result = sut.find(null);

        Assert.assertNull(result);
    }

    @Test
    public void testGetAdminsCount() {
        Long adminsCount = sut.getAdminUsersCountForCustomer(1L);
        Assert.assertEquals(new Long(1), adminsCount);
    }

    @Test
    public void testGetParentUser() {
        UserEntity parentUser = sut.getParentUser(TEST_USER_ID);
        Assert.assertNotNull(parentUser);
        Assert.assertTrue(parentUser.getId() == 1L);
    }

    @Test
    public void testGetParentUserWithNotExists() {
        UserEntity result = sut.getParentUser(INVALID_USER_ID);
        Assert.assertNull(result);
    }

    @Test
    public void testMasterUserFlag() {
        UserEntity user = sut.find(TEST_USER_ID);
        Assert.assertTrue(user.isMasterUser());
    }

    @Test
    public void testUpdate() {
        UserEntity user = sut.find(TEST_USER_ID);
        user.setFirstName("new fist name");
        sut.update(user);

        flushAndClearSession();

        user = sut.find(TEST_USER_ID);
        Assert.assertEquals("new fist name", user.getFirstName());
    }

    @Test
    public void testUserNotifications() {
        List<UserNotificationsBO> notifications = sut.getUserDefaultNotifications(1L, 1L);
        Assert.assertNotNull(notifications);
        Assert.assertEquals(3, notifications.size());

    }

    @Test
    public void testGetUserAdditionalInfo() {
        UserEntity user = sut.find(CUSTOMER_ID);
        Assert.assertNotNull(user);
        UserAdditionalContactInfoEntity additionalInfo = user.getAdditionalInfo();
        Assert.assertNotNull(additionalInfo);
        Assert.assertEquals(user.getId(), additionalInfo.getUser().getPersonId());
    }

    @Test
    public void shouldFindAllActiveUsersByBusinessUnit() {
        List<UserListItemBO> users = sut.searchUsers(1L, UserStatus.ACTIVE, 6L, false, "PLS PRO", UserSearchType.EMAIL, null);

        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("sysadmin", users.get(0).getUserId());
        Assert.assertEquals("EDIADMIN", users.get(1).getUserId());
    }

    @Test
    public void shouldSearchUsersByParameters() {
        List<UserListItemBO> users = sut.searchUsers(1L, UserStatus.INACTIVE, null, false, "MACSTEEL", UserSearchType.ID, "QUEST");

        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("QUEST", users.get(0).getUserId());
        Assert.assertEquals("quest@test.com", users.get(0).getEmail());
        Assert.assertEquals("DAVID", users.get(0).getFirstName());
        Assert.assertEquals("TRANTHAM", users.get(0).getLastName());
        Assert.assertEquals(new Long(2), users.get(0).getParentOrgId());
        Assert.assertEquals("MACSTEEL", users.get(0).getParentOrgName());
    }

    private UserEntity createUser() {
        UserEntity result = new UserEntity();
        result.setEmail("mail@mail.us");
        result.setFirstName("firstName");
        result.setLastName("lastName");
        result.setLogin(RandomStringUtils.random(10));
        result.setPassword("password");
        result.setUserStatus(UserStatus.ACTIVE);
        return result;
    }

    @Test
    public void shouldGetCustomersAssociatedWithUser() {
        List<UserCustomerBO> result = sut.getCustomersAssociatedWithUserByCriteria(1L, 2L, "MAC%");
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        UserCustomerBO customer = result.get(0);
        Assert.assertNotNull(customer);
        Assert.assertEquals(new Long(2), customer.getCustomerId());
        Assert.assertEquals("MACSTEEL", customer.getCustomerName());
        Assert.assertEquals("admin sysadmin", customer.getAccountExecutive());
        Assert.assertFalse(customer.getMultipleAE());
        Assert.assertEquals(new Long(58), customer.getLocationId());
        Assert.assertEquals(new Long(4), customer.getLocationsCount());
    }

    @Test
    public void shouldGetCustomersAssociatedWithUserByAE() {
        List<UserCustomerBO> result = sut.getCustomersAssociatedWithUserByAE(1L, null, "rich%");
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        UserCustomerBO customer = result.get(0);
        Assert.assertNotNull(customer);
        Assert.assertEquals(new Long(1), customer.getCustomerId());
        Assert.assertEquals("PLS SHIPPER", customer.getCustomerName());
        Assert.assertEquals("Multiple Account Executives", customer.getAccountExecutive());
        Assert.assertTrue(customer.getMultipleAE());
        Assert.assertEquals(new Long(2), customer.getLocationId());
        Assert.assertEquals(new Long(2), customer.getLocationsCount());
    }
}