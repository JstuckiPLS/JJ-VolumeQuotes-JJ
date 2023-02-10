package com.pls.dtobuilder.product;

import com.pls.shipment.domain.PackageTypeEntity;
import org.junit.Assert;

import org.junit.Test;

import com.pls.dto.PackageTypeDTO;

/**
 * Test cases for {@link PackageTypeDTOBuilder} class.
 * 
 * @author Aleksandr Leshchenko
 */
public class PackageTypeDTOBuilderTest {
    private static final String CODE = "PLT";
    public static final String DESCRIPTION = "Pallet";

    private final PackageTypeDTOBuilder sut = new PackageTypeDTOBuilder();

    @Test
    public void testBuildDTO() throws Exception {
        PackageTypeDTO dto = new PackageTypeDTO(CODE, DESCRIPTION);
        PackageTypeEntity entity = new PackageTypeEntity();
        entity.setId(CODE);
        entity.setDescription(DESCRIPTION);
        PackageTypeDTO result = sut.buildDTO(entity);
        Assert.assertEquals(dto.getCode(), result.getCode());
        Assert.assertEquals(dto.getLabel(), result.getLabel());
    }

    @Test
    public void testBuildEntity() throws Exception {
        PackageTypeDTO dto = new PackageTypeDTO(CODE, DESCRIPTION);
        PackageTypeEntity entity = new PackageTypeEntity();
        entity.setId(CODE);
        entity.setDescription(DESCRIPTION);
        Assert.assertEquals(entity, sut.buildEntity(dto));
    }
}
