package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockCarrGeoServicesDao;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;

/**
 * Integration test for {@link LtlBlockCarrGeoServicesService}.
 *
 * @author Artem Arapov
 *
 */
public class LtlBlockCarrGeoServicesServiceImplTestIT extends BaseServiceITClass {

    @Autowired
    private LtlBlockCarrGeoServicesService sut;

    @Autowired
    private LtlBlockCarrGeoServicesDao dao;

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void saveBlockedCarrierGeoServiceAddCSPEntity() throws Exception {
        LtlBlockCarrGeoServicesEntity expectedEntity = populateEntityWithRandomValues(new LtlBlockCarrGeoServicesEntity());
        expectedEntity = sut.saveBlockedCarrierGeoService(expectedEntity);
        assertNotNull(expectedEntity);

        List<LtlBlockCarrGeoServicesEntity> actualList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());

        LtlBlockCarrGeoServicesEntity actualCspEntity = actualList.get(0);
        assertNotNull(actualCspEntity);
        assertEquals(expectedEntity.getId(), actualCspEntity.getCopiedFrom());
        assertEntity(expectedEntity, actualCspEntity);
    }

    @Test
    public void saveBlockedCarrierGeoServiceUpdateCSPEntity() throws Exception {
        SecurityTestUtils.login("sysadmin");

        LtlBlockCarrGeoServicesEntity existingEntity = sut.getBlockedCarrierGeoServiceById(1L);
        assertNotNull(existingEntity);

        LtlBlockCarrGeoServicesEntity expectedEntity = populateEntityWithRandomValues(existingEntity);
        expectedEntity = sut.saveBlockedCarrierGeoService(expectedEntity);
        assertNotNull(expectedEntity);

        List<LtlBlockCarrGeoServicesEntity> actualList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());

        LtlBlockCarrGeoServicesEntity actualCspEntity = actualList.get(0);
        assertNotNull(actualCspEntity);
        assertEquals(expectedEntity.getId(), actualCspEntity.getCopiedFrom());
        assertEntity(expectedEntity, actualCspEntity);
    }

    @Test
    public void testInactivateBlockedCarrierGeoServices() {
        SecurityTestUtils.login("sysadmin");

        List<Long> expectedIds = Arrays.asList(1L);

        LtlBlockCarrGeoServicesEntity expectedEntity = sut.getBlockedCarrierGeoServiceById(1L);
        assertNotNull(expectedEntity);
        Assert.assertEquals(Status.ACTIVE, expectedEntity.getStatus());
        flushAndClearSession();

        sut.inactivateBlockedCarrierGeoServices(expectedIds, 1L);

        LtlBlockCarrGeoServicesEntity actualEntity = sut.getBlockedCarrierGeoServiceById(1L);
        assertNotNull(actualEntity);
        Assert.assertEquals(Status.INACTIVE, actualEntity.getStatus());
    }

    private LtlBlockCarrGeoServicesEntity populateEntityWithRandomValues(LtlBlockCarrGeoServicesEntity entity) {
        entity.setDestination(String.valueOf(Math.random()));
        entity.setOrigin(String.valueOf(Math.random()));
        entity.setNotes(String.valueOf(Math.random()));
        entity.setStatus(Status.ACTIVE);
        entity.setProfileId(1L);

        return entity;
    }

    private void assertEntity(LtlBlockCarrGeoServicesEntity expected, LtlBlockCarrGeoServicesEntity actual) {
        Assert.assertEquals(expected.getLtlBkCarrOriginGeoServiceDetails().get(0).getGeoValue(), actual.getLtlBkCarrOriginGeoServiceDetails()
                .get(0).getGeoValue());
        Assert.assertEquals(expected.getLtlBkCarrDestGeoServiceDetails().get(0).getGeoValue(), actual.getLtlBkCarrDestGeoServiceDetails().get(0)
                .getGeoValue());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
        Assert.assertEquals(expected.getNotes(), actual.getNotes());
    }
}
