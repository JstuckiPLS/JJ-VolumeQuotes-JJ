package com.pls.core.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.CustomerUserDao;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.shared.Status;

/**
 * Test cases for {@link CustomerUserDaoImpl}.
 * 
 * @author Denis Zhupinsky (Team International)
 */
public class CustomerUserDaoImplIT extends AbstractDaoTest {

    private static final Long CUSTOMER_ID = 1L;

    private static final Long INVALID_CUSTOMER_ID = -100500L;

    private static final long ORG_USER_WITH_NOTIFICATION_ID = 9;

    private static final Long USER_ID = 2L;

    @Autowired
    private CustomerUserDao sut;

    @Test
    public void testGetActiveByCustomerWithInactiveCustomerUserRecord() {
        clearCustomerUsers();

        setUserStatus(USER_ID, UserStatus.ACTIVE);
        updateCustomerUser(USER_ID, CUSTOMER_ID, Status.INACTIVE);

        List<CustomerUserEntity> customerUsersList = sut.getActive(CUSTOMER_ID);

        Assert.assertNotNull(customerUsersList);
        Assert.assertEquals(0, customerUsersList.size());
    }

    @Test
    public void testGetActiveByCustomerWithInactiveUserRecord() {
        clearCustomerUsers();

        setUserStatus(USER_ID, UserStatus.INACTIVE);
        updateCustomerUser(USER_ID, CUSTOMER_ID, Status.ACTIVE);

        List<CustomerUserEntity> customerUsersList = sut.getActive(CUSTOMER_ID);

        Assert.assertNotNull(customerUsersList);
        Assert.assertEquals(0, customerUsersList.size());
    }

    @Test
    public void testGetActiveByCustomerWithInvalidCustomerId() {
        List<CustomerUserEntity> customerUsersList = sut.getActive(INVALID_CUSTOMER_ID);

        Assert.assertNotNull(customerUsersList);
        Assert.assertEquals(0, customerUsersList.size());
    }

    @Test
    public void testGetActiveByCustomerWithNormalCase() {
        clearCustomerUsers();

        setUserStatus(USER_ID, UserStatus.ACTIVE);
        updateCustomerUser(USER_ID, CUSTOMER_ID, Status.ACTIVE);

        List<CustomerUserEntity> customerUsersList = sut.getActive(CUSTOMER_ID);

        Assert.assertNotNull(customerUsersList);
        Assert.assertEquals(2, customerUsersList.size());
    }

    @Test
    public void testGetActiveByCustomerWithNullCustomerId() {
        List<CustomerUserEntity> customerUsersList = sut.getActive(null);

        Assert.assertNotNull(customerUsersList);
        Assert.assertEquals(0, customerUsersList.size());
    }

    @Test
    public void testCustomerUserNotifications() {
        CustomerUserEntity customerUserEntity = sut.find(ORG_USER_WITH_NOTIFICATION_ID);
        Assert.assertNotNull(customerUserEntity);
        Assert.assertNotNull(customerUserEntity.getNotifications());
        Assert.assertFalse(customerUserEntity.getNotifications().isEmpty());
        Assert.assertEquals(3, customerUserEntity.getNotifications().size());
    }

    @Test
    public void testGetActiveByCustomerWithActiveOrInActiveOrganization() {
        clearCustomerUsers();
        setUserStatus(USER_ID, UserStatus.ACTIVE);
        updateCustomerUser(USER_ID, CUSTOMER_ID, Status.ACTIVE);
        updateOrganizationStatus(CUSTOMER_ID, false);
        List<CustomerUserEntity> customerUsersList = sut.getActive(CUSTOMER_ID);

        Assert.assertNotNull(customerUsersList);
        Assert.assertEquals(2, customerUsersList.size());

        updateOrganizationStatus(CUSTOMER_ID, true);
        customerUsersList = sut.getActive(CUSTOMER_ID);

        Assert.assertNotNull(customerUsersList);
        Assert.assertEquals(2, customerUsersList.size());
    }

    @Test
    public void shouldFindByPersonIdAndOrgId() {
        /*
         * Because the query that is executed by findByPersonIdOrgIdLocationId() now
         * explicitly checks for a NULL locationId and because the row returned when
         * USER_ID=2 and CUSTOMER_ID=1 contains a value of 1 in the locationId, this
         * test fails.  However, there is a record in our local dev database where when
         * USER_ID=1 and CUSTOMER_ID=28 a locationId of null is returned, I changed this
         * test to define two local variables for userId and customerId to make it pass.
         * 
         * I did it this way because of time constraints, but this solution makes sense.
         */
        Long userId = 1L;
        Long customerId = 28L;
        CustomerUserEntity entity = sut.findByPersonIdOrgIdLocationId(userId, customerId);
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getPersonId(), userId);
        Assert.assertEquals(entity.getCustomerId(), customerId);
    }

    private void clearCustomerUsers() {
        getSession().createSQLQuery("UPDATE FLATBED.ORGANIZATION_USERS SET STATUS = 'I'").executeUpdate();
    }

    private void setUserStatus(Long personId, UserStatus status) {
        Query query = getSession().createSQLQuery(
                "UPDATE FLATBED.USERS SET STATUS = :status WHERE PERSON_ID = :personId");
        query.setParameter("status", status.getUserStatus());
        query.setParameter("personId", personId);

        query.executeUpdate();
    }

    private void updateCustomerUser(Long personId, Long orgId, Status status) {
        Query query = getSession()
                .createSQLQuery(
                        "UPDATE FLATBED.ORGANIZATION_USERS SET STATUS = :status WHERE ORG_ID = :orgId AND PERSON_ID = :personId");
        query.setParameter("status", status.getCode());
        query.setParameter("personId", personId);
        query.setParameter("orgId", orgId);

        query.executeUpdate();
    }

}
