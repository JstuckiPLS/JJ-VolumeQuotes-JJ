package com.pls.ltlrating.service;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockLaneDao;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;
import com.pls.ltlrating.domain.bo.BlockLaneListItemVO;

/**
 * Test for {@link LtlBlockLaneService}.
 * 
 * @author Aleksandr Leshchenko
 */
public class LtlBlockLaneServiceImplTestIT extends BaseServiceITClass {

    private static final Long BLOCK_LANE_ID = 1L;

    private static final Long SHIPPER_ID = 1L;

    private static final Long CARRIER_ID = 66L;

    @Autowired
    private LtlBlockLaneService sut;

    @Autowired
    private LtlBlockLaneDao dao;

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testSaveBlockedLane() throws Exception {
        LtlBlockLaneEntity expectedEntity = populateEntityWithRandomValues(new LtlBlockLaneEntity());
        expectedEntity = sut.saveBlockedLane(expectedEntity);
        assertNotNull(expectedEntity);
        getSession().flush();
        BlockLaneListItemVO actualItem = dao.findById(expectedEntity.getId());
        assertNotNull(actualItem);
    }

    @Test
    public void testGetBlockedLaneById() throws Exception {
        BlockLaneListItemVO actualItem = sut.getBlockedLaneById(BLOCK_LANE_ID);
        assertNotNull(actualItem);
    }

    @Test
    public void testReactivateExpireInactivate() throws Exception {

        List<Long> ids = Arrays.asList(BLOCK_LANE_ID);
        sut.reactivateBlockedLanes(ids, SHIPPER_ID);
        getSession().flush();
        List<BlockLaneListItemVO> actualItemList = dao.findActiveAndEffectiveByProfileId(SHIPPER_ID);
        boolean item = false;
        for (BlockLaneListItemVO blockLaneItem : actualItemList) {
            if (blockLaneItem.getId() == BLOCK_LANE_ID) {
                item = true;
            }
        }
        Assert.assertTrue(item);

        sut.expireBlockedLanes(ids, SHIPPER_ID);
        getSession().flush();
        actualItemList = dao.findActiveAndEffectiveByProfileId(SHIPPER_ID);
        item = false;
        for (BlockLaneListItemVO blockLaneItem : actualItemList) {
            if (blockLaneItem.getId() == BLOCK_LANE_ID) {
                item = true;
            }
        }
        Assert.assertFalse(item);

        sut.inactivateBlockedLanes(ids, SHIPPER_ID, false);
        getSession().flush();
        actualItemList = dao.findExpiredByProfileId(SHIPPER_ID);
        item = false;
        for (BlockLaneListItemVO blockLaneItem : actualItemList) {
            if (blockLaneItem.getId() == BLOCK_LANE_ID) {
                item = true;
            }
        }
        Assert.assertFalse(item);
    }

    private LtlBlockLaneEntity populateEntityWithRandomValues(LtlBlockLaneEntity entity) {
        entity.setCarrierId(CARRIER_ID);
        entity.setShipperId(SHIPPER_ID);
        entity.setStatus(Status.ACTIVE);
        entity.setEffDate(new Date());
        entity.setOrigin("PA");
        entity.setDestination("IL");
        return entity;
    }

}
