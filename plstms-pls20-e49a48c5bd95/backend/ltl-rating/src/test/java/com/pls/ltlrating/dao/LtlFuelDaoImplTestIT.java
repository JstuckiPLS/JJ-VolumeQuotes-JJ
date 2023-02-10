package com.pls.ltlrating.dao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.pls.ltlrating.dao.impl.LtlFuelDaoImpl;
import com.pls.ltlrating.domain.DotRegionEntity;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelEntity;
import com.pls.ltlrating.domain.bo.FuelListItemVO;
import com.pls.ltlrating.domain.enums.FuelWeekDays;
import com.pls.ltlrating.domain.enums.UpchargeType;

/**
 * Test class for {@link LtlFuelDaoImpl}.
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelDaoImplTestIT extends AbstractDaoTest {

    private static final Long PROFILE_DETAIL_ID = 1L;

    private static final Long CHILD_PROFILE_DETAIL_ID = 9L;

    private static final Long USER_ID = 1L;

    @Autowired
    LtlFuelDao sut;

    @Test
    public void testPersist() {

        LtlFuelEntity ltlFuelEntity = createNotPersistedLtlFuelEntity();

        sut.saveOrUpdate(ltlFuelEntity);
        Long id = ltlFuelEntity.getId();
        Assert.assertNotNull(id);
        flushAndClearSession();

        LtlFuelEntity foundLtlFuelEntity = sut.find(id);

        Assert.assertNotNull(foundLtlFuelEntity);
        Assert.assertEquals(ltlFuelEntity.getId(), foundLtlFuelEntity.getId());
        Assert.assertEquals(ltlFuelEntity.getEffectiveDay(), foundLtlFuelEntity.getEffectiveDay());
        Assert.assertEquals(ltlFuelEntity.getUpchargeType(), foundLtlFuelEntity.getUpchargeType());
        Assert.assertEquals(0, ltlFuelEntity.getUpchargeFlat().compareTo(foundLtlFuelEntity.getUpchargeFlat()));
        Assert.assertEquals(0, ltlFuelEntity.getUpchargePercent().compareTo(foundLtlFuelEntity.getUpchargePercent()));
        Assert.assertEquals((long) (ltlFuelEntity.getEffectiveDate().getTime() / 1000),
                            (long) (foundLtlFuelEntity.getEffectiveDate().getTime() / 1000));
        Assert.assertEquals((long) (ltlFuelEntity.getExpirationDate().getTime() / 1000),
                            (long) (foundLtlFuelEntity.getExpirationDate().getTime() / 1000));
        Assert.assertEquals(ltlFuelEntity.getLtlPricingProfileId(), foundLtlFuelEntity.getLtlPricingProfileId());
    }

    @Test
    public void testFind() {
        LtlFuelEntity ltlFuelEntity = sut.find(1L);

        Assert.assertEquals(new Long(1L), ltlFuelEntity.getId());
        Assert.assertEquals(Status.ACTIVE, ltlFuelEntity.getStatus());
    }

    @Test
    public void testGetFuelTriggersByProfileDetailWithExpireDateLessThan() throws ParseException {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = dataFormat.parse("2099-01-01");
        List<FuelListItemVO> entities =
                sut.getFuelTriggersByProfileDetailWithExpireDateLessThan(1L, now);

        Assert.assertNotNull(entities);
        Assert.assertTrue("list of retrieved entities should not be empty", !entities.isEmpty());
        for (FuelListItemVO e : entities) {
            Assert.assertEquals(new Long(1L), e.getPricingProfileId());
//            Assert.assertEquals(new Long(1L), e.getLtlPricingProfile().getId());
            Assert.assertTrue("expiration date should be less", e.getExpirationDate().before(now));
        }
    }

    @Test
    public void testGetDOTRegionsAndFuelRates() {
        List<DotRegionFuelEntity> dotRegionFuelEntities = sut.getDOTRegionsAndFuelRates();
        Assert.assertNotNull(dotRegionFuelEntities);
        Assert.assertTrue("List of selected DotRegionFuelEntity should not be empty", dotRegionFuelEntities.size() > 0);
    }

    @Test
    public void testUpdateStatus() {
        LtlFuelEntity entity = sut.find(1L);
        Assert.assertNotNull(entity);
        Assert.assertEquals(Status.ACTIVE, entity.getStatus());

        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(1L);

        sut.updateFuelStatus(ids, Status.INACTIVE);
        flushAndClearSession();

        entity = sut.find(1L);
        Assert.assertNotNull(entity);
        Assert.assertEquals(Status.INACTIVE, entity.getStatus());
    }

    @Test
    public void testChange() {
        // test fails from time to time, so need to remove one item just for this test.
        getSession().createQuery("delete from LtlFuelGeoServicesEntity where id = 2").executeUpdate();

        LtlFuelEntity entity = sut.find(1L);
        // Had to add this line here as geo service details are lazy loaded.
        entity.getLtlFuelGeoServices().get(0).getLtlFuelGeoServiceDetails().get(0);
        getSession().evict(entity);
        flushAndClearSession();
        entity.getLtlFuelGeoServices().get(0).getLtlFuelGeoServiceDetails().get(0).setGeoValue("111");
        sut.saveOrUpdate(entity);
        entity = null;
        flushAndClearSession();
        entity = sut.find(1L);
        Assert.assertEquals("111", entity.getLtlFuelGeoServices().get(0).getLtlFuelGeoServiceDetails().get(0).getGeoValue());
    }

    @Test
    public void testUpdateStatusToInactiveByProfileId() {
        sut.updateStatusToInactiveByProfileId(1L, 2L);

        List<FuelListItemVO> entities = sut.getActiveAndEffectiveByProfileDetailId(1L);
        Assert.assertNotNull(entities);
        Assert.assertTrue("Should be empty", entities.size() == 0);
    }

    @Test
    public void testExpireByListOfIds() {
        Long[] ids = { 1L, 2L };
        List<Long> expectedList = Arrays.asList(ids);

        sut.expireByListOfIds(expectedList, 1L);
        flushAndClearSession();

        for (Long id : expectedList) {
            LtlFuelEntity actualEntity = sut.find(id);

            Assert.assertNotNull(actualEntity);
        }
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlFuelEntity> actualList = sut.findAllCspChildsCopyedFrom(1L);

        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void testExpirateCSPByCopiedFrom() {
        List<Long> expectedIds = Arrays.asList(1L);

        Calendar expectedCalendar = Calendar.getInstance();
        expectedCalendar.setTime(new Date());

        sut.expirateCSPByCopiedFrom(expectedIds, 1L);
        List<LtlFuelEntity> actualCSPList = sut.findAllCspChildsCopyedFrom(1L);
        Assert.assertNotNull(actualCSPList);
        Assert.assertFalse(actualCSPList.isEmpty());

        LtlFuelEntity actualEntity = actualCSPList.get(0);
        Assert.assertNotNull(actualEntity);

        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(actualEntity.getExpirationDate());

        Assert.assertEquals(expectedCalendar.get(Calendar.YEAR), actualCalendar.get(Calendar.YEAR));
        Assert.assertEquals(expectedCalendar.get(Calendar.MONTH), actualCalendar.get(Calendar.MONTH));
        Assert.assertEquals(expectedCalendar.get(Calendar.DATE), actualCalendar.get(Calendar.DATE));
    }

    @Test
    public void testUpdateStatusInCSPByCopiedFrom() {
        List<Long> actualIds = Arrays.asList(1L);

        sut.updateStatusInCSPByCopiedFrom(actualIds, Status.INACTIVE, 1L);

        List<LtlFuelEntity> actualCspDetailList = sut
                .findAllCspChildsCopyedFrom(1L);

        Assert.assertFalse(actualCspDetailList.isEmpty());
        LtlFuelEntity actualCspEntity = actualCspDetailList.get(0);

        Assert.assertEquals(Status.INACTIVE, actualCspEntity.getStatus());
    }

    @Test
    public void testInactivateCSPByProfileDetailId() {
        sut.inactivateCSPByProfileDetailId(PROFILE_DETAIL_ID, USER_ID);
        flushAndClearSession();

        List<LtlFuelEntity> actualList = sut.getAllFuelTriggersByProfileDetailId(CHILD_PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlFuelEntity entity : actualList) {
            Assert.assertNotNull(entity);
            Assert.assertEquals(Status.INACTIVE, entity.getStatus());
        }
    }

    private LtlFuelEntity createNotPersistedLtlFuelEntity() {
        LtlFuelEntity ltlFuelEntity = new LtlFuelEntity();
        ltlFuelEntity.setLtlPricingProfileId(1L);
        ltlFuelEntity.setEffectiveDay(FuelWeekDays.THU);
        ltlFuelEntity.setEffectiveDate(new Date());
        ltlFuelEntity.setExpirationDate(new Date());
        ltlFuelEntity.setUpchargeType(UpchargeType.FL);
        ltlFuelEntity.setUpchargeFlat(new BigDecimal("20"));
        ltlFuelEntity.setUpchargePercent(new BigDecimal("5"));
        ltlFuelEntity.setStatus(Status.ACTIVE);

        ltlFuelEntity.setDotRegion(createPersistedDotRegionEntity());

        return ltlFuelEntity;
    }

    private DotRegionEntity createPersistedDotRegionEntity() {
        DotRegionEntity dotRegionEntity = new DotRegionEntity();
        dotRegionEntity.setDotRegionName("region name 1");
        dotRegionEntity.setDescription("region description 1");
        getSession().save(dotRegionEntity);
        flushAndClearSession();
        return dotRegionEntity;
    }
}
