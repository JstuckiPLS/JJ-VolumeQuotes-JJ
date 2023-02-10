package com.pls.ltlrating.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlFakMapEntity;
import com.pls.ltlrating.domain.LtlPricGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingGeoServicesEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;
import com.pls.ltlrating.domain.enums.DistanceUOM;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.WeightUOM;

/**
 * Test cases of using {@link LtlPricingDetailsDao}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPricingDetailsDaoImplIT extends AbstractDaoTest {

    private static final Long CURRENT_USER = 1L;
    private static final Long MODIFIED_USER = -1L;
    private static final Long PROFILE_ID = 1L;
    private static final Long EXPECTED_BLANKET_PRICING_DETAIL_ID = 1L;
    private static final Long EXPECTED_CSP_PRICING_DETAIL_ID = 51L;
    private static final Long PROFILE_DETAIL_ID = 1L;

    @Autowired
    private LtlPricingDetailsDao sut;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlPricingDetailsEntity actualResult = sut.find(1L);
        assertNotNull(actualResult);
    }

    @Test
    public void testDeleteChildRecord() throws Exception {
        LtlPricingDetailsEntity entity = sut.find(1L);
        assertNotNull(entity);
        assertEquals(2, entity.getGeoServices().size());

        LtlPricingGeoServicesEntity existing = (LtlPricingGeoServicesEntity) entity.getGeoServices().toArray()[0];
        entity.getGeoServices().clear();
        entity.getGeoServices().add(existing);

        sut.merge(entity);
        flushAndClearSession();

        entity = sut.find(1L);
        assertEquals(1, entity.getGeoServices().size());
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        LtlPricingDetailsEntity newEntity = createMinimalEntity();

        Set<LtlPricingGeoServicesEntity> actualGeoServices = new HashSet<LtlPricingGeoServicesEntity>();

        actualGeoServices.add(createRandomLtlPricingGeoServicesEntity(newEntity));
        actualGeoServices.add(createRandomLtlPricingGeoServicesEntity(newEntity));
        newEntity.getGeoServices().addAll(actualGeoServices);

        Set<LtlFakMapEntity> actualFakMaps = new HashSet<LtlFakMapEntity>();
        actualFakMaps.add(createRandomLtlFakMapEntity(newEntity));
        actualFakMaps.add(createRandomLtlFakMapEntity(newEntity));
        newEntity.getFakMapping().addAll(actualFakMaps);

        newEntity = sut.saveOrUpdate(newEntity);
        assertNotNull(newEntity);
        assertNotNull(newEntity.getId());

        flushAndClearSession();

        LtlPricingDetailsEntity afterSave = (LtlPricingDetailsEntity) getSession().get(LtlPricingDetailsEntity.class, newEntity.getId());
        assertNotNull(afterSave);
        assertLtlPricingGeoServices(actualGeoServices, afterSave.getGeoServices());
        assertLtlFakMapEntity(actualFakMaps, afterSave.getFakMapping());
    }

    @Test
    public void testInactivateByProfileId() throws Exception {
        List<PricingDetailListItemVO> activeList = sut.findActiveAndEffectiveByProfileDetailId(PROFILE_ID);

        sut.updateStatusToInactiveByProfileId(PROFILE_ID, MODIFIED_USER);

        flushAndClearSession();

        List<PricingDetailListItemVO> actualResult = sut.findActiveAndEffectiveByProfileDetailId(PROFILE_ID);
        assertTrue(actualResult.isEmpty());

        for (PricingDetailListItemVO item : activeList) {
            LtlPricingDetailsEntity actualEntity = sut.find(item.getId());
            assertEquals(MODIFIED_USER, actualEntity.getModification().getModifiedBy());
        }
    }

    @Test
    public void testFindActiveAndEffectiveByProfileDetailId() throws Exception {
        List<PricingDetailListItemVO> result = sut.findActiveAndEffectiveByProfileDetailId(PROFILE_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for (PricingDetailListItemVO entity : result) {
            assertEquals(PROFILE_ID, entity.getProfileId());
        }
    }

    @Test
    public void testFindByInactiveStatusAndProfileId() throws Exception {
        List<PricingDetailListItemVO> result = sut.findArchivedPrices(PROFILE_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for (PricingDetailListItemVO entity : result) {
            assertEquals(PROFILE_ID, entity.getProfileId());
        }
    }

    @Test
    public void testFindExpiredByProfileDetailId() throws Exception {
        List<PricingDetailListItemVO> result = sut.findExpiredByProfileDetailId(PROFILE_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for (PricingDetailListItemVO entity : result) {
            assertEquals(PROFILE_ID, entity.getProfileId());
        }
    }

    @Test
    public void testUpdateStatusByProfileId() throws Exception {
        Long[] ids = { 1L, 2L };
        List<Long> expectedList = Arrays.asList(ids);
        final Status expectedStatus = Status.INACTIVE;

        sut.updateStatus(expectedList, expectedStatus, CURRENT_USER);
        flushAndClearSession();

        for (Long priceDetailId : expectedList) {
            LtlPricingDetailsEntity actualEntity = sut.find(priceDetailId);

            assertNotNull(actualEntity);
            assertEquals(expectedStatus, actualEntity.getStatus());
        }
    }

    @Test
    public void testUpdateStatusToExpired() {
        Long[] ids = { 1L, 2L };
        List<Long> expectedList = Arrays.asList(ids);

        sut.updateStatusToExpired(expectedList, CURRENT_USER);
        flushAndClearSession();

        for (Long priceDetailId : expectedList) {
            LtlPricingDetailsEntity actualEntity = sut.find(priceDetailId);

            assertNotNull(actualEntity);
        }
    }

    @Test
    public void testFindAllCspChildCopyedFrom() {
        List<LtlPricingDetailsEntity> actualCspDetailList = sut.findAllByCopiedFrom(EXPECTED_BLANKET_PRICING_DETAIL_ID);
        Assert.assertNotNull(actualCspDetailList);
        Assert.assertFalse(actualCspDetailList.isEmpty());

        LtlPricingDetailsEntity actualCSPDetail = actualCspDetailList.get(0);
        Assert.assertNotNull(actualCSPDetail);
        Assert.assertEquals(EXPECTED_CSP_PRICING_DETAIL_ID, actualCSPDetail.getId());
        Assert.assertEquals(EXPECTED_BLANKET_PRICING_DETAIL_ID, actualCSPDetail.getCopiedFrom());
    }

    @Test
    public void testExpirateCSPByCopiedFrom() {
        List<Long> actualIds = Arrays.asList(EXPECTED_BLANKET_PRICING_DETAIL_ID);

        Calendar expectedCalendar = Calendar.getInstance();
        expectedCalendar.setTime(new Date());

        sut.expirateCSPByCopiedFrom(actualIds, MODIFIED_USER);
        List<LtlPricingDetailsEntity> actualCspDetailList = sut.findAllByCopiedFrom(EXPECTED_BLANKET_PRICING_DETAIL_ID);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlPricingDetailsEntity actualCspEntity = actualCspDetailList.get(0);

        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(actualCspEntity.getExpDate());

        Assert.assertEquals(expectedCalendar.get(Calendar.YEAR), actualCalendar.get(Calendar.YEAR));
        Assert.assertEquals(expectedCalendar.get(Calendar.MONTH), actualCalendar.get(Calendar.MONTH));
        Assert.assertEquals(expectedCalendar.get(Calendar.DATE), actualCalendar.get(Calendar.DATE));
    }

    @Test
    public void testUpdateStatusInCSPByCopiedFrom() {
        List<Long> actualIds = Arrays.asList(EXPECTED_BLANKET_PRICING_DETAIL_ID);

        sut.updateStatusInCSPByCopiedFrom(actualIds, Status.INACTIVE, MODIFIED_USER);

        List<LtlPricingDetailsEntity> actualCspDetailList = sut.findAllByCopiedFrom(EXPECTED_BLANKET_PRICING_DETAIL_ID);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlPricingDetailsEntity actualCspEntity = actualCspDetailList.get(0);

        Assert.assertEquals(Status.INACTIVE, actualCspEntity.getStatus());
    }

    @Test
    public void testInactivateCSPByProfileDetailId() {
        sut.updateStatusToInactiveByProfileId(PROFILE_DETAIL_ID, MODIFIED_USER);
        flushAndClearSession();

        List<PricingDetailListItemVO> actualList = sut.findArchivedPrices(PROFILE_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        Assert.assertTrue(actualList.stream().anyMatch(d -> PROFILE_DETAIL_ID.equals(d.getId())));
    }

    private LtlPricingDetailsEntity createMinimalEntity() {
        LtlPricingDetailsEntity entity = new LtlPricingDetailsEntity();

        entity.setLtlPricProfDetailId(1L);
        entity.setCostType(LtlCostType.MI);
        entity.setCostApplWtUom(WeightUOM.KG);
        entity.setCostApplDistUom(DistanceUOM.KM);
        entity.setStatus(Status.ACTIVE);
        entity.setServiceType(LtlServiceType.DIRECT);
        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setModifiedBy(CURRENT_USER);

        return entity;
    }

    private LtlPricingGeoServicesEntity createRandomLtlPricingGeoServicesEntity(LtlPricingDetailsEntity price) {
        LtlPricingGeoServicesEntity entity = new LtlPricingGeoServicesEntity();
        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setModifiedBy(CURRENT_USER);
        entity.setPricingDetail(price);
        entity.setOriginDetails(createGeoDetail(entity, GeoType.ORIGIN));
        entity.setDestinationDetails(createGeoDetail(entity, GeoType.DESTINATION));
        return entity;
    }

    private Set<LtlPricGeoServiceDetailsEntity> createGeoDetail(LtlPricingGeoServicesEntity geoService, GeoType geoType) {
        LtlPricGeoServiceDetailsEntity detail = new LtlPricGeoServiceDetailsEntity();
        detail.setGeoService(geoService);
        detail.setGeoType(geoType);
        detail.setGeoServType(5);
        detail.setGeoValue(geoType.name() + Math.random());
        detail.setGeoValueSearchable(geoType.name());
        Set<LtlPricGeoServiceDetailsEntity> result = new HashSet<>();
        result.add(detail);
        return result;
    }

    private LtlFakMapEntity createRandomLtlFakMapEntity(LtlPricingDetailsEntity price) {
        LtlFakMapEntity entity = new LtlFakMapEntity();
        entity.setPricingDetail(price);
        entity.setActualClass(CommodityClass.CLASS_100);
        entity.setMappingClass(CommodityClass.CLASS_110);
        return entity;
    }

    private void assertLtlPricingGeoServices(Set<LtlPricingGeoServicesEntity> expectedSet, Set<LtlPricingGeoServicesEntity> actualSet) {
        assertNotNull(actualSet);
        assertEquals(expectedSet.size(), actualSet.size());
    }

    private void assertLtlFakMapEntity(Set<LtlFakMapEntity> expectedSet, Set<LtlFakMapEntity> actualSet) {
        assertNotNull(actualSet);
        assertEquals(expectedSet.size(), actualSet.size());
    }
}
