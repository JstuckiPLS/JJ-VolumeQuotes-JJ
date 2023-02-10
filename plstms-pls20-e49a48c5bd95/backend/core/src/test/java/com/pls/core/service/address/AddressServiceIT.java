package com.pls.core.service.address;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.Status;

/**
 * Test for {@link com.pls.core.service.address.AddressService}.
 * 
 * @author Aleksandr Leshchenko
 */
public class AddressServiceIT extends BaseServiceITClass {
    private static final String USER_NAME = "userName" + Math.random();

    @Autowired
    AddressService service;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void shouldDuplicateAddressOnUpdate() throws ValidationException {
        UserAddressBookEntity addressBookEntity = service.getCustomerAddressById(1L);
        assertNotNull(addressBookEntity);
        AddressEntity addressEntity = addressBookEntity.getAddress();
        assertNotNull(addressEntity);
        long previousAddressEntityId = addressEntity.getId();

        service.saveOrUpdate(addressBookEntity, 1L, 2L, true, true);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        addressBookEntity = service.getCustomerAddressById(1L);
        assertNotNull(addressBookEntity);
        addressEntity = addressBookEntity.getAddress();
        assertNotNull(addressEntity);
        assertNotSame(previousAddressEntityId, addressEntity.getId());
    }

    @Test
    public void shouldValidateAddressNameAndLocationCodeUniqueness() throws ValidationException {
        SecurityTestUtils.login(USER_NAME);

        UserAddressBookEntity address = createUserAddressBook();

        service.saveOrUpdate(address, SecurityTestUtils.DEFAULT_ORGANIZATION_ID, SecurityTestUtils.DEFAULT_PERSON_ID,
                true);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        address.setId(null);
        address.getAddress().setId(null);

        try {
            service.saveOrUpdate(address, SecurityTestUtils.DEFAULT_ORGANIZATION_ID,
                    SecurityTestUtils.DEFAULT_PERSON_ID, true);
            fail();
        } catch (ValidationException e) {
            assertSame(ValidationError.UNIQUE, e.getErrors().get("addressCode"));
            assertSame(ValidationError.UNIQUE, e.getErrors().get("addressName"));
        }
    }

    private UserAddressBookEntity createUserAddressBook() {
        UserAddressBookEntity userAddressBookEntity = new UserAddressBookEntity();
        userAddressBookEntity.setContactName("contactName" + Math.random());
        userAddressBookEntity.setAddress(createAddress());
        userAddressBookEntity.setAddressName("addressName" + Math.random());
        userAddressBookEntity.setAddressCode("LocCDTest1");
        userAddressBookEntity.setEmail("test@test.com");
        userAddressBookEntity.setStatus(Status.ACTIVE);
        userAddressBookEntity.setPhone(createPhone());
        return userAddressBookEntity;
    }

    private PhoneEntity createPhone() {
        PhoneEntity phone = new PhoneEntity();
        phone.setType(PhoneType.VOICE);
        phone.setCountryCode("1");
        phone.setAreaCode("111");
        phone.setNumber("1234567");

        return phone;
    }

    private AddressEntity createAddress() {
        AddressEntity address = new AddressEntity();
        address.setAddress1("Address1" + Math.random());
        address.setState(createState());
        address.setCity("EMERYVILLE");
        CountryEntity country = new CountryEntity();
        country.setId("USA");
        address.setCountry(country);
        address.setZip("94608");
        return address;
    }

    private StateEntity createState() {
        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setCountryCode("USA");
        statePK.setStateCode("CA");
        state.setStatePK(statePK);
        return state;
    }
}
