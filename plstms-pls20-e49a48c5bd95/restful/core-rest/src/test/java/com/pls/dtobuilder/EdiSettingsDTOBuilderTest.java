package com.pls.dtobuilder;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pls.EdiSettingsDTO;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.EdiSettingsEntity;

/**
 * Test cases for {@link EdiSettingsDTOBuilder}.
 * 
 * @author Brichak Aleksandr
 * 
 */
public class EdiSettingsDTOBuilderTest {

    private EdiSettingsDTOBuilder builder = new EdiSettingsDTOBuilder();
    private EdiSettingsDTO dto;
    private EdiSettingsEntity entity;

    @Before
    public void init() {
        dto = new EdiSettingsDTO();
        entity = new EdiSettingsEntity();
        long id = 1L;
        entity.setId(id);
        boolean bol = true;
        dto.setBolUnique(bol);
        entity.setIsUniqueRefAndBol(bol);
        List<ShipmentStatus> shipmentStatus = new ArrayList<ShipmentStatus>();
        shipmentStatus.add(ShipmentStatus.BOOKED);
        shipmentStatus.add(ShipmentStatus.CANCELLED);
        dto.setEdiStatus(shipmentStatus);
        entity.setEdiStatus(shipmentStatus);
        List<EdiType> ediType = new ArrayList<EdiType>();
        ediType.add(EdiType.EDI_211);
        dto.setEdiType(ediType);
        entity.setEdiType(ediType);
    }

    @Test
    public void testBuildEntity() {
        EdiSettingsEntity entity = builder.buildEntity(dto);

        Assert.assertTrue(dto.getEdiType().equals(entity.getEdiType()));
        Assert.assertTrue(dto.isBolUnique() && entity.isUniqueRefAndBol());
        Assert.assertTrue(dto.getEdiType().equals(entity.getEdiType()));
    }

    public void testBuildEntityFromNull() {
        Assert.assertEquals(new EdiSettingsEntity(), builder.buildEntity(null));
    }

    @Test
    public void testBuildDTO() {
        EdiSettingsDTO dto = builder.buildDTO(entity);

        Assert.assertTrue(entity.getEdiType().equals(dto.getEdiType()));
        Assert.assertTrue(entity.isUniqueRefAndBol() && dto.isBolUnique());
        Assert.assertTrue(entity.getEdiType().equals(dto.getEdiType()));
        entity.setId(null);

        dto = builder.buildDTO(entity);
        Assert.assertTrue(entity.getEdiType().equals(dto.getEdiType()));
        Assert.assertTrue(entity.isUniqueRefAndBol() && dto.isBolUnique());
        Assert.assertTrue(entity.getEdiType().equals(dto.getEdiType()));
        entity = new EdiSettingsEntity();
        dto = builder.buildDTO(entity);
        Assert.assertFalse(dto.isBolUnique());
    }

    public void testBuildDTOFromNull() {
        Assert.assertEquals(new EdiSettingsDTO(), builder.buildDTO(null));
    }

}
