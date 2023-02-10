package com.pls.ltlrating.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.impl.LtlGuaranteedPriceDaoImpl;
import com.pls.ltlrating.domain.LtlGuaranteedBlockDestEntity;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.ChargeRuleTypeEnum;
import com.pls.ltlrating.domain.bo.GuaranteedPriceListItemVO;

/**
 * Test cases for using {@link LtlGuaranteedPriceDaoImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlGuaranteedPriceDaoImplIT extends AbstractDaoTest {

    private static final Long GUARANTEED_PRICE_ID_FIRST = 1L;

    private static final Long GUARANTEED_PRICE_ID_SECCOND = 2L;

    private static final Long PRICE_PROFILE_ID = 1L;

    private static final Long CURRENT_USER = 1L;

    private static final Long BLANKET_GUARANTEED = 1L;

    private static final Long EXPECTED_CHILD_CSP_ID = 47L;

    private static final Long PROFILE_DETAIL_ID = 1L;

    private static final Long CHILD_PROFILE_DETAIL_ID = 9L;

    @Autowired
    private LtlGuaranteedPriceDao sut;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlGuaranteedPriceEntity entity = sut.find(GUARANTEED_PRICE_ID_FIRST);

        assertNotNull(entity);
        assertEquals(GUARANTEED_PRICE_ID_FIRST, entity.getId());
    }

    @Test
    public void testSave() throws Exception {
        LtlGuaranteedPriceEntity minimalEntity = createMinimalEntity();

        LtlGuaranteedPriceEntity afterSave = sut.saveOrUpdate(minimalEntity);

        flushAndClearSession();

        assertNotNull(afterSave);
        assertNotNull(afterSave.getId());

        LtlGuaranteedPriceEntity actualEntity = (LtlGuaranteedPriceEntity) getSession().get(
                LtlGuaranteedPriceEntity.class, afterSave.getId());

        assertLtlGuaranteedPriceEntity(afterSave, actualEntity);
    }

    @Test
    public void testFindAllGuaranteedPriceByProfileDetailId() throws Exception {
        List<LtlGuaranteedPriceEntity> list = sut.findByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testFindByActiveStatusAndProfileDetailId() throws Exception {
        List<GuaranteedPriceListItemVO> list = sut.findByStatusAndProfileDetailId(Status.ACTIVE, PRICE_PROFILE_ID);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testFindByInactiveStatusAndProfileDetailId() throws Exception {
        List<GuaranteedPriceListItemVO> list = sut.findByStatusAndProfileDetailId(Status.INACTIVE, PRICE_PROFILE_ID);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testFindActiveAndEffectiveByProfileDetailId() throws Exception {
        List<GuaranteedPriceListItemVO> list = sut.findActiveAndEffectiveByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testFindExpiredByProfileDetailId() throws Exception {
        List<GuaranteedPriceListItemVO> list = sut.findExpiredByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testUpdateStatus() throws Exception {
        final Status newStatus = Status.INACTIVE;
        List<Long> guaranteedIds = new ArrayList<Long>();
        guaranteedIds.add(GUARANTEED_PRICE_ID_FIRST);
        guaranteedIds.add(GUARANTEED_PRICE_ID_SECCOND);

        sut.updateStatusOfGuaranteedPriceList(guaranteedIds, newStatus, CURRENT_USER);

        LtlGuaranteedPriceEntity afterSaveFirst = sut.find(GUARANTEED_PRICE_ID_FIRST);

        assertNotNull(afterSaveFirst);
        assertEquals(newStatus, afterSaveFirst.getStatus());

        LtlGuaranteedPriceEntity afterSaveSeccond = sut.find(GUARANTEED_PRICE_ID_SECCOND);

        assertNotNull(afterSaveSeccond);
        assertEquals(newStatus, afterSaveSeccond.getStatus());
    }

    @Test
    public void testExpireByListOfIds() {
        Long[] ids = { 1L, 2L };
        List<Long> expectedList = Arrays.asList(ids);

        sut.expireByListOfIds(expectedList, CURRENT_USER);
        flushAndClearSession();

        for (Long id : expectedList) {
            LtlGuaranteedPriceEntity actualEntity = sut.find(id);

            assertNotNull(actualEntity);
        }
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlGuaranteedPriceEntity> actualList = sut.findAllCspChildsCopyedFrom(BLANKET_GUARANTEED);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        LtlGuaranteedPriceEntity actualEntity = actualList.get(0);
        Assert.assertEquals(EXPECTED_CHILD_CSP_ID, actualEntity.getId());
    }

    @Test
    public void testExpirateCSPByCopiedFrom() {
        List<Long> actualIds = Arrays.asList(BLANKET_GUARANTEED);

        Calendar expectedCalendar = Calendar.getInstance();
        expectedCalendar.setTime(new Date());

        sut.expirateCSPByCopiedFrom(actualIds, CURRENT_USER);
        List<LtlGuaranteedPriceEntity> actualCspDetailList = sut
                .findAllCspChildsCopyedFrom(BLANKET_GUARANTEED);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlGuaranteedPriceEntity actualCspEntity = actualCspDetailList.get(0);

        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(actualCspEntity.getExpDate());

        Assert.assertEquals(expectedCalendar.get(Calendar.YEAR), actualCalendar.get(Calendar.YEAR));
        Assert.assertEquals(expectedCalendar.get(Calendar.MONTH), actualCalendar.get(Calendar.MONTH));
        Assert.assertEquals(expectedCalendar.get(Calendar.DATE), actualCalendar.get(Calendar.DATE));
    }

    @Test
    public void testUpdateStatusInCSPByCopiedFrom() {
        List<Long> actualIds = Arrays.asList(BLANKET_GUARANTEED);

        sut.updateStatusInCSPByCopiedFrom(actualIds, Status.INACTIVE, CURRENT_USER);

        List<LtlGuaranteedPriceEntity> actualCspDetailList = sut
                .findAllCspChildsCopyedFrom(BLANKET_GUARANTEED);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlGuaranteedPriceEntity actualCspEntity = actualCspDetailList.get(0);

        Assert.assertEquals(Status.INACTIVE, actualCspEntity.getStatus());
    }

    @Test
    public void testInactivateCSPByProfileDetailId() {
        sut.inactivateCSPByProfileDetailId(PROFILE_DETAIL_ID, CURRENT_USER);
        flushAndClearSession();

        List<LtlGuaranteedPriceEntity> actualList = sut.findByProfileDetailId(CHILD_PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlGuaranteedPriceEntity entity : actualList) {
            Assert.assertNotNull(entity);
            Assert.assertEquals(Status.INACTIVE, entity.getStatus());
        }
    }

    private LtlGuaranteedPriceEntity createMinimalEntity() {
        LtlGuaranteedPriceEntity entity = new LtlGuaranteedPriceEntity();

        entity.setLtlPricProfDetailId(PRICE_PROFILE_ID);
        entity.setApplyBeforeFuel(Boolean.TRUE);
        entity.setChargeRuleType(ChargeRuleTypeEnum.FL);
        entity.setStatus(Status.ACTIVE);

        entity.getGuaranteedBlockDestinations().add(createRandomGuaranteedBlockDestEntity());
        entity.getGuaranteedBlockDestinations().add(createRandomGuaranteedBlockDestEntity());

        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setModifiedBy(CURRENT_USER);

        return entity;
    }

    private LtlGuaranteedBlockDestEntity createRandomGuaranteedBlockDestEntity() {
        LtlGuaranteedBlockDestEntity entity = new LtlGuaranteedBlockDestEntity();

        entity.setDestination(String.valueOf(Math.random()));
        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setModifiedBy(CURRENT_USER);

        return entity;
    }

    private void assertLtlGuaranteedPriceEntity(LtlGuaranteedPriceEntity expected, LtlGuaranteedPriceEntity actual) {
        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getLtlPricProfDetailId(), actual.getLtlPricProfDetailId());
        assertEquals(expected.getApplyBeforeFuel(), actual.getApplyBeforeFuel());
        assertEquals(expected.getChargeRuleType(), actual.getChargeRuleType());
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}
