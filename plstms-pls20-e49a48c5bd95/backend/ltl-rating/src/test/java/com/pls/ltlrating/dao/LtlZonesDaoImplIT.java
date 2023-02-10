package com.pls.ltlrating.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.impl.LtlZonesDaoImpl;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.domain.bo.ZonesListItemVO;

/**
 * Test cases for {@link LtlZonesDaoImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlZonesDaoImplIT extends AbstractDaoTest {

    private static final Long ZONE_ID = 1L;

    private static final Long PROFILE_ID = 1L;

    private static final Long PROFILE_DETAIL_ID = 1L;

    private static final Long CHILD_PROFILE_DETAIL_ID = 9L;

    private static final Long USER_ID = 1L;

    private static final Long BLANKET_ID = 1L;

    private static final String EXPECTED_NAME = "SOME NAME1";

    @Autowired
    private LtlZonesDao sut;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlZonesEntity result = sut.find(ZONE_ID);

        assertNotNull(result);
        assertFalse(result.getLtlZoneGeoServicesEntities().isEmpty());
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        LtlZonesEntity entity = createEntity();

        LtlZonesEntity afterSave = sut.saveOrUpdate(entity);
        assertNotNull(afterSave);
        assertNotNull(afterSave.getId());

        flushAndClearSession();

        LtlZonesEntity actualEntity = sut.find(afterSave.getId());
        assertNotNull(actualEntity);
    }

    @Test
    public void testFindByProfileId() throws Exception {
        List<LtlZonesEntity> result = sut.findByProfileDetailId(PROFILE_DETAIL_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertFalse(result.get(0).getLtlZoneGeoServicesEntities().isEmpty());
    }

    @Test
    public void testFindZoneByProfileIdAndNameNoDataFound() throws Exception {
        LtlZonesEntity result = sut.findZoneByProfileDetailIdAndName(PROFILE_ID, "ZONE-" + Math.random());
        Assert.assertNull(result);
    }

    @Test
    public void testFindZoneByProfileIdAndNameWithExistName() throws Exception {
        LtlZonesEntity actualResult = sut.findZoneByProfileDetailIdAndName(PROFILE_ID, EXPECTED_NAME);
        assertNotNull(actualResult);
        assertEquals(PROFILE_ID, actualResult.getLtlPricProfDetailId());
        assertEquals(EXPECTED_NAME, actualResult.getName());
    }

    @Test
    public void testFindByActiveStatusAndProfileId() throws Exception {
        List<ZonesListItemVO> result = sut.findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertListByStatus(result, Status.ACTIVE);
    }

    @Test
    public void testFindByInactiveStatusAndProfileId() throws Exception {
        List<ZonesListItemVO> result = sut.findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertListByStatus(result, Status.INACTIVE);
    }

    @Test
    public void testActivate() throws Exception {
        List<Long> ids = Arrays.asList(4L, 5L);

        sut.updateStatus(ids, Status.ACTIVE, USER_ID);

        flushAndClearSession();

        LtlZonesEntity actualEntity = sut.find(4L);
        assertEquals(Status.ACTIVE, actualEntity.getStatus());

        actualEntity = sut.find(5L);
        assertEquals(Status.ACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testInactivate() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        sut.updateStatus(ids, Status.INACTIVE, USER_ID);

        flushAndClearSession();

        LtlZonesEntity actualEntity = sut.find(1L);
        assertEquals(Status.INACTIVE, actualEntity.getStatus());

        actualEntity = sut.find(2L);
        assertEquals(Status.INACTIVE, actualEntity.getStatus());

        actualEntity = sut.find(3L);
        assertEquals(Status.INACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlZonesEntity> actualResult = sut.findAllCspChildsCopyedFrom(BLANKET_ID);

        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testUpdateStatusInCSPByCopiedFrom() {
        List<Long> actualIds = Arrays.asList(BLANKET_ID);

        sut.updateStatusInCSPByCopiedFrom(actualIds, Status.INACTIVE, USER_ID);

        List<LtlZonesEntity> actualCspDetailList = sut.findAllCspChildsCopyedFrom(BLANKET_ID);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlZonesEntity actualCspEntity = actualCspDetailList.get(0);

        Assert.assertEquals(Status.INACTIVE, actualCspEntity.getStatus());
    }

    @Test
    public void testInactivateByProfileDetailId() {
        sut.inactivateCSPByProfileDetailId(PROFILE_DETAIL_ID, USER_ID);
        flushAndClearSession();

        List<LtlZonesEntity> actualList = sut.findByProfileDetailId(CHILD_PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlZonesEntity item : actualList) {
            Assert.assertNotNull(item);
            Assert.assertEquals(Status.INACTIVE, item.getStatus());
        }
    }

    private LtlZonesEntity createEntity() {
        LtlZonesEntity entity = new LtlZonesEntity();

        entity.setLtlPricProfDetailId(PROFILE_ID);
        entity.setName("Name: " + Math.random());
        entity.setStatus(Status.ACTIVE);

        return entity;
    }

    private void assertListByStatus(List<ZonesListItemVO> list, Status expectedStatus) {
        for (ZonesListItemVO entity : list) {
            assertEquals(entity.getStatus(), expectedStatus);
            assertFalse(entity.getGeography().isEmpty());
        }
    }
}
