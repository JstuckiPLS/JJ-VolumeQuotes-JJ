package com.pls.core.domain.user;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CustomerEntity;

/**
 * Test of Hibernate mapping for {@link UserPhoneEntity} class.
 * 
 * @author Maxim Medvedev
 */
public class UserPhoneEntityMappingIT extends AbstractDaoTest {

    private static final Long CUSTOMER_ID = 1L;

    private static final Long PERSON_ID = -1L;

    @Test
    public void testCascadeCreate() {
        UserEntity user = getEntity(UserEntity.class, PERSON_ID);
        CustomerEntity org = getEntity(CustomerEntity.class, CUSTOMER_ID);

        CustomerUserEntity customerUser = new CustomerUserEntity();
        customerUser.setCustomer(org);
        customerUser.setUser(user);

        UserPhoneEntity phone = createNewPhone(PhoneType.VOICE);
        phone.setCustomerUser(customerUser);
        customerUser.getPhones().add(phone);

        getSession().saveOrUpdate(customerUser);
        flushAndClearSession();

        Assert.assertNotNull(phone.getId());
    }

    @Test
    public void testCascadeUpdate() {
        UserEntity user = getEntity(UserEntity.class, PERSON_ID);
        CustomerEntity org = getEntity(CustomerEntity.class, CUSTOMER_ID);

        CustomerUserEntity customerUser = new CustomerUserEntity();
        customerUser.setCustomer(org);
        customerUser.setUser(user);

        UserPhoneEntity phone = createNewPhone(PhoneType.VOICE);
        phone.setCustomerUser(customerUser);
        customerUser.getPhones().add(phone);

        getSession().saveOrUpdate(customerUser);
        flushAndClearSession();

        CustomerUserEntity newCustomerUser = getEntity(CustomerUserEntity.class, customerUser.getId());
        Assert.assertNotSame(customerUser, newCustomerUser);
        UserPhoneEntity newPhone = newCustomerUser.getPhones().get(0);
        Assert.assertNotSame(phone, newPhone);

        newPhone.setType(PhoneType.FAX);
        getSession().saveOrUpdate(newCustomerUser);
        flushAndClearSession();

        Assert.assertEquals(PhoneType.FAX, getEntity(UserPhoneEntity.class, newPhone.getId()).getType());
    }

    @Test
    public void testCreation() {
        UserEntity user = getEntity(UserEntity.class, PERSON_ID);
        CustomerEntity org = getEntity(CustomerEntity.class, CUSTOMER_ID);

        CustomerUserEntity customerUser = new CustomerUserEntity();
        customerUser.setCustomer(org);
        customerUser.setUser(user);

        save(customerUser);

        UserPhoneEntity phone = createNewPhone(PhoneType.VOICE);
        phone.setCustomerUser(customerUser);
        customerUser.getPhones().add(phone);

        getSession().saveOrUpdate(phone);
        flushAndClearSession();

        Assert.assertNotNull(phone.getId());
    }

    private UserPhoneEntity createNewPhone(PhoneType type) {
        UserPhoneEntity result = new UserPhoneEntity();
        result.setCountryCode("1");
        result.setAreaCode("11");
        result.setNumber("22");
        result.setType(type);
        return result;
    }

}
