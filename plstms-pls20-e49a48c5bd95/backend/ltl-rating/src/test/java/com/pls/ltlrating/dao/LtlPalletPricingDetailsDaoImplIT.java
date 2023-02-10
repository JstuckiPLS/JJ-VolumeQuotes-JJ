package com.pls.ltlrating.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.WeightUOM;

/**
 * Test cases for {@link LtlPalletPricingDetailsDao}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPalletPricingDetailsDaoImplIT extends AbstractDaoTest {

    private static final Long CURRENT_USER = 1L;
    private static final Long MODIFIED_USER = -1L;
    private static final Long PROFILE_DETAIL_ID = 1L;
    private static final Long PRICE_DETAIL_ID = 1L;
    private static final Long BLANKET_ID = 1L;

    @Autowired
    private LtlPalletPricingDetailsDao sut;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlPalletPricingDetailsEntity actualResult = sut.find(PRICE_DETAIL_ID);
        assertNotNull(actualResult);
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        LtlPalletPricingDetailsEntity newEntity = createMinimalEntity();

        newEntity = sut.saveOrUpdate(newEntity);
        assertNotNull(newEntity);
        assertNotNull(newEntity.getId());

        flushAndClearSession();

        LtlPalletPricingDetailsEntity afterSave = (LtlPalletPricingDetailsEntity) getSession().get(
                LtlPalletPricingDetailsEntity.class,
                newEntity.getId());
        assertNotNull(afterSave);
        assertEquals(newEntity, afterSave);
    }

    @Test
    public void testInactivateByProfileId() throws Exception {
        LtlPalletPricingDetailsEntity activeEntity = sut.find(PRICE_DETAIL_ID);
        assertNotNull(activeEntity);
        assertTrue(activeEntity.getStatus() == Status.ACTIVE);

        sut.updateStatus(PRICE_DETAIL_ID, Status.INACTIVE, MODIFIED_USER);

        flushAndClearSession();

        LtlPalletPricingDetailsEntity actualEntity = sut.find(PRICE_DETAIL_ID);
        assertNotNull(actualEntity);
        assertTrue(actualEntity.getStatus() == Status.INACTIVE);
        assertEquals(MODIFIED_USER, actualEntity.getModification().getModifiedBy());
    }

    @Test
    public void testFindActiveAndEffectiveByProfileDetailId() throws Exception {
        List<LtlPalletPricingDetailsEntity> result = sut.findActiveAndEffective(PROFILE_DETAIL_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for (LtlPalletPricingDetailsEntity entity : result) {
            assertEquals(PROFILE_DETAIL_ID, entity.getProfileDetailId());
            assertEquals(Status.ACTIVE, entity.getStatus());
            assertTrue(entity.getEffDate().compareTo(new Date()) < 0);
            assertTrue(entity.getExpDate() == null || entity.getExpDate().compareTo(new Date()) > 0);
        }
    }

    @Test
    public void testFindByInactiveStatusAndDetailId() throws Exception {
        List<LtlPalletPricingDetailsEntity> result = sut.findByStatusAndDetailId(PROFILE_DETAIL_ID, Status.INACTIVE);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for (LtlPalletPricingDetailsEntity entity : result) {
            assertEquals(PROFILE_DETAIL_ID, entity.getProfileDetailId());
            assertEquals(Status.INACTIVE, entity.getStatus());
        }
    }

    @Test
    public void testUpdateStatusByProfileId() throws Exception {
        final Status newStatus = Status.INACTIVE;
        final Long priceDetailId = 1L;

        sut.updateStatus(priceDetailId, newStatus, 1L);

        LtlPalletPricingDetailsEntity afterSave = sut.find(priceDetailId);

        assertNotNull(afterSave);
        assertEquals(newStatus, afterSave.getStatus());
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlPalletPricingDetailsEntity> actualResult = sut.findAllCspChildsCopyedFrom(BLANKET_ID);

        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testUpdateStatusInCSPByCopiedFrom() {
        sut.updateStatusInCSPByCopiedFrom(BLANKET_ID, Status.INACTIVE, MODIFIED_USER);

        List<LtlPalletPricingDetailsEntity> actualCspDetailList = sut.findAllCspChildsCopyedFrom(BLANKET_ID);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlPalletPricingDetailsEntity actualCspEntity = actualCspDetailList.get(0);

        Assert.assertEquals(Status.INACTIVE, actualCspEntity.getStatus());
    }

    private LtlPalletPricingDetailsEntity createMinimalEntity() {
        LtlPalletPricingDetailsEntity entity = new LtlPalletPricingDetailsEntity();

        entity.setProfileDetailId(PROFILE_DETAIL_ID);
        entity.setCostType(LtlCostType.MI);
        entity.setCostApplWtUom(WeightUOM.KG);
        entity.setStatus(Status.ACTIVE);
        entity.setServiceType(LtlServiceType.DIRECT);
        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setModifiedBy(CURRENT_USER);

        return entity;
    }
}
