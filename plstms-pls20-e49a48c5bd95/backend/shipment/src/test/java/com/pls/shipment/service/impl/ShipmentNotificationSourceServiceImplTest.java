package com.pls.shipment.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CustomerUserDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.UserAddressEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.domain.user.UserPhoneEntity;
import com.pls.shipment.domain.bo.ShipmentNotificationSourceItemBo;

/**
 * Test case for {@link ShipmentNotificationSourceServiceImpl}.
 * 
 * @author Alexander Kirichenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentNotificationSourceServiceImplTest {
    private static final Long ORG_ID = 1L;

    private static final Long USER_ID = 1L;

    @Mock
    private CustomerUserDao customerUserDao;

    @InjectMocks
    private ShipmentNotificationSourceServiceImpl service;

    @Mock
    private UserAddressBookDao userAddressBookDao;

    @Test
    public void shouldGetShipmentNotificationSourceItems() {
        List<CustomerUserEntity> activeCustomerUsers = prepareActiveCustomerUsers();
        List<UserAddressBookEntity> addressBookEntityList = prepareUserAddressBookList();

        Mockito.when(customerUserDao.getActive(ORG_ID)).thenReturn(activeCustomerUsers);
        Mockito.when(userAddressBookDao.getCustomerAddressBookForUser(ORG_ID, USER_ID, true, Arrays.asList(AddressType.values())))
                .thenReturn(addressBookEntityList);
        List<ShipmentNotificationSourceItemBo> result = service.getShipmentNotificationSourceItems(ORG_ID,
                USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(prepareShipmentNotificationSourceItems(), result);
    }

    private ShipmentNotificationSourceItemBo createShipmentNotificationSourceItemBo(Long itemId,
            String email, String name, String contactName, String origin, PhoneNumber phoneNumber) {
        ShipmentNotificationSourceItemBo bo = new ShipmentNotificationSourceItemBo();
        bo.setId(itemId);
        bo.setEmail(email);
        bo.setName(name);
        bo.setContactName(contactName);
        bo.setPhone(phoneNumber);
        bo.setOrigin(origin);
        return bo;
    }

    private List<CustomerUserEntity> prepareActiveCustomerUsers() {
        ArrayList<CustomerUserEntity> result = new ArrayList<CustomerUserEntity>();
        CustomerUserEntity customerUserEntity = new CustomerUserEntity();
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setLogin("USER");
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        customerUserEntity.setUser(user);
        customerUserEntity.setId(1L);
        UserPhoneEntity phoneEntity = new UserPhoneEntity();
        phoneEntity.setId(1L);
        phoneEntity.setCustomerUser(customerUserEntity);
        phoneEntity.setCountryCode("001");
        phoneEntity.setAreaCode("111");
        phoneEntity.setNumber("1234567");
        phoneEntity.setType(PhoneType.VOICE);
        customerUserEntity.getPhones().add(phoneEntity);
        CustomerEntity organization = new CustomerEntity();
        organization.setName("name1");
        customerUserEntity.setCustomer(organization);
        UserAddressEntity userAddressEntity = new UserAddressEntity();
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity("CRANBERRY TWP");
        addressEntity.setStateCode("PA");
        addressEntity.setZip("16066");
        userAddressEntity.setAddress(addressEntity);
        customerUserEntity.getAddresses().add(userAddressEntity);
        result.add(customerUserEntity);
        return result;
    }

    private List<ShipmentNotificationSourceItemBo> prepareShipmentNotificationSourceItems() {
        ArrayList<ShipmentNotificationSourceItemBo> result = new ArrayList<ShipmentNotificationSourceItemBo>();
        PhoneEntity phoneNumber = new PhoneEntity();
        phoneNumber.setId(1L);
        phoneNumber.setCountryCode("001");
        phoneNumber.setAreaCode("111");
        phoneNumber.setNumber("1234567");
        phoneNumber.setType(PhoneType.VOICE);
        PhoneEntity faxNumber = new PhoneEntity();
        faxNumber.setId(1L);
        faxNumber.setCountryCode("001");
        faxNumber.setAreaCode("111");
        faxNumber.setNumber("1234567");
        faxNumber.setType(PhoneType.FAX);
        result.add(createShipmentNotificationSourceItemBo(1L, "test@test.com", "name1", "John Doe", "CRANBERRY TWP, PA, 16066", phoneNumber));
        result.add(createShipmentNotificationSourceItemBo(2L, "test2@test.com", null, "Jane Doe",  "CRANBERRY TWP, PA, 16066", phoneNumber));
        return result;
    }

    private List<UserAddressBookEntity> prepareUserAddressBookList() {
        ArrayList<UserAddressBookEntity> result = new ArrayList<UserAddressBookEntity>();
        UserAddressBookEntity userAddressBookEntity = new UserAddressBookEntity();
        userAddressBookEntity.setId(2L);
        userAddressBookEntity.setEmail("test2@test.com");
        userAddressBookEntity.setContactName("Jane Doe");
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setId(1L);
        phoneEntity.setCountryCode("001");
        phoneEntity.setAreaCode("111");
        phoneEntity.setNumber("1234567");
        phoneEntity.setType(PhoneType.VOICE);
        userAddressBookEntity.setPhone(phoneEntity);
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity("CRANBERRY TWP");
        addressEntity.setStateCode("PA");
        addressEntity.setZip("16066");
        userAddressBookEntity.setAddress(addressEntity);
        result.add(userAddressBookEntity);
        return result;
    }
}
