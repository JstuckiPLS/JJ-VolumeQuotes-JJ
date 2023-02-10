package com.pls.dtobuilder.address;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.address.ZipCodePK;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.PlainAddressDTO;
import com.pls.dto.address.ZipDTO;

/**
 * Tests for {@link PlainAddressDTOBuilder}.
 * 
 * @author Artem Arapov
 * 
 */
public class PlainAddressDTOBuilderTest {

    private final PlainAddressDTOBuilder builder = new PlainAddressDTOBuilder();

    @Test
    public void testBuildDTO() {
        AddressEntity address = createAddress();
        PlainAddressDTO dto = builder.buildDTO(address);

        assertEquals(address.getId(), dto.getId());
        assertEquals(address.getAddress1(), dto.getAddress1());
        assertEquals(address.getAddress2(), dto.getAddress2());
        assertEquals(address.getZipCode().getId().getCountry().getId(), dto.getCountry().getId());
        assertEquals(address.getZipCode().getCity(), dto.getZip().getCity());
        assertEquals(address.getZipCode().getStateCode(), dto.getZip().getState());
        assertEquals(address.getZipCode().getZipCode(), dto.getZip().getZip());
    }

    @Test
    public void testBuildEntity() {
        PlainAddressDTO dto = createPlainAddressDTO();
        AddressEntity entity = builder.buildEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getAddress1().toUpperCase(), entity.getAddress1());
        assertEquals(dto.getAddress2().toUpperCase(), entity.getAddress2());
        assertEquals(dto.getZip().getCity().toUpperCase(), entity.getCity());
        assertEquals(dto.getZip().getCountry().getId(), entity.getCountry().getId());
        assertEquals(dto.getZip().getState(), entity.getState().getStatePK().getStateCode());
        assertEquals(dto.getZip().getZip().toUpperCase(), entity.getZip());
    }

    private PlainAddressDTO createPlainAddressDTO() {
        PlainAddressDTO dto = new PlainAddressDTO();

        dto.setId(1L);
        dto.setAddress1("address1" + Math.random());
        dto.setAddress2("address2" + Math.random());
        CountryDTO country = new CountryDTO("country" + Math.random());
        ZipDTO zip = new ZipDTO();
        zip.setCity("city" + Math.random());
        zip.setCountry(new CountryDTO());
        zip.setState("state" + Math.random());
        zip.setZip("zip" + Math.random());
        dto.setZip(zip);
        dto.setCountry(country);
        return dto;
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
        return address;
    }
}
