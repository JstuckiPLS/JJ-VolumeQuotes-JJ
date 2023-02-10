package com.pls.dtobuilder;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.dto.enums.CommodityClassDTO;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link CommodityClassDTOBuilder} class.
 * 
 * @author Maxim Medvedev
 */
public class CommodityClassDTOBuilderTest {
    private CommodityClassDTOBuilder sut = new CommodityClassDTOBuilder();

    @Test
    public void testBuildDTOWithNormalCase() {
        for (CommodityClass entity : CommodityClass.values()) {
            CommodityClassDTO result = sut.buildDTO(entity);

            Assert.assertEquals(CommodityClassDTO.valueOf(entity.name()), result);
        }
    }

    @Test
    public void testBuildDTOWithNull() {
        CommodityClassDTO result = sut.buildDTO(null);

        Assert.assertNull(result);
    }

    @Test
    public void testBuildEntityWithNormalCase() {
        for (CommodityClassDTO dto : CommodityClassDTO.values()) {
            CommodityClass result = sut.buildEntity(dto);

            Assert.assertEquals(CommodityClass.valueOf(dto.name()), result);
        }
    }

    @Test
    public void testBuildEntityWithNull() {
        CommodityClass result = sut.buildEntity(null);

        Assert.assertNull(result);
    }
}
