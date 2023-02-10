package com.pls.dtobuilder;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.dto.address.BillToDefaultValuesDTO;
import com.pls.dtobuilder.address.BillToDefaultValuesDTOBuilder;

/**
 * Test cases for {@link BillToDefaultValuesDTOBuilder} class.
 * 
 * @author Brichak Aleksandr
 */

public class BillToDefaultValuesDTOBuilderTest {

    private BillToDefaultValuesDTOBuilder sut = new BillToDefaultValuesDTOBuilder(
            new BillToDefaultValuesDTOBuilder.DataProvider() {
                @Override
                public BillToDefaultValuesEntity getDefaultValues(Long id) {
                    return null;
                }
            });

    @Test
    public void testBuildDTOWithNull() {
        BillToDefaultValuesDTO result = sut.buildDTO(null);
        Assert.assertNotNull(result);
    }

    @Test
    public void testBuildEntityWithNull() {
        BillToDefaultValuesEntity result = sut.buildEntity(null);
        Assert.assertNull(result);
    }

    @Test
    public void testBuildDTO() {
        BillToDefaultValuesEntity entity = new BillToDefaultValuesEntity();
        entity.setBillTo(new BillToEntity());
        entity.setBillToId(1L);
        entity.setDirection(ShipmentDirection.INBOUND);
        entity.setEdiPayTerms(PaymentTerms.THIRD_PARTY_COLLECT);
        entity.setEdiDirection(ShipmentDirection.OUTBOUND);
        entity.setManualBolDirection(ShipmentDirection.OUTBOUND);
        entity.setManualBolPayTerms(PaymentTerms.THIRD_PARTY_PREPAID);
        entity.setPayTerms(PaymentTerms.COLLECT);

        BillToDefaultValuesDTO result = sut.buildDTO(entity);

        Assert.assertNotNull(result);
        Assert.assertTrue(entity.getDirection().getCode().equals(result.getDirection()));
        Assert.assertTrue(entity.getEdiDirection().getCode().equals(result.getEdiDirection()));
        Assert.assertTrue(entity.getManualBolDirection().getCode().equals(result.getManualBolDirection()));
        Assert.assertTrue(entity.getPayTerms().equals(result.getPayTerms()));
        Assert.assertTrue(entity.getEdiPayTerms().equals(result.getEdiPayTerms()));
        Assert.assertTrue(entity.getManualBolPayTerms().equals(result.getManualBolPayTerms()));
    }

    @Test
    public void testBuildEntity() {
        BillToDefaultValuesDTO dto = new BillToDefaultValuesDTO();
        dto.setDirection(ShipmentDirection.INBOUND.getCode());
        dto.setEdiPayTerms(PaymentTerms.THIRD_PARTY_COLLECT);
        dto.setEdiDirection(ShipmentDirection.INBOUND.getCode());
        dto.setManualBolDirection(ShipmentDirection.OUTBOUND.getCode());
        dto.setManualBolPayTerms(PaymentTerms.THIRD_PARTY_PREPAID);
        dto.setPayTerms(PaymentTerms.COLLECT);

        BillToDefaultValuesEntity result = sut.buildEntity(dto);

        Assert.assertNotNull(result);
        Assert.assertTrue(dto.getDirection().equals(result.getDirection().getCode()));
        Assert.assertTrue(dto.getEdiDirection().equals(result.getEdiDirection().getCode()));
        Assert.assertTrue(dto.getManualBolDirection().equals(result.getManualBolDirection().getCode()));
        Assert.assertTrue(dto.getPayTerms().equals(result.getPayTerms()));
        Assert.assertTrue(dto.getEdiPayTerms().equals(result.getEdiPayTerms()));
        Assert.assertTrue(dto.getManualBolPayTerms().equals(result.getManualBolPayTerms()));
    }
}
