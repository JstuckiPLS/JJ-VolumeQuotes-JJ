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
import com.pls.ltlrating.dao.impl.LtlBlockCarrGeoServicesDaoImpl;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.bo.BlockCarrierListItemVO;

/**
 * Test cases for {@link LtlBlockCarrGeoServicesDaoImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlBlockCarrGeoServicesDaoImplIT extends AbstractDaoTest {

    private static final Long BLOCK_CARRIER_ID = 1L;

    private static final Long PROFILE_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final Long BLANKET_ID = 1L;

    private static final Long PROFILE_DETAIL_ID = 1L;

    private static final Long CHILD_PROFILE_DETAIL_ID = 9L;

    @Autowired
    private LtlBlockCarrGeoServicesDao sut;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlBlockCarrGeoServicesEntity entity = sut.find(BLOCK_CARRIER_ID);

        assertNotNull(entity);
    }

    @Test
    public void testSave() throws Exception {
        LtlBlockCarrGeoServicesEntity entity = createEntity();

        LtlBlockCarrGeoServicesEntity afterSave = sut.saveOrUpdate(entity);
        assertNotNull(afterSave);
        assertNotNull(afterSave.getId());

        flushAndClearSession();

        LtlBlockCarrGeoServicesEntity actualEntity = sut.find(afterSave.getId());
        assertNotNull(actualEntity);
    }

    @Test
    public void testFindByActiveStatus() throws Exception {
        List<BlockCarrierListItemVO> resultList = sut.findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertListByStatus(resultList, Status.ACTIVE);
    }

    @Test
    public void testFindByInactiveStatus() throws Exception {
        List<BlockCarrierListItemVO> resultList = sut.findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertListByStatus(resultList, Status.INACTIVE);
    }

    @Test
    public void testActivate() throws Exception {
        List<Long> ids = Arrays.asList(4L, 5L);

        sut.updateStatus(ids, Status.ACTIVE, USER_ID);

        flushAndClearSession();

        LtlBlockCarrGeoServicesEntity actualEntity = sut.find(4L);
        assertEquals(Status.ACTIVE, actualEntity.getStatus());

        actualEntity = sut.find(5L);
        assertEquals(Status.ACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testInactivate() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        sut.updateStatus(ids, Status.INACTIVE, USER_ID);

        flushAndClearSession();

        LtlBlockCarrGeoServicesEntity actualEntity = sut.find(1L);
        assertEquals(Status.INACTIVE, actualEntity.getStatus());

        actualEntity = sut.find(2L);
        assertEquals(Status.INACTIVE, actualEntity.getStatus());

        actualEntity = sut.find(3L);
        assertEquals(Status.INACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlBlockCarrGeoServicesEntity> actualResult = sut.findAllCspChildsCopyedFrom(BLANKET_ID);

        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testUpdateStatusInCSPByCopiedFrom() {
        List<Long> expectedIds = Arrays.asList(BLANKET_ID);

        sut.updateStatusInCSPByCopiedFrom(expectedIds, Status.INACTIVE, USER_ID);

        List<LtlBlockCarrGeoServicesEntity> actualCspList = sut.findAllCspChildsCopyedFrom(BLANKET_ID);
        Assert.assertNotNull(actualCspList);
        Assert.assertFalse(actualCspList.isEmpty());

        LtlBlockCarrGeoServicesEntity actualCspEntity = actualCspList.get(0);
        Assert.assertNotNull(actualCspEntity);
        Assert.assertEquals(Status.INACTIVE, actualCspEntity.getStatus());
    }

    @Test
    public void testFindByProfileDetailId() {
        List<LtlBlockCarrGeoServicesEntity> actualList = sut.findByProfileDetailId(PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlBlockCarrGeoServicesEntity entity : actualList) {
            Assert.assertNotNull(entity);
            Assert.assertEquals(PROFILE_DETAIL_ID, entity.getProfileId());
        }
    }

    @Test
    public void testInactivateCSPByProfileDetailId() {
        sut.inactivateCSPByProfileDetailId(PROFILE_DETAIL_ID, USER_ID);
        flushAndClearSession();

        List<LtlBlockCarrGeoServicesEntity> childCSPList = sut.findByProfileDetailId(CHILD_PROFILE_DETAIL_ID);
        Assert.assertNotNull(childCSPList);
        Assert.assertFalse(childCSPList.isEmpty());
        for (LtlBlockCarrGeoServicesEntity child : childCSPList) {
            Assert.assertNotNull(child);
            Assert.assertEquals(Status.INACTIVE, child.getStatus());
        }
    }

    private LtlBlockCarrGeoServicesEntity createEntity() {
        LtlBlockCarrGeoServicesEntity entity = new LtlBlockCarrGeoServicesEntity();
        entity.setDestination("Destination " + Math.random());
        entity.setOrigin("Origin " + Math.random());
        entity.setStatus(Status.ACTIVE);
        entity.setProfileId(PROFILE_ID);
        entity.getModification().setCreatedBy(USER_ID);

        return entity;
    }

    private void assertListByStatus(List<BlockCarrierListItemVO> list, Status expectedStatus) {
        for (BlockCarrierListItemVO entity : list) {
            assertEquals(entity.getStatus(), expectedStatus);
        }
    }
}
