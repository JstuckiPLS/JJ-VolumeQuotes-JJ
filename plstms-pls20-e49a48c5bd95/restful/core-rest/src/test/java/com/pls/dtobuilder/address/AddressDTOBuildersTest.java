package com.pls.dtobuilder.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.sql.Time;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.AddressNotificationsEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.address.ZipCodePK;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dtobuilder.AddressNotificationsDTOBuilder;

/**
 * Tests for {@link AddressBookDTOBuilder} and {@link AddressBookEntryDTOBuilder}.
 * 
 * @author Aleksandr Leshchenko
 */
public class AddressDTOBuildersTest {

    @SuppressWarnings("deprecation")
    private static final Time TIME_FROM = new Time(1, 2, 0);
    @SuppressWarnings("deprecation")
    private static final Time TIME_TO = new Time(4, 5, 0);
    private static final PickupAndDeliveryWindowDTO TIME_FROM_DTO = new PickupAndDeliveryWindowDTO(1, 2, true);
    private static final PickupAndDeliveryWindowDTO TIME_TO_DTO = new PickupAndDeliveryWindowDTO(4, 5, true);

    private AddressBookDTOBuilder builder;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBuildDTO() {
        builder = new AddressBookDTOBuilder();

        UserAddressBookEntity entity = createUserAddressBookEntity();

        AddressBookEntryDTO dto = builder.buildDTO(entity);

        assertSame(entity.getId(), dto.getId());
        assertSame(entity.getAddressName(), dto.getAddressName());
        assertSame(entity.getContactName(), dto.getContactName());
        assertEquals(TIME_FROM_DTO.toString(), dto.getPickupWindowFrom().toString());
        assertEquals(TIME_TO_DTO.toString(), dto.getPickupWindowTo().toString());
        assertSame(entity.getAddressCode(), dto.getAddressCode());
        assertSame(entity.getEmail(), dto.getEmail());
        assertSame(entity.getPickupNotes(), dto.getPickupNotes());
        assertSame(entity.getDeliveryNotes(), dto.getDeliveryNotes());
        assertSame(entity.getPhone().getAreaCode(), dto.getPhone().getAreaCode());
        assertSame(entity.getPhone().getCountryCode(), dto.getPhone().getCountryCode());
        assertSame(entity.getPhone().getNumber(), dto.getPhone().getNumber());
        assertSame(entity.getFax().getAreaCode(), dto.getFax().getAreaCode());
        assertSame(entity.getFax().getCountryCode(), dto.getFax().getCountryCode());
        assertSame(entity.getFax().getNumber(), dto.getFax().getNumber());
        // check address
        assertSame(entity.getAddress().getAddress1(), dto.getAddress1());
        assertSame(entity.getAddress().getAddress2(), dto.getAddress2());
        assertSame(entity.getAddress().getZipCode().getCity(), dto.getZip().getCity());
        assertSame(entity.getAddress().getZipCode().getId().getCountry().getId(), dto.getZip().getCountry().getId());
        assertSame(entity.getAddress().getZipCode().getStateCode(), dto.getZip().getState());
        assertSame(entity.getAddress().getZipCode().getZipCode(), dto.getZip().getZip());
        assertSame(entity.getAddress().getLatitude(), dto.getLatitude());
        assertSame(entity.getAddress().getLongitude(), dto.getLongitude());
    }

    @Test
    public void shouldBuildEntity() {
        final AddressBookEntryDTO dto = createAddressBookEntryDTO();

        final UserAddressBookEntity userAddressBookEntity = new UserAddressBookEntity();
        userAddressBookEntity.setId(dto.getId());
        final AddressEntity address = new AddressEntity();
        address.setId(dto.getId());
        userAddressBookEntity.setAddress(address);
        builder = new AddressBookDTOBuilder(
         new AddressBookDTOBuilder.DataProvider() {
            @Override
            public UserAddressBookEntity getAddress(Long id) {
                return id.equals(dto.getId()) ? userAddressBookEntity : null;
            }
        },
        new AddressNotificationsDTOBuilder.DataProvider() {
            @Override
            public AddressNotificationsEntity getAddressNotification(Long id) {
                return null;
            }
        });

        UserAddressBookEntity entity = builder.buildEntity(dto);

        assertSame(userAddressBookEntity, entity);
        assertSame(address, entity.getAddress());
        verifyEntity(dto, entity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBuildEntityWithInvalidDataProvider() {
        AddressBookEntryDTO dto = createAddressBookEntryDTO();

        builder = new AddressBookDTOBuilder();

        builder.buildEntity(dto);
    }

    private void verifyEntity(AddressBookEntryDTO dto, UserAddressBookEntity entity) {
        assertSame(dto.getId(), entity.getId());
        assertSame(dto.getAddressName(), entity.getAddressName());
        assertSame(dto.getContactName(), entity.getContactName());
        assertEquals(TIME_FROM.toString(), entity.getPickupFrom().toString());
        assertEquals(TIME_TO.toString(), entity.getPickupTo().toString());
        assertSame(dto.getAddressCode(), entity.getAddressCode());
        assertSame(dto.getEmail(), entity.getEmail());
        assertSame(dto.getPickupNotes(), entity.getPickupNotes());
        assertSame(dto.getDeliveryNotes(), entity.getDeliveryNotes());
        assertSame(dto.getPhone().getAreaCode(), entity.getPhone().getAreaCode());
        assertSame(dto.getPhone().getCountryCode(), entity.getPhone().getCountryCode());
        assertSame(dto.getPhone().getNumber(), entity.getPhone().getNumber());
        assertSame(dto.getFax().getAreaCode(), entity.getFax().getAreaCode());
        assertSame(dto.getFax().getCountryCode(), entity.getFax().getCountryCode());
        assertSame(dto.getFax().getNumber(), entity.getFax().getNumber());
        // check address
        assertEquals(dto.getAddress1().toUpperCase(), entity.getAddress().getAddress1());
        assertEquals(dto.getAddress2().toUpperCase(), entity.getAddress().getAddress2());
        assertEquals(dto.getZip().getCity().toUpperCase(), entity.getAddress().getCity());
        assertSame(dto.getZip().getCountry().getId(), entity.getAddress().getCountry().getId());
        assertSame(dto.getZip().getState(), entity.getAddress().getState().getStatePK().getStateCode());
        assertEquals(dto.getZip().getZip().toUpperCase(), entity.getAddress().getZip());
        assertSame(dto.getLatitude(), entity.getAddress().getLatitude());
        assertSame(dto.getLongitude(), entity.getAddress().getLongitude());
    }

    private AddressBookEntryDTO createAddressBookEntryDTO() {
        AddressBookEntryDTO dto = new AddressBookEntryDTO();
        dto.setId((long) (Math.random() * 100));
        dto.setAddressName("addressName");
        dto.setContactName("contactName");
        dto.setPickupWindowFrom(TIME_FROM_DTO);
        dto.setPickupWindowTo(TIME_TO_DTO);
        dto.setAddressCode("locationCode");
        dto.setEmail("email");
        dto.setPickupNotes("pickupNotes");
        dto.setDeliveryNotes("deliveryNotes");
        dto.setPhone(createPhoneBO());
        dto.setFax(createPhoneBO());
        dto.setAddress1("address1");
        dto.setAddress2("address2");
        dto.setZip(new ZipDTO());
        dto.getZip().setCity("city");
        dto.getZip().setCountry(new CountryDTO("country"));
        dto.getZip().setState("state");
        dto.getZip().setZip("zip");
        dto.setLatitude(BigDecimal.ONE);
        dto.setLongitude(BigDecimal.TEN);
        dto.setVersion(1L);
        return dto;
    }

    private PhoneBO createPhoneBO() {
        PhoneBO phone = new PhoneBO();
        phone.setAreaCode("areaCode");
        phone.setCountryCode("countryCode");
        phone.setNumber("phoneNumber");
        return phone;
    }

    private UserAddressBookEntity createUserAddressBookEntity() {
        UserAddressBookEntity addressBookEntity = new UserAddressBookEntity();
        addressBookEntity.setId((long) (Math.random() * 100));
        addressBookEntity.setAddressName("addressName" + Math.random());
        addressBookEntity.setContactName("contactName" + Math.random());
        addressBookEntity.setPickupFrom(TIME_FROM);
        addressBookEntity.setPickupTo(TIME_TO);
        addressBookEntity.setAddressCode("locationCode" + Math.random());
        addressBookEntity.setEmail("email" + Math.random());
        addressBookEntity.setPickupNotes("pickupNotes" + Math.random());
        addressBookEntity.setDeliveryNotes("DeliveryNotes" + Math.random());
        PhoneEntity phone = createPhone(new PhoneEntity());
        phone.setType(PhoneType.VOICE);
        addressBookEntity.setPhone(phone);
        PhoneEntity fax = createPhone(new PhoneEntity());
        fax.setType(PhoneType.FAX);
        addressBookEntity.setFax(fax);
        addressBookEntity.setAddress(createAddress());
        return addressBookEntity;
    }

    private <P extends PhoneEntity> P createPhone(P phone) {
        phone.setAreaCode("areaCode" + Math.random());
        phone.setCountryCode("countryCode" + Math.random());
        phone.setNumber("number" + Math.random());
        return phone;
    }

    private AddressEntity createAddress() {
        AddressEntity address = new AddressEntity();
        address.setAddress1("address1" + Math.random());
        address.setAddress2("address2" + Math.random());
        ZipCodeEntity zipCode = new ZipCodeEntity();
        ZipCodePK id = new ZipCodePK();
        CountryEntity country = new CountryEntity();
        country.setId("country" + Math.random());
        id.setCountry(country);
        zipCode.setId(id);
        zipCode.setStateCode("state" + Math.random());
        zipCode.setCity("city" + Math.random());
        zipCode.setZipCode("zip" + Math.random());
        address.setZipCode(zipCode);
        address.setLatitude(BigDecimal.ONE);
        address.setLongitude(BigDecimal.TEN);
        return address;
    }
}
