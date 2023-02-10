package com.pls.user.restful.dtobuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.enums.CustomerServiceContactInfoType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.NetworkUserEntity;
import com.pls.core.domain.user.PromoCodeEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserAdditionalContactInfoEntity;
import com.pls.core.domain.user.UserAddressEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.domain.user.UserNotificationsEntity;
import com.pls.core.domain.user.UserPhoneEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.user.domain.CapabilityEntity;
import com.pls.user.domain.UserCapabilityEntity;
import com.pls.user.domain.UserGroupEntity;
import com.pls.user.restful.dto.CustomerLocationUserDTO;
import com.pls.user.restful.dto.CustomerUserDTO;
import com.pls.user.restful.dto.UserDTO;
import com.pls.user.restful.dtobuilder.UserDTOBuilder.DataProvider;

/**
 * Test cases for {@link UserDTOBuilder}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class UserDTOBuilderTest {
    @Mock
    private DataProvider dataProvider;

    @InjectMocks
    private UserDTOBuilder sut;

    @Test
    public void shouldBuildDtoWithBaseFields() {
        UserEntity entity = createEntity(false, false);
        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildDtoWithFaxWithoutCustomer() {
        UserEntity entity = createEntity(true, false);
        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildDto() {
        UserEntity entity = createEntity(true, true);
        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildEntityWithBaseFields() {
        UserDTO dto = createDto(false, false);
        Long personId = dto.getPersonId();
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(getUserEntityWithId(personId));
        UserEntity entity = sut.buildEntity(dto);
        compareDtoAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildEntityWithFaxWithoutCustomer() {
        UserDTO dto = createDto(true, false);
        Long personId = dto.getPersonId();
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(getUserEntityWithId(personId));
        UserEntity entity = sut.buildEntity(dto);
        compareDtoAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildEntity() {
        UserDTO dto = createDto(true, true);
        Long personId = dto.getPersonId();
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(getUserEntityWithId(personId));
        UserEntity entity = sut.buildEntity(dto);
        compareDtoAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildDtoWithAssignedCustomers() {
        UserEntity entity = createEntity(true, false);
        // add assigned customers
        Long customerId1 = (long) (Math.random() * 100);
        Long customerId2 = (long) (Math.random() * 100) + 101;
        Long customerId3 = (long) (Math.random() * 100) + 202;
        Long customerId4 = (long) (Math.random() * 100) + 303;
        Long customerId5 = (long) (Math.random() * 100) + 404;
        List<String> notifications = Collections.unmodifiableList(Arrays.asList("1notification" + Math.random(),
                "2notification" + Math.random()));
        CustomerUserEntity customerUser1 = addCustomerUser(entity, customerId1, null);
        CustomerUserEntity customerUser11 = addCustomerUser(entity, customerId1, (long) (Math.random() * 100));
        customerUser11.getModification().setModifiedDate(DateUtility.truncateMilliseconds(DateUtils.addDays(new Date(), 1)));
        CustomerUserEntity customerUser12 = addCustomerUser(entity, customerId1, (long) (Math.random() * 100) + 101);
        CustomerUserEntity customerUser13 = addCustomerUser(entity, customerId1, (long) (Math.random() * 100) + 202);
        customerUser13.getNotifications().add(createNotification(notifications.get(0), Status.ACTIVE));
        customerUser13.getNotifications().add(createNotification("iNotification" + Math.random(), Status.INACTIVE));
        customerUser13.getNotifications().add(createNotification(notifications.get(1), Status.ACTIVE));
        CustomerUserEntity customerUser2 = addCustomerUser(entity, customerId2, null);
        addCustomerUser(entity, customerId3, null);
        CustomerUserEntity customerUser4 = addCustomerUser(entity, customerId4, null);
        CustomerUserEntity customerUser5 = addCustomerUser(entity, customerId5, (long) (Math.random() * 100));
        customerUser5.getModification().setModifiedDate(DateUtility.truncateMilliseconds(DateUtils.addDays(new Date(), 1)));
        CustomerUserEntity customerUser6 = addCustomerUser(entity, customerId5, (long) (Math.random() * 100) + 101);
        customerUser4.setStatus(Status.INACTIVE);

        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId1), Mockito.isNull(Long.class))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId1), Mockito.eq(customerUser11.getLocationId())))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId1), Mockito.eq(customerUser12.getLocationId())))
                .thenReturn(false);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId1), Mockito.eq(customerUser13.getLocationId())))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId2), Mockito.isNull(Long.class))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId3), Mockito.isNull(Long.class))).thenReturn(false);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId4), Mockito.isNull(Long.class))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId5), Mockito.eq(customerUser5.getLocationId())))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerId5), Mockito.eq(customerUser6.getLocationId())))
                .thenReturn(true);

        ArrayList<AssociatedCustomerLocationBO> customerLocations1 = new ArrayList<AssociatedCustomerLocationBO>();
        customerLocations1.add(new AssociatedCustomerLocationBO());
        AssociatedCustomerLocationBO location1 = new AssociatedCustomerLocationBO();
        location1.setAccountExecutive("accountExecutive" + Math.random());
        customerLocations1.add(location1);
        customerLocations1.add(new AssociatedCustomerLocationBO());
        Mockito.when(dataProvider.getCustomerLocationsAssignedToCurrentUser(customerId1)).thenReturn(customerLocations1);
        ArrayList<AssociatedCustomerLocationBO> customerLocations2 = new ArrayList<AssociatedCustomerLocationBO>();
        AssociatedCustomerLocationBO location2 = new AssociatedCustomerLocationBO();
        location2.setAccountExecutive("accountExecutive" + Math.random());
        customerLocations2.add(location2);
        AssociatedCustomerLocationBO location3 = new AssociatedCustomerLocationBO();
        location3.setAccountExecutive("1accountExecutive" + Math.random());
        customerLocations2.add(location3);
        Mockito.when(dataProvider.getCustomerLocationsAssignedToCurrentUser(customerId5)).thenReturn(customerLocations2);

        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);

        // verify customers
        Assert.assertEquals(3, dto.getCustomers().size());
        CustomerUserDTO customerUserDTO = findCustomer(dto.getCustomers(), customerId1);
        Assert.assertNotNull(customerUserDTO);
        Assert.assertEquals(customerId1, customerUserDTO.getCustomerId());
        Assert.assertEquals(customerUser1.getCustomer().getName(), customerUserDTO.getCustomerName());
        Assert.assertEquals(customerUser11.getModification().getModifiedDate().toString(), customerUserDTO.getAssignmentDate().toString());
        Assert.assertEquals(location1.getAccountExecutive(), customerUserDTO.getAccountExecutive());
        Assert.assertFalse(customerUserDTO.getMultipleAE());
        Assert.assertEquals(new Long(3), customerUserDTO.getLocationsCount());
        Assert.assertEquals(2, customerUserDTO.getLocations().size());
        Assert.assertEquals(customerUser11.getLocationId(), customerUserDTO.getLocations().get(0).getLocationId());
        Assert.assertTrue(customerUserDTO.getLocations().get(0).getNotifications().isEmpty());
        Assert.assertEquals(customerUser13.getLocationId(), customerUserDTO.getLocations().get(1).getLocationId());
        Assert.assertTrue(customerUserDTO.getLocations().get(1).getNotifications().containsAll(notifications));

        customerUserDTO = findCustomer(dto.getCustomers(), customerId2);
        Assert.assertNotNull(customerUserDTO);
        Assert.assertEquals(customerId2, customerUserDTO.getCustomerId());
        Assert.assertEquals(customerUser2.getCustomer().getName(), customerUserDTO.getCustomerName());
        Assert.assertEquals(customerUser2.getModification().getModifiedDate(), customerUserDTO.getAssignmentDate());
        Assert.assertNull(customerUserDTO.getAccountExecutive());
        Assert.assertFalse(customerUserDTO.getMultipleAE());
        Assert.assertEquals(new Long(0), customerUserDTO.getLocationsCount());
        Assert.assertEquals(0, customerUserDTO.getLocations().size());

        customerUserDTO = findCustomer(dto.getCustomers(), customerId5);
        Assert.assertNotNull(customerUserDTO);
        Assert.assertEquals(customerId5, customerUserDTO.getCustomerId());
        Assert.assertEquals(customerUser5.getCustomer().getName(), customerUserDTO.getCustomerName());
        Assert.assertEquals(customerUser5.getModification().getModifiedDate().toString(), customerUserDTO.getAssignmentDate().toString());
        Assert.assertEquals("Multiple Account Executives", customerUserDTO.getAccountExecutive());
        Assert.assertTrue(customerUserDTO.getMultipleAE());
        Assert.assertEquals(new Long(2), customerUserDTO.getLocationsCount());
        Assert.assertEquals(2, customerUserDTO.getLocations().size());
        Assert.assertEquals(customerUser5.getLocationId(), customerUserDTO.getLocations().get(0).getLocationId());
        Assert.assertTrue(customerUserDTO.getLocations().get(0).getNotifications().isEmpty());
        Assert.assertEquals(customerUser6.getLocationId(), customerUserDTO.getLocations().get(1).getLocationId());
        Assert.assertTrue(customerUserDTO.getLocations().get(1).getNotifications().isEmpty());
    }

    @Test
    @SuppressWarnings("unused")
    public void shouldBuildEntityWithAssignedCustomers() {
        UserDTO dto = createDto(true, false);
        // 1 new customer with locations (1 location wich customer has no access)
        CustomerUserDTO customer1 = addCustomer(dto, (long) (Math.random() * 100));
        CustomerLocationUserDTO location11 = addLocation(customer1, (long) (Math.random() * 100));
        List<String> notifications1 = Collections.unmodifiableList(Arrays.asList("1notification" + Math.random(),
                "2notification" + Math.random()));
        location11.getNotifications().addAll(notifications1);
        CustomerLocationUserDTO location12 = addLocation(customer1, (long) (Math.random() * 100) + 101);
        CustomerLocationUserDTO location13 = addLocation(customer1, (long) (Math.random() * 100) + 202);
        // 1 new customer without locations
        CustomerUserDTO customer2 = addCustomer(dto, (long) (Math.random() * 100) + 101);
        // 1 existing customer with new and removed locations
        CustomerUserDTO customer3 = addCustomer(dto, (long) (Math.random() * 100) + 202);
        CustomerLocationUserDTO location31 = addLocation(customer3, (long) (Math.random() * 100));
        List<String> notifications2 = Collections.unmodifiableList(Arrays.asList("1notification" + Math.random(),
                "2notification" + Math.random(), "3notification" + Math.random()));
        location31.getNotifications().addAll(notifications2);
        CustomerLocationUserDTO location32 = addLocation(customer3, (long) (Math.random() * 100) + 101);
        CustomerLocationUserDTO location33 = addLocation(customer3, (long) (Math.random() * 100) + 202);
        // 1 existing customer without locations (verify that locations are removed)
        CustomerUserDTO customer4 = addCustomer(dto, (long) (Math.random() * 100) + 303);
        // 1 customer with no access for current user
        CustomerUserDTO customer5 = addCustomer(dto, (long) (Math.random() * 100) + 404);

        Long personId = dto.getPersonId();
        UserEntity userEntity = getUserEntityWithId(personId);
        // 2 customers with locaitons (accessible and non accessible by current user)
        CustomerUserEntity customerUser1 = addCustomerUser(userEntity, customer3.getCustomerId(), null);
        CustomerUserEntity customerUser12 = addCustomerUser(userEntity, customer3.getCustomerId(), location31.getLocationId());
        UserNotificationsEntity notification1 = createNotification(notifications2.get(0), Status.INACTIVE);
        customerUser12.getNotifications().add(notification1);
        UserNotificationsEntity notification2 = createNotification(notifications2.get(1), Status.ACTIVE);
        customerUser12.getNotifications().add(notification2);
        UserNotificationsEntity notification3 = createNotification("4notificationType" + Math.random(), Status.ACTIVE);
        customerUser12.getNotifications().add(notification3);
        CustomerUserEntity customerUser13 = addCustomerUser(userEntity, customer3.getCustomerId(), (long) (Math.random() * 100) + 303);
        CustomerUserEntity customerUser14 = addCustomerUser(userEntity, customer3.getCustomerId(), (long) (Math.random() * 100) + 404);

        CustomerUserEntity customerUser2 = addCustomerUser(userEntity, customer4.getCustomerId(), null);
        CustomerUserEntity customerUser21 = addCustomerUser(userEntity, customer4.getCustomerId(), (long) (Math.random() * 100));
        CustomerUserEntity customerUser22 = addCustomerUser(userEntity, customer4.getCustomerId(), (long) (Math.random() * 100) + 101);

        // 1 customer with no access for current user
        CustomerUserEntity customerUser3 = addCustomerUser(userEntity, (long) (Math.random() * 100) + 505, (long) (Math.random() * 100));

        // 2 customers with access for current user that were removed
        CustomerUserEntity customerUser4 = addCustomerUser(userEntity, (long) (Math.random() * 100) + 606, null);
        CustomerUserEntity customerUser5 = addCustomerUser(userEntity, (long) (Math.random() * 100) + 707, (long) (Math.random() * 100));

        Mockito.when(dataProvider.findUserById(personId)).thenReturn(userEntity);

        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer1.getCustomerId()), Mockito.isNull(Long.class)))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer1.getCustomerId()),
                Mockito.eq(location11.getLocationId()))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer1.getCustomerId()),
                Mockito.eq(location12.getLocationId()))).thenReturn(false);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer1.getCustomerId()),
                Mockito.eq(location13.getLocationId()))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer2.getCustomerId()), Mockito.isNull(Long.class)))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer3.getCustomerId()), Mockito.isNull(Long.class)))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer3.getCustomerId()),
                Mockito.eq(location31.getLocationId()))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer3.getCustomerId()),
                Mockito.eq(location32.getLocationId()))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer3.getCustomerId()),
                Mockito.eq(location33.getLocationId()))).thenReturn(false);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer3.getCustomerId()),
                Mockito.eq(customerUser13.getLocationId()))).thenReturn(false);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer3.getCustomerId()),
                Mockito.eq(customerUser14.getLocationId()))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer4.getCustomerId()), Mockito.isNull(Long.class)))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer4.getCustomerId()),
                Mockito.eq(customerUser21.getLocationId()))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer4.getCustomerId()),
                Mockito.eq(customerUser22.getLocationId()))).thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customer5.getCustomerId()), Mockito.isNull(Long.class)))
                .thenReturn(false);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerUser4.getCustomerId()), Mockito.isNull(Long.class)))
                .thenReturn(true);
        Mockito.when(dataProvider.isCurrentUserAssignedToCustomerLocation(Mockito.eq(customerUser5.getCustomerId()),
                Mockito.eq(customerUser5.getLocationId()))).thenReturn(true);

        userEntity = sut.buildEntity(dto);
        compareDtoAndEntity(dto, userEntity);

        // verify assigned customers
        Assert.assertEquals(15, userEntity.getCustomerUsers().size());
        CustomerUserEntity customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer1.getCustomerId(), null, Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer1.getCustomerId(), location11.getLocationId(), Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertEquals(2, customerUser.getNotifications().size());
        Assert.assertNotNull(findNotification(customerUser, notifications1.get(0), Status.ACTIVE));
        Assert.assertNotNull(findNotification(customerUser, notifications1.get(1), Status.ACTIVE));
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer1.getCustomerId(), location13.getLocationId(), Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer2.getCustomerId(), null, Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer3.getCustomerId(), null, Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer3.getCustomerId(), location31.getLocationId(), Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertEquals(4, customerUser.getNotifications().size());
        Assert.assertNotNull(findNotification(customerUser, notifications2.get(0), Status.ACTIVE));
        Assert.assertNotNull(findNotification(customerUser, notifications2.get(1), Status.ACTIVE));
        Assert.assertNotNull(findNotification(customerUser, notifications2.get(2), Status.ACTIVE));
        Assert.assertNotNull(findNotification(customerUser, notification3.getNotificationType(), Status.INACTIVE));
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer3.getCustomerId(), location32.getLocationId(), Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer3.getCustomerId(), customerUser13.getLocationId(), Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer3.getCustomerId(), customerUser14.getLocationId(), Status.INACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer4.getCustomerId(), null, Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer4.getCustomerId(), customerUser21.getLocationId(), Status.INACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customer4.getCustomerId(), customerUser22.getLocationId(), Status.INACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customerUser3.getCustomerId(), customerUser3.getLocationId(), Status.ACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customerUser4.getCustomerId(), null, Status.INACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
        customerUser = findCustomerUser(userEntity.getCustomerUsers(), customerUser5.getCustomerId(), customerUser5.getLocationId(), Status.INACTIVE);
        Assert.assertNotNull(customerUser);
        Assert.assertTrue(customerUser.getNotifications().isEmpty());
    }

    @Test
    public void shouldBuildDtoWithNetworks() {
        UserEntity entity = createEntity(true, false);
        NetworkUserEntity network1 = addNetwork(entity, (long) (Math.random() * 100), Status.ACTIVE);
        addNetwork(entity, (long) (Math.random() * 100) + 101, Status.ACTIVE);
        NetworkUserEntity network2 = addNetwork(entity, (long) (Math.random() * 100) + 202, Status.INACTIVE);
        addNetwork(entity, (long) (Math.random() * 100) + 303, Status.INACTIVE);
        NetworkUserEntity network3 = addNetwork(entity, (long) (Math.random() * 100) + 404, Status.ACTIVE);

        List<SimpleValue> currentUserNetworks = new ArrayList<SimpleValue>();
        currentUserNetworks.add(new SimpleValue(network1.getNetworkId(), ""));
        currentUserNetworks.add(new SimpleValue(network2.getNetworkId(), ""));
        currentUserNetworks.add(new SimpleValue(network3.getNetworkId(), ""));
        Mockito.when(dataProvider.findNetworksForCurrentUser()).thenReturn(currentUserNetworks);

        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);

        Assert.assertEquals(2, dto.getNetworkIds().size());
        Assert.assertTrue(dto.getNetworkIds().contains(network1.getNetworkId()));
        Assert.assertTrue(dto.getNetworkIds().contains(network3.getNetworkId()));
    }

    @Test
    public void shouldBuildEntityWithNetworks() {
        UserDTO dto = createDto(true, true);
        Long networkId1 = (long) (Math.random() * 100);
        Long networkId2 = (long) (Math.random() * 100) + 101;
        Long networkId3 = (long) (Math.random() * 100) + 202;
        Long networkId4 = (long) (Math.random() * 100) + 303;
        dto.getNetworkIds().addAll(Arrays.asList(networkId1, networkId2, networkId3, networkId4));

        Long personId = dto.getPersonId();
        UserEntity user = getUserEntityWithId(personId);
        addNetwork(user, networkId2, Status.ACTIVE);
        addNetwork(user, networkId3, Status.INACTIVE);
        NetworkUserEntity network1 = addNetwork(user, (long) (Math.random() * 100) + 606, Status.ACTIVE);
        NetworkUserEntity network2 = addNetwork(user, (long) (Math.random() * 100) + 707, Status.INACTIVE);
        NetworkUserEntity network3 = addNetwork(user, (long) (Math.random() * 100) + 404, Status.ACTIVE);
        NetworkUserEntity network4 = addNetwork(user, (long) (Math.random() * 100) + 505, Status.INACTIVE);
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(user);

        List<SimpleValue> currentUserNetworks = new ArrayList<SimpleValue>();
        currentUserNetworks.add(new SimpleValue(networkId1, ""));
        currentUserNetworks.add(new SimpleValue(networkId2, ""));
        currentUserNetworks.add(new SimpleValue(networkId3, ""));
        currentUserNetworks.add(new SimpleValue(network1.getNetworkId(), ""));
        currentUserNetworks.add(new SimpleValue(network2.getNetworkId(), ""));
        Mockito.when(dataProvider.findNetworksForCurrentUser()).thenReturn(currentUserNetworks);

        user = sut.buildEntity(dto);
        compareDtoAndEntity(dto, user);

        Assert.assertEquals(7, user.getNetworkUsers().size());
        Assert.assertNotNull(findNetwork(user, networkId1, Status.ACTIVE));
        Assert.assertNotNull(findNetwork(user, networkId2, Status.ACTIVE));
        Assert.assertNotNull(findNetwork(user, networkId3, Status.ACTIVE));
        Assert.assertNotNull(findNetwork(user, network1.getNetworkId(), Status.INACTIVE));
        Assert.assertNotNull(findNetwork(user, network2.getNetworkId(), Status.INACTIVE));
        Assert.assertNotNull(findNetwork(user, network3.getNetworkId(), Status.ACTIVE));
        Assert.assertNotNull(findNetwork(user, network4.getNetworkId(), Status.INACTIVE));
    }

    @Test
    public void shouldBuildDtoWithContactInfo() {
        UserEntity entity = createEntity(true, false);
        UserAdditionalContactInfoBO defaultContactInfo = Mockito.mock(UserAdditionalContactInfoBO.class);
        UserAdditionalContactInfoBO userContactInfo = Mockito.mock(UserAdditionalContactInfoBO.class);

        Mockito.when(dataProvider.getContactInfo(null)).thenReturn(defaultContactInfo);
        Mockito.when(dataProvider.getContactInfo(entity)).thenReturn(userContactInfo);

        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);

        Mockito.verifyNoMoreInteractions(defaultContactInfo);
        Mockito.verifyNoMoreInteractions(userContactInfo);
        Assert.assertSame(defaultContactInfo, dto.getDefaultInfo());
        Assert.assertSame(userContactInfo, dto.getAdditionalInfo());
    }

    @Test
    public void shouldBuildEntityWithoutContactInfo() {
        UserDTO dto = createDto(true, false);
        dto.setAdditionalInfo(getContactInfo());
        dto.setCustomerServiceContactInfoType(Math.random() > 0.5 ? CustomerServiceContactInfoType.DEFAULT
                : CustomerServiceContactInfoType.SAME_AS_USER_PROFILE);
        Long personId = dto.getPersonId();
        UserEntity user = getUserEntityWithId(personId);
        UserAdditionalContactInfoEntity userContactInfo = Mockito.mock(UserAdditionalContactInfoEntity.class);
        user.setAdditionalInfo(userContactInfo);
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(user);

        user = sut.buildEntity(dto);
        compareDtoAndEntity(dto, user);

        Assert.assertNull(user.getAdditionalInfo());
        Mockito.verifyNoMoreInteractions(userContactInfo);
    }

    @Test
    public void shouldBuildEntityWithoutContactInfoForCustomType() {
        UserDTO dto = createDto(true, false);
        dto.setCustomerServiceContactInfoType(CustomerServiceContactInfoType.CUSTOM);
        Long personId = dto.getPersonId();
        UserEntity user = getUserEntityWithId(personId);
        UserAdditionalContactInfoEntity userContactInfo = Mockito.mock(UserAdditionalContactInfoEntity.class);
        user.setAdditionalInfo(userContactInfo);
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(user);

        user = sut.buildEntity(dto);
        compareDtoAndEntity(dto, user);

        Assert.assertNull(user.getAdditionalInfo());
        Mockito.verifyNoMoreInteractions(userContactInfo);
    }

    @Test
    public void shouldBuildEntityWithContactInfoForCustomType() {
        UserDTO dto = createDto(true, false);
        UserAdditionalContactInfoBO contactInfo = getContactInfo();
        dto.setAdditionalInfo(contactInfo);
        dto.setCustomerServiceContactInfoType(CustomerServiceContactInfoType.CUSTOM);
        Long personId = dto.getPersonId();
        UserEntity user = getUserEntityWithId(personId);
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(user);

        user = sut.buildEntity(dto);
        compareDtoAndEntity(dto, user);

        Assert.assertNotNull(user.getAdditionalInfo());
        Assert.assertEquals("EXTERNAL", user.getAdditionalInfo().getContactType());
        Assert.assertEquals(contactInfo.getContactName(), user.getAdditionalInfo().getContactName());
        Assert.assertEquals(contactInfo.getEmail(), user.getAdditionalInfo().getEmail());
        Assert.assertEquals(contactInfo.getPhone().getAreaCode(), user.getAdditionalInfo().getPhone().getAreaCode());
        Assert.assertEquals(contactInfo.getPhone().getCountryCode(), user.getAdditionalInfo().getPhone().getCountryCode());
        Assert.assertEquals(contactInfo.getPhone().getNumber(), user.getAdditionalInfo().getPhone().getNumber());
        Assert.assertEquals(PhoneType.VOICE, user.getAdditionalInfo().getPhone().getType());
    }

    @Test
    public void shouldBuildEntityWithContactInfoForCustomTypeWithExistingContactInfo() {
        UserDTO dto = createDto(true, false);
        UserAdditionalContactInfoBO contactInfo = getContactInfo();
        dto.setAdditionalInfo(contactInfo);
        dto.setCustomerServiceContactInfoType(CustomerServiceContactInfoType.CUSTOM);
        Long personId = dto.getPersonId();
        UserEntity user = getUserEntityWithId(personId);
        UserAdditionalContactInfoEntity userContactInfo = new UserAdditionalContactInfoEntity();
        String contactType = "" + Math.random();
        userContactInfo.setContactType(contactType);
        user.setAdditionalInfo(userContactInfo);
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(user);

        user = sut.buildEntity(dto);
        compareDtoAndEntity(dto, user);

        Assert.assertNotNull(user.getAdditionalInfo());
        Assert.assertEquals(contactType, user.getAdditionalInfo().getContactType());
        Assert.assertEquals(contactInfo.getContactName(), user.getAdditionalInfo().getContactName());
        Assert.assertEquals(contactInfo.getEmail(), user.getAdditionalInfo().getEmail());
        Assert.assertEquals(contactInfo.getPhone().getAreaCode(), user.getAdditionalInfo().getPhone().getAreaCode());
        Assert.assertEquals(contactInfo.getPhone().getCountryCode(), user.getAdditionalInfo().getPhone().getCountryCode());
        Assert.assertEquals(contactInfo.getPhone().getNumber(), user.getAdditionalInfo().getPhone().getNumber());
        Assert.assertEquals(PhoneType.VOICE, user.getAdditionalInfo().getPhone().getType());
    }

    @Test
    public void shouldBuildDtoWithRoles() {
        long currentUserId = (long) (Math.random() * 100) + 101;
        SecurityTestUtils.login("username", currentUserId);
        UserEntity entity = createEntity(true, false);

        List<UserGroupEntity> userGroups = new ArrayList<UserGroupEntity>();
        UserGroupEntity group1 = addUserGroup(userGroups, (long) (Math.random() * 100), Status.ACTIVE);
        UserGroupEntity group2 = addUserGroup(userGroups, (long) (Math.random() * 100) + 101, Status.ACTIVE);
        addUserGroup(userGroups, (long) (Math.random() * 100) + 202, Status.ACTIVE);
        UserGroupEntity group3 = addUserGroup(userGroups, (long) (Math.random() * 100) + 303, Status.INACTIVE);
        UserGroupEntity group4 = addUserGroup(userGroups, (long) (Math.random() * 100) + 404, Status.INACTIVE);
        addUserGroup(userGroups, (long) (Math.random() * 100) + 505, Status.INACTIVE);
        UserGroupEntity group5 = addUserGroup(userGroups, (long) (Math.random() * 100) + 606, Status.ACTIVE);

        List<UserGroupEntity> currentUserGroups = new ArrayList<UserGroupEntity>();
        addUserGroup(currentUserGroups, group1.getGroupId(), Status.ACTIVE);
        addUserGroup(currentUserGroups, group2.getGroupId(), Status.INACTIVE);
        addUserGroup(currentUserGroups, group3.getGroupId(), Status.ACTIVE);
        addUserGroup(currentUserGroups, group4.getGroupId(), Status.INACTIVE);
        addUserGroup(currentUserGroups, group5.getGroupId(), Status.ACTIVE);
        addUserGroup(currentUserGroups, (long) (Math.random() * 100) + 707, Status.INACTIVE);
        addUserGroup(currentUserGroups, (long) (Math.random() * 100) + 808, Status.ACTIVE);

        Mockito.when(dataProvider.getGroups(entity.getId())).thenReturn(userGroups);
        Mockito.when(dataProvider.getGroups(currentUserId)).thenReturn(currentUserGroups);

        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);

        Assert.assertEquals(2, dto.getRoles().size());
        Assert.assertTrue(dto.getRoles().contains(group1.getGroupId()));
        Assert.assertTrue(dto.getRoles().contains(group5.getGroupId()));
    }

    @Test
    public void shouldBuildDtoWithAdditionalPermissions() {
        String capability1 = "1capability" + Math.random();
        String capability2 = "2capability" + Math.random();
        String capability3 = "3capability" + Math.random();
        String capability4 = "4capability" + Math.random();
        SecurityTestUtils.login("username", (long) (Math.random() * 100) + 101, capability1, capability2, capability3, capability4);

        UserEntity entity = createEntity(true, false);

        List<UserCapabilityEntity> capabilities = new ArrayList<UserCapabilityEntity>();
        UserCapabilityEntity userCapability1 = addCapability(capabilities, (long) (Math.random() * 100), Status.ACTIVE, capability1);
        addCapability(capabilities, (long) (Math.random() * 100) + 101, Status.INACTIVE, capability2);
        UserCapabilityEntity userCapability2 = addCapability(capabilities, (long) (Math.random() * 100) + 202, Status.ACTIVE, capability4);
        Mockito.when(dataProvider.getCapabilities(entity.getId())).thenReturn(capabilities);

        UserDTO dto = sut.buildDTO(entity);
        compareDtoAndEntity(dto, entity);

        Assert.assertEquals(2, dto.getPermissions().size());
        Assert.assertTrue(dto.getPermissions().contains(userCapability1.getCapabilityId()));
        Assert.assertTrue(dto.getPermissions().contains(userCapability2.getCapabilityId()));
    }

    @Test
    public void shouldBuildEntityWithPromoCodeField() {
        UserDTO dto = createDto(false, false);
        dto.setDiscount(BigDecimal.ONE);
        dto.setPromoCode("promoCode1234556");
        Long personId = dto.getPersonId();
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(getUserEntityWithId(personId));
        UserEntity entity = sut.buildEntity(dto);
        compareDtoAndEntity(dto, entity);
        Set<PromoCodeEntity> promoCodes = entity.getPromoCodes();
        Assert.assertFalse(promoCodes.isEmpty());
        Assert.assertEquals(1, promoCodes.size());
        PromoCodeEntity promoCode = promoCodes.stream().findFirst().get();
        Assert.assertEquals(promoCode.getCode(), dto.getPromoCode());
        Assert.assertEquals(promoCode.getPercentage(), dto.getDiscount());
        Assert.assertEquals(promoCode.getStatus(), Status.ACTIVE);
    }

    @Test
    public void shouldUpdatePromoCodeField() {
        UserDTO dto = createDto(false, false);
        dto.setDiscount(BigDecimal.ONE);
        dto.setPromoCode("promoCode1234556");
        Long personId = dto.getPersonId();
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(buildPromoCode(getUserEntityWithId(personId)));
        UserEntity entity = sut.buildEntity(dto);
        compareDtoAndEntity(dto, entity);
        Set<PromoCodeEntity> promoCodes = entity.getPromoCodes();
        Assert.assertFalse(promoCodes.isEmpty());
        Assert.assertEquals(2, promoCodes.size());
        for (PromoCodeEntity promoCodeEntity : promoCodes) {
            if (promoCodeEntity.getCode().equals("PROMO_CODE_TEST")) {
                Assert.assertEquals(promoCodeEntity.getStatus(), Status.INACTIVE);
            }
            if (promoCodeEntity.getCode().equals("promoCode1234556")) {
                Assert.assertEquals(promoCodeEntity.getStatus(), Status.ACTIVE);
            }
        }
    }

    @Test
    public void shouldRemovePromoCode() {
        UserDTO dto = createDto(false, false);
        Long personId = dto.getPersonId();
        Mockito.when(dataProvider.findUserById(personId)).thenReturn(buildPromoCode(getUserEntityWithId(personId)));
        UserEntity entity = sut.buildEntity(dto);
        compareDtoAndEntity(dto, entity);
        Set<PromoCodeEntity> promoCodes = entity.getPromoCodes();
        Assert.assertFalse(promoCodes.isEmpty());
        Assert.assertEquals(1, promoCodes.size());
        PromoCodeEntity promoCode = promoCodes.stream().findFirst().get();
        Assert.assertEquals(promoCode.getStatus(), Status.INACTIVE);
    }

    private UserEntity buildPromoCode(UserEntity user) {
        PromoCodeEntity promoCode = new PromoCodeEntity();
        promoCode.setAccountExecutive(user);
        promoCode.setCode("PROMO_CODE_TEST");
        promoCode.setPercentage(BigDecimal.ONE);
        promoCode.setStatus(com.pls.core.shared.Status.ACTIVE);
        promoCode.setAccountExecutive(user);
        user.getPromoCodes().add(promoCode);
        return user;
    }

    private UserCapabilityEntity addCapability(List<UserCapabilityEntity> capabilities, long capabilityId, Status status, String capabilityName) {
        UserCapabilityEntity userCapability = new UserCapabilityEntity();
        userCapability.setCapabilityId(capabilityId);
        userCapability.setStatus(status);
        CapabilityEntity capability = new CapabilityEntity();
        capability.setName(capabilityName);
        userCapability.setCapability(capability);
        capabilities.add(userCapability);
        return userCapability;
    }

    private UserGroupEntity addUserGroup(List<UserGroupEntity> userGroups, Long groupId, Status status) {
        UserGroupEntity group = new UserGroupEntity();
        group.setGroupId(groupId);
        group.setStatus(status);
        userGroups.add(group);
        return group;
    }

    private UserAdditionalContactInfoBO getContactInfo() {
        UserAdditionalContactInfoBO additionalInfo = new UserAdditionalContactInfoBO();
        additionalInfo.setContactName("contactName" + Math.random());
        additionalInfo.setEmail("email" + Math.random());
        additionalInfo.setPhone(getPhone());
        return additionalInfo;
    }

    private UserNotificationsEntity createNotification(String notificationType, Status status) {
        UserNotificationsEntity notification1 = new UserNotificationsEntity();
        notification1.setNotificationType(notificationType);
        notification1.setStatus(status);
        return notification1;
    }

    private UserNotificationsEntity findNotification(CustomerUserEntity entity, String notificationType, Status status) {
        for (UserNotificationsEntity notification : entity.getNotifications()) {
            if (notification.getNotificationType().equals(notificationType) && status == notification.getStatus()) {
                return notification;
            }
        }
        return null;
    }

    private NetworkUserEntity findNetwork(UserEntity entity, Long networkId, Status status) {
        for (NetworkUserEntity network : entity.getNetworkUsers()) {
            if (network.getNetworkId().equals(networkId) && status == network.getStatus()) {
                return network;
            }
        }
        return null;
    }

    private NetworkUserEntity addNetwork(UserEntity entity, Long networkId, Status status) {
        NetworkUserEntity network = new NetworkUserEntity();
        network.setNetworkId(networkId);
        network.setStatus(status);
        entity.getNetworkUsers().add(network);
        return network;
    }

    private CustomerLocationUserDTO addLocation(CustomerUserDTO customer, Long locationId) {
        CustomerLocationUserDTO location = new CustomerLocationUserDTO();
        location.setLocationId(locationId);
        customer.getLocations().add(location);
        return location;
    }

    private CustomerUserDTO addCustomer(UserDTO dto, Long customerId) {
        CustomerUserDTO customer = new CustomerUserDTO();
        customer.setCustomerId(customerId);
        customer.setLocations(new ArrayList<CustomerLocationUserDTO>());
        dto.getCustomers().add(customer);
        return customer;
    }

    private CustomerUserEntity findCustomerUser(List<CustomerUserEntity> customers, Long customerId, Long locationId, Status status) {
        for (CustomerUserEntity customer : customers) {
            if (customer.getCustomerId().equals(customerId) && ObjectUtils.equals(locationId, customer.getLocationId())
                    && status == customer.getStatus()) {
                return customer;
            }
        }
        return null;
    }

    private CustomerUserDTO findCustomer(List<CustomerUserDTO> customers, Long customerId) {
        for (CustomerUserDTO customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    private UserEntity getUserEntityWithId(Long personId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(personId);
        return userEntity;
    }

    private UserDTO createDto(boolean addFax, boolean addCustomerUser) {
        UserDTO dto = new UserDTO();
        dto.setPersonId((long) (Math.random() * 100));
        dto.setFirstName("firstName" + Math.random());
        dto.setLastName("lastName" + Math.random());
        dto.setEmail("email" + Math.random());
        dto.setAddress1("address1" + Math.random());
        int contactInfoIndex = (int) ((CustomerServiceContactInfoType.values().length - 1) * Math.random());
        dto.setCustomerServiceContactInfoType(CustomerServiceContactInfoType.values()[contactInfoIndex]);
        CountryDTO country = new CountryDTO("coutnry" + Math.random());
        dto.setCountry(country);
        ZipDTO zip = new ZipDTO();
        zip.setCity("city" + Math.random());
        zip.setCountry(country);
        zip.setState("state" + Math.random());
        zip.setZip("zip" + Math.random());
        dto.setZip(zip);
        dto.setPhone(getPhone());
        if (addFax) {
            dto.setFax(getPhone());
        }
        if (addCustomerUser) {
            CustomerUserDTO customerUserDto = new CustomerUserDTO();
            customerUserDto.setCustomerId((long) (Math.random() * 100));
            customerUserDto.setCustomerName("customerName" + Math.random());
            dto.getCustomers().add(customerUserDto);
        }
        ParentOrganizationBO parentOrganization = new ParentOrganizationBO();
        parentOrganization.setCustomer(true);
        parentOrganization.setOrganizationId((long) (Math.random() * 100));
        parentOrganization.setOrganizationName("orgName" + Math.random());
        dto.setParentOrganization(parentOrganization);
        return dto;
    }

    private PhoneBO getPhone() {
        PhoneBO phone = new PhoneBO();
        phone.setCountryCode("countryCode" + Math.random());
        phone.setAreaCode("areaCode" + Math.random());
        phone.setNumber("phoneNumber" + Math.random());
        return phone;
    }

    private UserEntity createEntity(boolean addFax, boolean addCustomerUser) {
        UserEntity entity = getUserEntityWithId((long) (Math.random() * 100));
        entity.setLogin("login" + Math.random());
        entity.setFirstName("firstName" + Math.random());
        entity.setLastName("lastName" + Math.random());
        entity.setUserStatus(UserStatus.ACTIVE);
        entity.setEmail("email" + Math.random());
        int contactInfoIndex = (int) ((CustomerServiceContactInfoType.values().length - 1) * Math.random());
        entity.setCustomerServiceContactInfoType(CustomerServiceContactInfoType.values()[contactInfoIndex]);
        UserAddressEntity userAddressEntity = new UserAddressEntity();
        AddressEntity address = new AddressEntity();
        address.setAddress1("address1" + Math.random());
        CountryEntity country = new CountryEntity();
        country.setId("country" + Math.random());
        address.setCountry(country);
        address.setStateCode("stateCode" + Math.random());
        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setCountryCode("countryCode" + Math.random());
        statePK.setStateCode("stateCode" + Math.random());
        state.setStatePK(statePK);
        address.setState(state);
        address.setCity("city" + Math.random());
        address.setZip("zip" + Math.random());
        address.setStatus(Status.ACTIVE);
        userAddressEntity.setAddress(address);
        userAddressEntity.setUser(entity);
        entity.getAddresses().add(userAddressEntity);
        entity.getPhones().add(
                createPhoneEntity("countryCode" + Math.random(), "areaCode" + Math.random(), "number" + Math.random(), PhoneType.VOICE));
        if (addFax) {
            entity.getPhones().add(
                    createPhoneEntity("countryCode" + Math.random(), "areaCode" + Math.random(), "number" + Math.random(), PhoneType.FAX));
        }
        if (addCustomerUser) {
            addCustomerUser(entity, (long) (Math.random() * 100), (long) (Math.random() * 100));
        }
        addParentOrganization(entity);
        return entity;
    }

    private void addParentOrganization(UserEntity entity) {
        OrganizationEntity parentOrganization = new CustomerEntity();
        parentOrganization.setId((long) (Math.random() * 100));
        parentOrganization.setName("name" + Math.random());
        entity.setParentOrganization(parentOrganization);
        entity.setParentOrgId(parentOrganization.getId());
    }

    private CustomerUserEntity addCustomerUser(UserEntity entity, Long customerId, Long locationId) {
        CustomerUserEntity customerUserEntity = new CustomerUserEntity();
        customerUserEntity.setUser(entity);
        customerUserEntity.setCustomer(createCustomer(customerId));
        customerUserEntity.setStatus(Status.ACTIVE);
        customerUserEntity.setLocationId(locationId);
        entity.getCustomerUsers().add(customerUserEntity);
        return customerUserEntity;
    }

    private UserPhoneEntity createPhoneEntity(String countryCode, String areaCode, String number, PhoneType type) {
        UserPhoneEntity result = new UserPhoneEntity();
        result.setStatus(Status.ACTIVE);
        result.setCountryCode(countryCode);
        result.setAreaCode(areaCode);
        result.setNumber(number);
        result.setType(type);
        return result;
    }

    private CustomerEntity createCustomer(Long customerId) {
        CustomerEntity result = new CustomerEntity();
        result.setId(customerId);
        result.setName("name" + Math.random());
        return result;
    }

    private void compareDtoAndEntity(UserDTO dto, UserEntity entity) {
        Assert.assertEquals(dto.getPersonId(), entity.getId());
        Assert.assertEquals(dto.getUserId(), entity.getLogin());
        Assert.assertEquals(dto.getFirstName(), entity.getFirstName());
        Assert.assertEquals(dto.getLastName(), entity.getLastName());
        Assert.assertEquals(dto.getEmail(), entity.getEmail());
        Assert.assertEquals(dto.getCustomerServiceContactInfoType(), dto.getCustomerServiceContactInfoType());
        AddressEntity address = entity.getUserAddress().getAddress();
        Assert.assertEquals(StringUtils.lowerCase(dto.getAddress1()), StringUtils.lowerCase(address.getAddress1()));
        Assert.assertEquals(dto.getAddress2(), address.getAddress2());
        Assert.assertEquals(dto.getCountry().getId(), address.getCountry().getId());
        Assert.assertEquals(StringUtils.lowerCase(dto.getZip().getState()), StringUtils.lowerCase(address.getStateCode()));
        Assert.assertEquals(StringUtils.lowerCase(dto.getZip().getZip()), StringUtils.lowerCase(address.getZip()));
        UserPhoneEntity phone = entity.getActiveUserPhoneByType(PhoneType.VOICE);
        Assert.assertEquals(dto.getPhone().getCountryCode(), phone.getCountryCode());
        Assert.assertEquals(dto.getPhone().getAreaCode(), phone.getAreaCode());
        Assert.assertEquals(dto.getPhone().getNumber(), phone.getNumber());
        Assert.assertEquals(PhoneType.VOICE, phone.getType());
        PhoneBO faxDto = dto.getFax();
        UserPhoneEntity faxEntity = entity.getActiveUserPhoneByType(PhoneType.FAX);
        Assert.assertEquals(faxDto != null, faxEntity != null);
        if (faxDto != null && faxEntity != null) {
            Assert.assertEquals(faxDto.getCountryCode(), faxEntity.getCountryCode());
            Assert.assertEquals(faxDto.getAreaCode(), faxEntity.getAreaCode());
            Assert.assertEquals(faxDto.getNumber(), faxEntity.getNumber());
            Assert.assertEquals(PhoneType.FAX, faxEntity.getType());
        }
        Assert.assertNotNull(dto.getParentOrganization());
        Assert.assertEquals(dto.getParentOrganization().getOrganizationId(), entity.getParentOrgId());
    }
}
