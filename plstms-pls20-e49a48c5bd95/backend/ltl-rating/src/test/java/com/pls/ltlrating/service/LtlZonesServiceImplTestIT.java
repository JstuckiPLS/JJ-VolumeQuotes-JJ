package com.pls.ltlrating.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlZonesDao;
import com.pls.ltlrating.domain.LtlZoneGeoServicesEntity;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.service.impl.LtlZonesServiceImpl;
import com.pls.ltlrating.service.validation.LtlZonesValidator;

/**
 * Integration tests for {@link LtlZonesServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlZonesServiceImplTestIT extends BaseServiceITClass {

    @Autowired
    private LtlZonesSerivce sut;

    @Autowired
    private LtlZonesDao dao;

    @Resource(type = LtlZonesValidator.class)
    private Validator<LtlZonesEntity> validator;

    @Test
    public void saveLTLZoneAddNewChildEntity() throws Exception {
        LtlZonesEntity expectedEntity = populateLtlZoneEntityWithRandomValues(new LtlZonesEntity());
        expectedEntity.setLtlPricProfDetailId(1L);
        expectedEntity = sut.saveLTLZone(expectedEntity);
        Assert.assertNotNull(expectedEntity);

        List<LtlZonesEntity> actualChildsList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        Assert.assertNotNull(actualChildsList);
        Assert.assertFalse(actualChildsList.isEmpty());

        LtlZonesEntity actualChildEntity = actualChildsList.get(0);
        Assert.assertNotNull(actualChildEntity);
        Assert.assertEquals(expectedEntity.getId(), actualChildEntity.getCopiedFrom());
    }

    @Test
    public void saveLTLZoneUpdateChildEntity() throws Exception {
        LtlZonesEntity expectedEntity = sut.getLTLZoneById(1L);
        expectedEntity = populateLtlZoneEntityWithRandomValues(expectedEntity);
        expectedEntity = sut.saveLTLZone(expectedEntity);
        Assert.assertNotNull(expectedEntity);

        List<LtlZonesEntity> actualChildsList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        Assert.assertNotNull(actualChildsList);
        Assert.assertFalse(actualChildsList.isEmpty());

        LtlZonesEntity actualChildEntity = actualChildsList.get(0);
        Assert.assertNotNull(actualChildEntity);
        Assert.assertEquals(expectedEntity.getId(), actualChildEntity.getCopiedFrom());
        assertEntity(expectedEntity, actualChildEntity);
    }

    @Test(expected = ValidationException.class)
    public void testLtlZoneValidator() throws Exception {
        LtlZonesEntity entity = new LtlZonesEntity();
        entity.setId(null);
        entity.setLtlPricProfDetailId(1L);
        entity.setName("SOME NAME1");
        validator.validate(entity);
    }

    private LtlZonesEntity populateLtlZoneEntityWithRandomValues(LtlZonesEntity entity) {
        entity.setName(String.valueOf(Math.random()));
        entity.setStatus(Status.ACTIVE);
        for (LtlZoneGeoServicesEntity item : entity.getLtlZoneGeoServicesEntities()) {
            populateLtlZoneGeoEntityWithRandomValues(item);
        }
        entity.getLtlZoneGeoServicesEntities().add(populateLtlZoneGeoEntityWithRandomValues(new LtlZoneGeoServicesEntity()));

        return entity;
    }

    private LtlZoneGeoServicesEntity populateLtlZoneGeoEntityWithRandomValues(LtlZoneGeoServicesEntity entity) {
        entity.setLocation(String.valueOf(Math.random()));
        return entity;
    }

    private void assertEntity(LtlZonesEntity expected, LtlZonesEntity actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
        for (LtlZoneGeoServicesEntity item : expected.getLtlZoneGeoServicesEntities()) {
            actual.getLtlZoneGeoServicesEntities().contains(item);
        }
    }
}
