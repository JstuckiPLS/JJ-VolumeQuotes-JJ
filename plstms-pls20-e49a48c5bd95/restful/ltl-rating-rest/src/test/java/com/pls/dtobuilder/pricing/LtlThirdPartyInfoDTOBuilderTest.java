package com.pls.dtobuilder.pricing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.shared.Status;
import com.pls.dto.LtlThirdPartyInfoDTO;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.PlainAddressDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * Tests for {@link LtlThirdPartyInfoDTOBuilder}.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlThirdPartyInfoDTOBuilderTest {

    private final LtlThirdPartyInfoDTOBuilder builder = new LtlThirdPartyInfoDTOBuilder();

    @Test
    public void testBuildDTO() {
        LtlPricingThirdPartyInfoEntity entity = createEntity();
        LtlThirdPartyInfoDTO dto = builder.buildDTO(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getPricProfDetailId(), dto.getProfileDetailId());
        assertEquals(entity.getAccountNum(), dto.getAccountNum());
        assertEquals(entity.getCompany(), dto.getCompany());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getPhone().getAreaCode(), dto.getPhone().getAreaCode());
        assertEquals(entity.getPhone().getCountryCode(), dto.getPhone().getCountryCode());
        assertEquals(entity.getPhone().getNumber(), dto.getPhone().getNumber());
        assertEquals(entity.getFax().getAreaCode(), dto.getFax().getAreaCode());
        assertEquals(entity.getFax().getCountryCode(), dto.getFax().getCountryCode());
        assertEquals(entity.getFax().getNumber(), dto.getFax().getNumber());
        verifyAddressDTO(entity.getAddress(), dto.getAddress());
    }

    @Test
    public void testBuildEntity() {
        LtlThirdPartyInfoDTO dto = createDTO();
        LtlPricingThirdPartyInfoEntity entity = builder.buildEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getAccountNum(), entity.getAccountNum());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getProfileDetailId(), entity.getPricProfDetailId());
        assertEquals(dto.getCompany(), entity.getCompany());
        assertEquals(dto.getPhone().getAreaCode(), entity.getPhone().getAreaCode());
        assertEquals(dto.getPhone().getCountryCode(), entity.getPhone().getCountryCode());
        assertEquals(dto.getPhone().getNumber(), entity.getPhone().getNumber());
        assertEquals(PhoneType.VOICE, entity.getPhone().getType());
        assertEquals(dto.getFax().getAreaCode(), entity.getFax().getAreaCode());
        assertEquals(dto.getFax().getCountryCode(), entity.getFax().getCountryCode());
        assertEquals(dto.getFax().getNumber(), entity.getFax().getNumber());
        assertEquals(PhoneType.FAX, entity.getFax().getType());
        verifyAddressEntity(dto.getAddress(), entity.getAddress());
    }

    private void verifyAddressDTO(AddressEntity entity, PlainAddressDTO dto) {
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getAddress1(), dto.getAddress1());
        assertEquals(entity.getAddress2(), dto.getAddress2());
        assertEquals(entity.getCountry().getId(), dto.getCountry().getId());
        assertEquals(entity.getCity(), dto.getZip().getCity());
        assertEquals(entity.getState().getStatePK().getStateCode(), dto.getZip().getState());
        assertEquals(entity.getZip(), dto.getZip().getZip());
    }

    private void verifyAddressEntity(PlainAddressDTO dto, AddressEntity entity) {
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getAddress1().toUpperCase(), entity.getAddress1());
        assertEquals(dto.getAddress2().toUpperCase(), entity.getAddress2());
        assertEquals(dto.getZip().getCity().toUpperCase(), entity.getCity());
        assertEquals(dto.getZip().getState(), entity.getState().getStatePK().getStateCode());
        assertEquals(dto.getZip().getZip().toUpperCase(), entity.getZip());
        assertEquals(dto.getZip().getCountry().getId(), entity.getCountry().getId());
    }

    private LtlPricingThirdPartyInfoEntity createEntity() {
        LtlPricingThirdPartyInfoEntity entity = new LtlPricingThirdPartyInfoEntity();

        entity.setId(1L);
        entity.setAccountNum("Account" + Math.random());
        entity.setAddress(createAddress());
        PhoneEntity phone = createPhone(new PhoneEntity());
        phone.setType(PhoneType.VOICE);
        entity.setPhone(phone);
        PhoneEntity fax = createPhone(new PhoneEntity());
        fax.setType(PhoneType.FAX);
        entity.setFax(fax);
        entity.setEmail("Email" + Math.random());
        entity.setStatus(Status.ACTIVE);
        entity.setCompany("company" + Math.random());
        entity.setPricProfDetailId(1L);

        return entity;
    }

    private LtlThirdPartyInfoDTO createDTO() {
        LtlThirdPartyInfoDTO dto = new LtlThirdPartyInfoDTO();

        dto.setId(1L);
        dto.setAccountNum("account" + Math.random());
        dto.setEmail("email" + Math.random());
        dto.setAddress(createPlainAddressDTO());
        dto.setPhone(createPhoneBO());
        dto.setFax(createPhoneBO());
        dto.setProfileDetailId(1L);
        dto.setVersion(1L);
        dto.setCompany("company" + Math.random());

        return dto;
    }

    private PlainAddressDTO createPlainAddressDTO() {
        PlainAddressDTO dto = new PlainAddressDTO();

        dto.setId(1L);
        dto.setAddress1("address1" + Math.random());
        dto.setAddress2("address2" + Math.random());
        ZipDTO zip = new ZipDTO();
        zip.setCity("city" + Math.random());
        zip.setCountry(new CountryDTO());
        zip.setState("state" + Math.random());
        zip.setZip("zip" + Math.random());
        dto.setZip(zip);

        return dto;
    }

    private PhoneBO createPhoneBO() {
        PhoneBO phone = new PhoneBO();
        phone.setAreaCode("areaCode");
        phone.setCountryCode("countryCode");
        phone.setNumber("phoneNumber");
        return phone;
    }

    private AddressEntity createAddress() {
        AddressEntity address = new AddressEntity();
        address.setAddress1("address1" + Math.random());
        address.setAddress2("address2" + Math.random());
        address.setState(createState());
        address.setCity("city" + Math.random());
        CountryEntity country = new CountryEntity();
        country.setId(address.getState().getStatePK().getCountryCode());
        address.setCountry(country);
        address.setZip("zip" + Math.random());
        return address;
    }

    private <P extends PhoneEntity> P createPhone(P phone) {
        phone.setAreaCode("areaCode" + Math.random());
        phone.setCountryCode("countryCode" + Math.random());
        phone.setNumber("number" + Math.random());
        return phone;
    }

    private StateEntity createState() {
        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setCountryCode("country" + Math.random());
        statePK.setStateCode("state" + Math.random());
        state.setStatePK(statePK);
        return state;
    }
}
