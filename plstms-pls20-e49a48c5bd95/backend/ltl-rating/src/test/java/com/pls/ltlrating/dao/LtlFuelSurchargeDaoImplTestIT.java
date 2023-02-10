package com.pls.ltlrating.dao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;

/**
 * {@link LtlFuelSurchargeDao} test.
 *
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelSurchargeDaoImplTestIT extends AbstractDaoTest {

    private static final Long ACTUAL_ID = 1L;

    private static final Long EXPECTED_COPY_ID = 133L;

    private static final Long MODIFIED_BY = 1L;

    private static final Long EXPECTED_BLANKET_FUEL_SURCHARGE_ID = 1L;

    private static final Long PROFILE_DETAIL_ID = 1L;

    private static final Long CHILD_PROFILE_DETAIL_ID = 9L;

    @Autowired
    private LtlFuelSurchargeDao sut;

    @Test
    public void testPersist() {

        BigDecimal minRate = BigDecimal.valueOf(1);
        BigDecimal maxRate = BigDecimal.valueOf(5);
        BigDecimal surcharge = BigDecimal.valueOf(2);

        List<LtlFuelSurchargeEntity> exitsingFuelSurchargeEntities = sut.getAll();

        LtlFuelSurchargeEntity ltlFuelSurchargeEntity = new LtlFuelSurchargeEntity();
        ltlFuelSurchargeEntity.setLtlPricingProfileId(1L);
        ltlFuelSurchargeEntity.setMinRate(minRate);
        ltlFuelSurchargeEntity.setMaxRate(maxRate);
        ltlFuelSurchargeEntity.setSurcharge(surcharge);
        ltlFuelSurchargeEntity.setStatus(Status.ACTIVE);

        sut.saveOrUpdate(ltlFuelSurchargeEntity);

        flushAndClearSession();

        List<LtlFuelSurchargeEntity> updatedFuelSurchargeEntities = sut.getAll();

        Assert.assertEquals(exitsingFuelSurchargeEntities.size() + 1, updatedFuelSurchargeEntities.size());

        updatedFuelSurchargeEntities.removeAll(exitsingFuelSurchargeEntities);
        ltlFuelSurchargeEntity = updatedFuelSurchargeEntities.get(0);
        Assert.assertEquals(0, minRate.compareTo(ltlFuelSurchargeEntity.getMinRate()));
        Assert.assertEquals(0, maxRate.compareTo(ltlFuelSurchargeEntity.getMaxRate()));
        Assert.assertEquals(0, surcharge.compareTo(ltlFuelSurchargeEntity.getSurcharge()));
        Assert.assertEquals(Status.ACTIVE, ltlFuelSurchargeEntity.getStatus());
    }

    @Test
    public void testGetActiveFuelSurchargeByProfileDetailId() {
        List<LtlFuelSurchargeEntity> list = sut.getActiveFuelSurchargeByProfileDetailId(1L);
        Assert.assertTrue("List of LtlFuelSurchargeEntity should not be empty", list.size() > 0);
    }

    @Test
    public void testGetFuelSurchargeByFuelCharge() {
        BigDecimal surcharge = sut.getFuelSurchargeByFuelCharge(new BigDecimal("4.51"));
        Assert.assertNotNull(surcharge);
        Assert.assertEquals(0, new BigDecimal("22").compareTo(surcharge));
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlFuelSurchargeEntity> actualList = sut.findAllCspChildsCopyedFrom(ACTUAL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());

        LtlFuelSurchargeEntity actualCopy = actualList.get(0);
        Assert.assertNotNull(actualCopy);
        Assert.assertEquals(EXPECTED_COPY_ID, actualCopy.getId());
        Assert.assertEquals(ACTUAL_ID, actualCopy.getCopiedFrom());
    }

    @Test
    public void testUpdateStatusByListOfIds() {
        List<Long> expectedIds = Arrays.asList(ACTUAL_ID);
        LtlFuelSurchargeEntity existedEntity = sut.find(ACTUAL_ID);
        Assert.assertNotNull(existedEntity);
        Assert.assertEquals(Status.ACTIVE, existedEntity.getStatus());

        sut.updateStatusByListOfIds(expectedIds, Status.INACTIVE, MODIFIED_BY);
        flushAndClearSession();

        LtlFuelSurchargeEntity actualEntity = sut.find(ACTUAL_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(Status.INACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testUpdateStatusInCSPByCopiedFrom() {
        List<Long> actualIds = Arrays.asList(ACTUAL_ID);

        sut.updateStatusInCSPByCopiedFrom(actualIds, Status.INACTIVE, MODIFIED_BY);

        List<LtlFuelSurchargeEntity> actualCspDetailList = sut
                .findAllCspChildsCopyedFrom(EXPECTED_BLANKET_FUEL_SURCHARGE_ID);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlFuelSurchargeEntity actualCspEntity = actualCspDetailList.get(0);

        Assert.assertEquals(Status.INACTIVE, actualCspEntity.getStatus());
    }

    @Test
    public void testGetAllByProfileDetailId() {
        List<LtlFuelSurchargeEntity> actualList = sut.getAllByProfileDetailId(PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlFuelSurchargeEntity entity : actualList) {
            Assert.assertNotNull(entity);
            Assert.assertEquals(PROFILE_DETAIL_ID, entity.getLtlPricingProfileId());
        }
    }

    @Test
    public void testInactivateCSPByProfileDetailId() {
        sut.inactivateCSPByProfileDetailId(PROFILE_DETAIL_ID, MODIFIED_BY);
        flushAndClearSession();

        List<LtlFuelSurchargeEntity> actualList = sut.getAllByProfileDetailId(CHILD_PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlFuelSurchargeEntity entity : actualList) {
            Assert.assertNotNull(entity);
            Assert.assertEquals(Status.INACTIVE, entity.getStatus());
        }
    }
}
