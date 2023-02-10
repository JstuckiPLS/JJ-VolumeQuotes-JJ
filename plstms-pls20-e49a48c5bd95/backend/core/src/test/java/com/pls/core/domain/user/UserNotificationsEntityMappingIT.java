package com.pls.core.domain.user;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;

/**
 * Test of Hibernate mapping for {@link UserNotificationsEntity} class.
 * 
 * @author Maxim Medvedev
 */
public class UserNotificationsEntityMappingIT extends AbstractDaoTest {

    private static final Long CUSTOMER_ID = 1L;

    private static final Long PERSON_ID = -1L;

    private static final String TYPE_1 = "DISPATCHED";

    private static final String TYPE_2 = "PICK_UP";

    @Test
    public void testCascadeCreate() {
        UserEntity user = getEntity(UserEntity.class, PERSON_ID);
        CustomerEntity org = getEntity(CustomerEntity.class, CUSTOMER_ID);

        CustomerUserEntity customerUser = new CustomerUserEntity();
        customerUser.setCustomer(org);
        customerUser.setUser(user);

        UserNotificationsEntity notification = createNew(TYPE_1);
        notification.setCustomerUser(customerUser);
        customerUser.getNotifications().add(notification);

        getSession().saveOrUpdate(customerUser);
        flushAndClearSession();

        Assert.assertNotNull(notification.getId());
    }

    @Test
    public void testCascadeUpdate() {
        UserEntity user = getEntity(UserEntity.class, PERSON_ID);
        CustomerEntity org = getEntity(CustomerEntity.class, CUSTOMER_ID);

        CustomerUserEntity customerUser = new CustomerUserEntity();
        customerUser.setCustomer(org);
        customerUser.setUser(user);

        UserNotificationsEntity notification = createNew(TYPE_1);
        notification.setCustomerUser(customerUser);
        customerUser.getNotifications().add(notification);

        getSession().saveOrUpdate(customerUser);
        flushAndClearSession();

        CustomerUserEntity newCustomerUser = getEntity(CustomerUserEntity.class, customerUser.getId());
        Assert.assertNotSame(customerUser, newCustomerUser);
        UserNotificationsEntity newNotification = newCustomerUser.getNotifications().get(0);
        Assert.assertNotSame(notification, newNotification);

        newNotification.setNotificationType(TYPE_2);
        getSession().saveOrUpdate(newCustomerUser);
        flushAndClearSession();

        Assert.assertEquals(TYPE_2, getEntity(UserNotificationsEntity.class, newNotification.getId())
                .getNotificationType());
    }

    @Test
    public void testCreation() {
        UserEntity user = getEntity(UserEntity.class, PERSON_ID);
        CustomerEntity org = getEntity(CustomerEntity.class, CUSTOMER_ID);

        CustomerUserEntity customerUser = new CustomerUserEntity();
        customerUser.setCustomer(org);
        customerUser.setUser(user);

        save(customerUser);

        UserNotificationsEntity notification = createNew(TYPE_1);
        notification.setCustomerUser(customerUser);
        customerUser.getNotifications().add(notification);

        getSession().saveOrUpdate(notification);
        flushAndClearSession();

        Assert.assertNotNull(notification.getId());
    }

    private UserNotificationsEntity createNew(String type) {
        UserNotificationsEntity result = new UserNotificationsEntity();
        result.setNotificationType(type);
        result.setStatus(Status.ACTIVE);
        return result;
    }

}
