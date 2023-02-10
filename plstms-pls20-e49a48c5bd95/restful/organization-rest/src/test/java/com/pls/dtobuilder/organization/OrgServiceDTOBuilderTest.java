package com.pls.dtobuilder.organization;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.dto.organization.OrgServiceDTO;

/**
 * Test cases for {@link OrgServiceDTOBuilder} class.
 * 
 * @author Artem Arapov
 *
 */
public class OrgServiceDTOBuilderTest {

    @Test
    public void testBuildDTOWithNullImaging() {
        OrgServiceEntity entity = new OrgServiceEntity();
        entity.setImaging(null);

        OrgServiceDTOBuilder sut = new OrgServiceDTOBuilder();
        OrgServiceDTO dto = sut.buildDTO(entity);

        Assert.assertNotNull(dto);
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getImaging());
    }

    @Test
    public void testBuildDTOWithNullInvoice() {
        OrgServiceEntity entity = new OrgServiceEntity();
        entity.setInvoice(null);

        OrgServiceDTOBuilder sut = new OrgServiceDTOBuilder();
        OrgServiceDTO dto = sut.buildDTO(entity);

        Assert.assertNotNull(dto);
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getInvoice());
    }

    @Test
    public void testBuildDTOWithNullPickup() {
        OrgServiceEntity entity = new OrgServiceEntity();
        entity.setPickup(null);

        OrgServiceDTOBuilder sut = new OrgServiceDTOBuilder();
        OrgServiceDTO dto = sut.buildDTO(entity);

        Assert.assertNotNull(dto);
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getPickup());
    }

    @Test
    public void testBuildDTOWithNullTracking() {
        OrgServiceEntity entity = new OrgServiceEntity();
        entity.setTracking(null);

        OrgServiceDTOBuilder sut = new OrgServiceDTOBuilder();
        OrgServiceDTO dto = sut.buildDTO(entity);

        Assert.assertNotNull(dto);
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getTracking());
    }

    @Test
    public void testBuildDTOWithNotDefaultValues() {
        OrgServiceEntity entity = new OrgServiceEntity();
        entity.setImaging(CarrierIntegrationType.API);
        entity.setInvoice(CarrierIntegrationType.API);
        entity.setPickup(CarrierIntegrationType.API);
        entity.setTracking(CarrierIntegrationType.API);

        OrgServiceDTOBuilder sut = new OrgServiceDTOBuilder();
        OrgServiceDTO dto = sut.buildDTO(entity);

        Assert.assertNotNull(dto);
        Assert.assertEquals(CarrierIntegrationType.API.name(), dto.getImaging());
        Assert.assertEquals(CarrierIntegrationType.API.name(), dto.getInvoice());
        Assert.assertEquals(CarrierIntegrationType.API.name(), dto.getPickup());
        Assert.assertEquals(CarrierIntegrationType.API.name(), dto.getTracking());
    }

    @Test
    public void testBuildDTOWithDefaultValues() {
        OrgServiceEntity entity = new OrgServiceEntity();

        OrgServiceDTOBuilder sut = new OrgServiceDTOBuilder();
        OrgServiceDTO dto = sut.buildDTO(entity);

        Assert.assertNotNull(dto);
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getImaging());
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getInvoice());
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getPickup());
        Assert.assertEquals(CarrierIntegrationType.EDI.name(), dto.getTracking());
    }
}
