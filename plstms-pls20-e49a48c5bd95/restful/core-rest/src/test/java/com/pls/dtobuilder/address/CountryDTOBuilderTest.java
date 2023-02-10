package com.pls.dtobuilder.address;

import com.pls.core.domain.organization.CountryEntity;
import com.pls.dto.address.CountryDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link CountryDTOBuilder}.
 * 
 * @author Andrey Kachur
 * 
 */
public class CountryDTOBuilderTest {

    private CountryDTOBuilder builder = new CountryDTOBuilder();
    private CountryDTO dto;
    private CountryEntity entity;

    @Before
    public void init() {
        dto = new CountryDTO();
        entity = new CountryEntity();
        String id = new Object().toString();
        dto.setId(id);
        entity.setId(id);
        String name = new Object().toString();
        dto.setName(name);
        entity.setName(name);
        String dialingCode = new Object().toString();
        dto.setDialingCode(dialingCode);
        entity.setPhoneCode(dialingCode);
    }

    @Test
    public void testBuildEntity() {
        CountryEntity entity = builder.buildEntity(dto);

        Assert.assertTrue(dto.getId().equals(entity.getId()));
        Assert.assertTrue(dto.getName().equals(entity.getName()));
        Assert.assertEquals(dto.getDialingCode(), entity.getPhoneCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildEntityFromNull() {
        builder.buildEntity(null);
    }

    @Test
    public void testBuildDTO() {
        CountryDTO dto = builder.buildDTO(entity);

        Assert.assertTrue(entity.getId().equals(dto.getId()));
        Assert.assertTrue(entity.getName().equals(dto.getName()));
        Assert.assertEquals(entity.getPhoneCode(), dto.getDialingCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildDTOFromNull() {
        builder.buildDTO(null);
    }

}
