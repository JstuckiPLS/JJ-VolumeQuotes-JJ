package com.pls.restful.address;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.address.ZipCodePK;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dtobuilder.address.CountryDTOBuilder;

/**
 * Tests for {@link AddressResource}.
 * 
 * @author Brichak Aleksandr
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressResourceTest {

    @InjectMocks
    private AddressResource sut;

    @Mock
    private UserPermissionsService userPermissionsService;

    @Mock
    private AddressService addressService;

    @Mock
    CountryDTOBuilder countryDTOBuilder;

    @Mock
    private DBUtilityService dbUtilityService;

    @Mock
    UserAddressBookEntity addressBookEntity;

    @Mock
    AddressBookEntryDTO addressBookEntryDTO;

    @Mock
    AddressEntity addressEntity;

    private static final Long ORG_ID = 1L;

    private static final Long USER_ID = 2L;

    private static final Long ADDRESS_ID = 1L;

    @Before
    public void setUp() {
        SecurityTestUtils.logout();
    }

    @Test(expected = ApplicationException.class)
    public void testSaveSelfAddressWithoutSelfPermission() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        when(userPermissionsService.hasCapability(Capabilities.CAN_CREATE_ADDRESSES_WITH_SELF_OPTION.name()))
                .thenReturn(false);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTO(false, USER_ID);
        sut.saveAddress(ORG_ID, dto);
    }

    @Test
    public void testSaveSelfAddressWithSelfPermission() throws Exception {
        SecurityTestUtils.login("Test", ORG_ID);

        when(userPermissionsService.hasCapability(Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name()))
                .thenReturn(true);
        when(userPermissionsService.hasCapability(Capabilities.CAN_CREATE_ADDRESSES_WITH_SELF_OPTION.name()))
                .thenReturn(true);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTO(true, USER_ID);
        sut.saveAddress(USER_ID, dto);
        verify(addressService, times(1)).saveOrUpdate(eq(new UserAddressBookEntity()), eq(USER_ID), eq(ORG_ID),
                eq(true), eq(false));
    }

    @Test
    public void testGetSharedAddress() throws Exception {
        SecurityTestUtils.login("Test", ORG_ID);
        when(userPermissionsService.hasCapability(Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name()))
                .thenReturn(true);

        when(addressService.getCustomerAddressById(ADDRESS_ID)).thenReturn(createUserAddressBookEntity(true));
        when(addressEntity.getCountry()).thenReturn(new CountryEntity());
        AddressBookEntryDTO dto = sut.findAddress(USER_ID, ADDRESS_ID, null, null);
        Assert.assertNotNull(dto);
    }

    @Test
    public void testGetNotSharedAddress() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);

        when(addressService.getCustomerAddressById(ADDRESS_ID)).thenReturn(createUserAddressBookEntity(false));
        when(addressEntity.getCountry()).thenReturn(new CountryEntity());
        AddressBookEntryDTO dto = sut.findAddress(USER_ID, ADDRESS_ID, null, null);
        Assert.assertNotNull(dto);
    }

    @Test(expected = ApplicationException.class)
    public void testGetNotSharedAddressCreatedDifferentUser() throws Exception {
        SecurityTestUtils.login("Test", ORG_ID);

        when(addressService.getCustomerAddressById(ADDRESS_ID)).thenReturn(createUserAddressBookEntity(false));
        when(addressEntity.getCountry()).thenReturn(new CountryEntity());
        AddressBookEntryDTO dto = sut.findAddress(USER_ID, ADDRESS_ID, null, null);
        Assert.assertNotNull(dto);
    }

    @Test
    public void testGetSharedAddressCreatedDifferentUser() throws Exception {
        SecurityTestUtils.login("Test", ORG_ID);

        when(addressService.getCustomerAddressById(ADDRESS_ID)).thenReturn(createUserAddressBookEntity(true));
        when(addressEntity.getCountry()).thenReturn(new CountryEntity());
        AddressBookEntryDTO dto = sut.findAddress(USER_ID, ADDRESS_ID, null, null);
        Assert.assertNotNull(dto);
    }

    @Test(expected = ApplicationException.class)
    public void testSaveAddressWithoutPickupWindow() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTOWithPickUpAndDelivery(null,
                new PickupAndDeliveryWindowDTO(1, 0, true), new PickupAndDeliveryWindowDTO(1, 0, true),
                new PickupAndDeliveryWindowDTO(1, 0, true));
        sut.saveAddress(ORG_ID, dto);
    }

    @Test(expected = ApplicationException.class)
    public void testSaveAddressWithoutPickupOrDeliveryWindow1() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTOWithPickUpAndDelivery(new PickupAndDeliveryWindowDTO(1,
                0, true), null, new PickupAndDeliveryWindowDTO(1, 0, true),
                new PickupAndDeliveryWindowDTO(1, 0, true));
        sut.saveAddress(ORG_ID, dto);
    }

    @Test(expected = ApplicationException.class)
    public void testSaveAddressWithoutPickupOrDeliveryWindow2() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTOWithPickUpAndDelivery(new PickupAndDeliveryWindowDTO(1,
                0, true), new PickupAndDeliveryWindowDTO(1, 0, true), null, new PickupAndDeliveryWindowDTO(1, 0, true));
        sut.saveAddress(ORG_ID, dto);
    }

    @Test(expected = ApplicationException.class)
    public void testSaveAddressWithoutPickupOrDeliveryWindow3() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTOWithPickUpAndDelivery(new PickupAndDeliveryWindowDTO(1,
                0, true), new PickupAndDeliveryWindowDTO(1, 0, true), new PickupAndDeliveryWindowDTO(1, 0, true), null);
        sut.saveAddress(ORG_ID, dto);
    }

    @Test
    public void testSaveAddressWithPickupOrDeliveryWindow() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTOWithPickUpAndDelivery(new PickupAndDeliveryWindowDTO(1,
                0, true), new PickupAndDeliveryWindowDTO(2, 0, true), new PickupAndDeliveryWindowDTO(1, 0, true),
                new PickupAndDeliveryWindowDTO(2, 0, true));
        sut.saveAddress(ORG_ID, dto);
        verify(addressService, times(1)).saveOrUpdate(eq(new UserAddressBookEntity()), eq(ORG_ID), eq(USER_ID),
                eq(true), eq(false));
    }

    @Test(expected = ApplicationException.class)
    public void testSaveAddressWithWrongPickupOrDeliveryWindow() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTOWithPickUpAndDelivery(new PickupAndDeliveryWindowDTO(3,
                0, true), new PickupAndDeliveryWindowDTO(2, 0, true), new PickupAndDeliveryWindowDTO(1, 0, true),
                new PickupAndDeliveryWindowDTO(2, 0, true));
        sut.saveAddress(ORG_ID, dto);
    }

    @Test(expected = ApplicationException.class)
    public void testSaveAddressWithWrongPickupOrDeliveryWindow1() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        AddressBookEntryDTO dto = createTestAddressBookEntryDTOWithPickUpAndDelivery(new PickupAndDeliveryWindowDTO(1,
                0, true), new PickupAndDeliveryWindowDTO(2, 0, true), new PickupAndDeliveryWindowDTO(1, 15, true),
                new PickupAndDeliveryWindowDTO(2, 0, true));
        sut.saveAddress(ORG_ID, dto);
    }

    private AddressBookEntryDTO createTestAddressBookEntryDTO(boolean isShared, Long userId) {
        AddressBookEntryDTO addressDTO = new AddressBookEntryDTO();
        addressDTO.setId(userId);
        addressDTO.setSharedAddress(isShared);
        addressDTO.setCreatedBy(userId);
        addressDTO.setZip(new ZipDTO());
        addressDTO.setCountry(new CountryDTO());
        return addressDTO;

    }

    private UserAddressBookEntity createUserAddressBookEntity(boolean isShared) {
        UserAddressBookEntity entity = new UserAddressBookEntity();
        entity.getModification().setCreatedBy(USER_ID);
        entity.setAddress(createAddressEntity());
        entity.setAddressCode("USA");
        entity.setAddressName("ORA");
        entity.setContactName("DEN");
        entity.setEmail("a@aa.aaa");
        entity.setOrgId(USER_ID);

        if (!isShared) {
            entity.setPersonId(USER_ID);
        }

        return entity;
    }

    private AddressEntity createAddressEntity() {
        AddressEntity entity = new AddressEntity();
        ZipCodeEntity zipCode = new ZipCodeEntity();
        ZipCodePK id = new ZipCodePK();
        CountryEntity country = new CountryEntity();
        country.setId("country" + Math.random());
        id.setCountry(country);
        zipCode.setId(id);
        zipCode.setStateCode("state" + Math.random());
        zipCode.setCity("city" + Math.random());
        zipCode.setZipCode("zip" + Math.random());
        entity.setZipCode(zipCode);
        return entity;
    }

    private AddressBookEntryDTO createTestAddressBookEntryDTOWithPickUpAndDelivery(PickupAndDeliveryWindowDTO... args) {
        AddressBookEntryDTO addressDTO = new AddressBookEntryDTO();
        addressDTO.setId(USER_ID);
        addressDTO.setSharedAddress(true);
        addressDTO.setCreatedBy(USER_ID);
        addressDTO.setZip(new ZipDTO());
        addressDTO.setCountry(new CountryDTO());
        addressDTO.setDeliveryWindowFrom(args[0]);
        addressDTO.setDeliveryWindowTo(args[1]);
        addressDTO.setPickupWindowFrom(args[2]);
        addressDTO.setPickupWindowTo(args[3]);
        return addressDTO;

    }

}
