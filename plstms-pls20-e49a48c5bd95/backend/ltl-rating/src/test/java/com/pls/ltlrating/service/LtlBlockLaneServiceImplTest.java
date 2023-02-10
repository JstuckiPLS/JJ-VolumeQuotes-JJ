package com.pls.ltlrating.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockLaneDao;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;
import com.pls.ltlrating.domain.bo.BlanketCarrListItemVO;
import com.pls.ltlrating.domain.bo.BlockLaneListItemVO;
import com.pls.ltlrating.service.impl.LtlBlockLaneServiceImpl;
import com.pls.ltlrating.service.validation.LtlBlockLaneServicesValidator;

/**
 * Test cases for {@link LtlBlockLaneServiceImpl}.
 *
 * @author Ashwini Neelgund
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlBlockLaneServiceImplTest {

    private static final Long BLOCK_LANE_ID = 1L;

    private static final Long SHIPPER_ID = 1L;

    private static final Long CARRIER_ID = 66L;

    private static final Long USER_ID = 1L;

    private LtlBlockLaneEntity entity;

    private BlockLaneListItemVO listItem;

    private BlanketCarrListItemVO blanCarrListItem;

    @InjectMocks
    private LtlBlockLaneServiceImpl sut;

    @Mock
    private LtlBlockLaneDao dao;

    @Mock
    private Validator<LtlBlockLaneServicesValidator> validator;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);

        entity = createEntity();
        listItem = createListItem();
        blanCarrListItem = createBlanCarrListItem();
    }

    @Test
    public void testGetActiveBlockLaneByProfileId() throws Exception {
        List<BlockLaneListItemVO> expectedResult = Arrays.asList(listItem);
        when(dao.findActiveAndEffectiveByProfileId(SHIPPER_ID)).thenReturn(expectedResult);
        List<BlockLaneListItemVO> actualResult = sut.getActiveBlockedLanesByProfileId(SHIPPER_ID);
        verify(dao).findActiveAndEffectiveByProfileId(SHIPPER_ID);
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testGetExpiredBlockLaneByProfileId() throws Exception {
        List<BlockLaneListItemVO> expectedResult = Arrays.asList(listItem);
        when(dao.findExpiredByProfileId(SHIPPER_ID)).thenReturn(expectedResult);
        List<BlockLaneListItemVO> actualResult = sut.getExpiredBlockLaneByProfileId(SHIPPER_ID);
        verify(dao).findExpiredByProfileId(SHIPPER_ID);
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testGetInactiveBlockLaneByProfileId() throws Exception {
        List<BlockLaneListItemVO> expectedResult = Arrays.asList(listItem);
        when(dao.findByStatusAndProfileId(Status.INACTIVE.getStatusCode(), SHIPPER_ID)).thenReturn(expectedResult);
        List<BlockLaneListItemVO> actualResult = sut.getInactiveBlockLaneByProfileId(SHIPPER_ID);
        verify(dao).findByStatusAndProfileId(Status.INACTIVE.getStatusCode(), SHIPPER_ID);
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testGetApplicableBlanketCarrListForCust() throws Exception {
        List<BlanketCarrListItemVO> expectedResult = Arrays.asList(blanCarrListItem);
        when(dao.getUnblockedBlanketCarrListForCust(SHIPPER_ID)).thenReturn(expectedResult);
        List<BlanketCarrListItemVO> actualResult = sut.getApplicableBlanketCarrListForCust(SHIPPER_ID);
        verify(dao).getUnblockedBlanketCarrListForCust(SHIPPER_ID);
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testSaveBlockedLane() throws Exception {
        when(dao.merge(entity)).thenReturn(entity);
        LtlBlockLaneEntity actualResult = sut.saveBlockedLane(entity);
        verify(dao).merge(entity);
        assertNotNull(actualResult);

        LtlBlockLaneEntity expectedResult = entity;
        expectedResult.setId(null);
        when(dao.saveOrUpdate(entity)).thenReturn(entity);
        actualResult = sut.saveBlockedLane(entity);
        verify(dao).saveOrUpdate(entity);
        assertNotNull(actualResult);
    }

    @Test
    public void testGetBlockedLaneById() throws Exception {
        when(dao.findById(BLOCK_LANE_ID)).thenReturn(listItem);
        BlockLaneListItemVO result = sut.getBlockedLaneById(BLOCK_LANE_ID);
        verify(dao).findById(BLOCK_LANE_ID);
        assertNotNull(result);
    }

    @Test
    public void testExpireBlockedLane() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        sut.expireBlockedLanes(ids, SHIPPER_ID);
        verify(dao).expireBlockedLanes(ids, USER_ID);
        verify(dao).findActiveAndEffectiveByProfileId(SHIPPER_ID);
    }

    @Test
    public void testInactivateBlockedLanesFromActiveTab() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        sut.inactivateBlockedLanes(ids, SHIPPER_ID, true);
        verify(dao).updateStatusOfBlockedLanes(ids, Status.INACTIVE, USER_ID);
        verify(dao).findActiveAndEffectiveByProfileId(SHIPPER_ID);
    }

    @Test
    public void testInactivateBlockedLanesFromExpiredTab() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        sut.inactivateBlockedLanes(ids, SHIPPER_ID, false);
        verify(dao).updateStatusOfBlockedLanes(ids, Status.INACTIVE, USER_ID);
        verify(dao).findExpiredByProfileId(SHIPPER_ID);
    }

    @Test
    public void testReactivateBlockedLanes() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        sut.reactivateBlockedLanes(ids, SHIPPER_ID);
        verify(dao).updateStatusOfBlockedLanes(ids, Status.ACTIVE, USER_ID);
        verify(dao).findByStatusAndProfileId(Status.INACTIVE.getStatusCode(), SHIPPER_ID);
    }

    private BlockLaneListItemVO createListItem() {
        BlockLaneListItemVO entity = new BlockLaneListItemVO();
        entity.setId(BLOCK_LANE_ID);
        entity.setCarrierId(CARRIER_ID);
        entity.setShipperId(SHIPPER_ID);
        return entity;
    }

    private LtlBlockLaneEntity createEntity() {
        LtlBlockLaneEntity entity = new LtlBlockLaneEntity();
        entity.setId(BLOCK_LANE_ID);
        entity.setCarrierId(CARRIER_ID);
        entity.setShipperId(SHIPPER_ID);
        entity.setStatus(Status.ACTIVE);
        entity.setEffDate(new Date());
        return entity;
    }

    private BlanketCarrListItemVO createBlanCarrListItem() {
        BlanketCarrListItemVO entity = new BlanketCarrListItemVO();
        entity.setId(CARRIER_ID);
        return entity;
    }

}
