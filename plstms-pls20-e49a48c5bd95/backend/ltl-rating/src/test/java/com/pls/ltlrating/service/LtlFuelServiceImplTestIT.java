package com.pls.ltlrating.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlFuelDao;
import com.pls.ltlrating.domain.DotRegionEntity;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelEntity;
import com.pls.ltlrating.domain.LtlFuelGeoServicesEntity;
import com.pls.ltlrating.domain.bo.FuelListItemVO;
import com.pls.ltlrating.domain.enums.FuelWeekDays;
import com.pls.ltlrating.domain.enums.UpchargeType;
import com.pls.ltlrating.service.impl.LtlFuelServiceImpl;
import com.sun.syndication.io.FeedException;

/**
 * Test for {@link LtlFuelServiceImpl}.
 *
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelServiceImplTestIT extends BaseServiceITClass {

    @Autowired
    private LtlFuelService sut;

    @Autowired
    private LtlFuelDao dao;

    private static final String USERNAME = "SPARTAN1";

    private static final Long FUEL_BLANKET = 3L;

    @Before
    public void before() {
        SecurityTestUtils.login(USERNAME);
    }

    @After
    public void after() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testGetFuelTriggerById() {
        LtlFuelEntity ltlFuelEntity = sut.getFuelTriggerById(1L);
        Assert.assertEquals(new Long(1L), ltlFuelEntity.getId());
        Assert.assertEquals(FuelWeekDays.MON, ltlFuelEntity.getEffectiveDay());
    }

    @Test
    public void testSaveFuelTrigger() {
        LtlFuelEntity ltlFuelEntity = sut.getFuelTriggerById(4L);

        ltlFuelEntity.setEffectiveDay(FuelWeekDays.FRI);

        sut.saveFuelTrigger(ltlFuelEntity);

        ltlFuelEntity = sut.getFuelTriggerById(4L);
        Assert.assertEquals(FuelWeekDays.FRI, ltlFuelEntity.getEffectiveDay());
    }

    @Test
    public void testGetAllFuelTriggersByProfileDetailId() {
        List<LtlFuelEntity> entities = sut.getAllFuelTriggersByProfileDetailId(1L);

        Assert.assertNotNull(entities);
        for (LtlFuelEntity e : entities) {
            Assert.assertEquals(new Long(1L), e.getLtlPricingProfileId());
//            Assert.assertEquals(new Long(1L), e.getLtlPricingProfile().getId());
        }
    }

    @Test
    public void testGetActiveFuelTriggersByProfileDetailId() {
        List<FuelListItemVO> entities = sut.getActiveFuelTriggersByProfileDetailId(1L);

        Assert.assertNotNull(entities);
        for (FuelListItemVO e : entities) {
            Assert.assertEquals(new Long(1L), e.getPricingProfileId());
//            Assert.assertEquals(new Long(1L), e.getLtlPricingProfile().getId());
            Assert.assertEquals(Status.ACTIVE.getCode(), e.getStatus());
        }
    }

    @Test
    public void testGetInactiveFuelTriggersByProfileDetailId() {
        List<FuelListItemVO> entities = sut.getInactiveFuelTriggersByProfileDetailId(1L);

        Assert.assertNotNull(entities);
        for (FuelListItemVO e : entities) {
            Assert.assertEquals(new Long(1L), e.getPricingProfileId());
//            Assert.assertEquals(new Long(1L), e.getLtlPricingProfile().getId());
            Assert.assertEquals(Status.INACTIVE.getCode(), e.getStatus());
        }
    }

    @Test
    public void testGetExpiredFuelTriggersByProfileDetailId() {
        Date now = new Date();
        List<FuelListItemVO> entities = sut.getExpiredFuelTriggersByProfileDetailId(1L);

        Assert.assertNotNull(entities);
        Assert.assertTrue("list of retrieved entities should not be empty", !entities.isEmpty());
        for (FuelListItemVO e : entities) {
            Assert.assertEquals(new Long(1L), e.getPricingProfileId());
//            Assert.assertEquals(new Long(1L), e.getLtlPricingProfile().getId());
            Assert.assertTrue("expiration date should be less", e.getExpirationDate().before(now));
        }
    }

    @Test
    public void testiInactivateFuelTriggers() {
        final Long activeFuelId = 1L;
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(activeFuelId);
        List<FuelListItemVO> entities = sut.inactivateFuelTriggers(ids, 1L, true);
        Assert.assertNotNull(entities);
        for (FuelListItemVO entity : entities) {
            if (entity.getId() == activeFuelId) {
                Assert.assertTrue("Returned set should not include Ltl Fuel with ID=" + activeFuelId, false);
            }
        }
    }

    @Test
    public void testiReactivateFuelTriggers() {
        final Long inactiveFuelId = 2L;
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(inactiveFuelId);
        List<FuelListItemVO> entities = sut.reactivateFuelTriggers(ids, 1L);
        Assert.assertNotNull(entities);
        for (FuelListItemVO entity : entities) {
            if (entity.getId() == inactiveFuelId) {
                Assert.assertTrue("Returned set should not include Ltl Fuel with ID=" + inactiveFuelId, false);
            }
        }
    }

    @Test
    public void testGetDOTRegions() {
        List<DotRegionEntity> actualList = sut.getDotRegions();
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void testRetrieveRegionsFuelRatesFromDOT() throws IllegalArgumentException, FeedException, IOException {
        Map<String, String> dotRegionToCharge = sut.retrieveRegionsFuelRatesFromDOT();
        Assert.assertNotNull(dotRegionToCharge);
        Assert.assertTrue("Map with DOT should not be empty", dotRegionToCharge.size() > 0);
    }

    @Test
    public void testCopyFrom() {
        int shouldBeCopiedNumber = sut.getActiveFuelTriggersByProfileDetailId(2L).size();

        sut.copyFrom(2L, 1L, false);
        List<FuelListItemVO> copiedEntities = sut.getActiveFuelTriggersByProfileDetailId(1L);

        Assert.assertEquals(shouldBeCopiedNumber, copiedEntities.size());
    }

    @Test
    public void testGetFirstDayOfTheWeek() throws Exception {
        LtlFuelServiceImpl ltlObject = new LtlFuelServiceImpl();
        Method method = LtlFuelServiceImpl.class.getDeclaredMethod("getFirstDayOfTheWeek",
                new Class[] { Calendar.class });
        method.setAccessible(true);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dateToChange = formatter.parse("03/01/2015");
        Date expectedDate = formatter.parse("29/12/2014");
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToChange);
        Date receivedDate = (Date) method.invoke(ltlObject, new Object[] { cal });
        Assert.assertEquals(expectedDate.getTime(), receivedDate.getTime());

        dateToChange = formatter.parse("07/02/2015");
        expectedDate = formatter.parse("02/02/2015");
        cal.setTime(dateToChange);
        receivedDate = (Date) method.invoke(ltlObject, new Object[] { cal });
        Assert.assertEquals(expectedDate.getTime(), receivedDate.getTime());

    }

    @Test
    public void testSaveFuelTriggerAddCSPChild() {
        LtlFuelEntity expectedEntity = populateWithRandomValues(new LtlFuelEntity());
        expectedEntity.setLtlPricingProfileId(1L);

        LtlFuelEntity actualEntity = sut.saveFuelTrigger(expectedEntity);
        Assert.assertNotNull(actualEntity);

        List<LtlFuelEntity> actualCSPList = dao.findAllCspChildsCopyedFrom(actualEntity.getId());
        Assert.assertNotNull(actualCSPList);
        Assert.assertFalse(actualCSPList.isEmpty());
        LtlFuelEntity actualChild = actualCSPList.get(0);
        Assert.assertNotNull(actualChild);
        Assert.assertEquals(actualEntity.getId(), actualChild.getCopiedFrom());
    }

    @Test
    public void testSaveFuelTriggerUpdateChild() {
        LtlFuelEntity existingEntity = sut.getFuelTriggerById(FUEL_BLANKET);
        Assert.assertNotNull(existingEntity);

        LtlFuelEntity expectedEntity = populateWithRandomValues(existingEntity);
        LtlFuelEntity actualEntity = sut.saveFuelTrigger(expectedEntity);
        Assert.assertNotNull(actualEntity);

        List<LtlFuelEntity> actualCSPList = dao.findAllCspChildsCopyedFrom(actualEntity.getId());
        Assert.assertNotNull(actualCSPList);
        Assert.assertFalse(actualCSPList.isEmpty());
        LtlFuelEntity actualChild = actualCSPList.get(0);
        Assert.assertNotNull(actualChild);
        assertEntity(actualEntity, actualChild);
    }

    @Test
    public void testReceiveRegionsFuelRates() throws IllegalArgumentException, FeedException, IOException {
        sut.receiveRegionsFuelRates();
        List<DotRegionFuelEntity> regionsRates = sut.getRegionsRates();
        Assert.assertNotNull(regionsRates);
        Assert.assertFalse(regionsRates.isEmpty());
        Assert.assertEquals(regionsRates.size(), 11);
    }

    private LtlFuelEntity populateWithRandomValues(LtlFuelEntity entity) {
        entity.setEffectiveDate(new Date());
        entity.setEffectiveDay(FuelWeekDays.SUN);
        entity.setExpirationDate(new Date());
        entity.setStatus(Status.ACTIVE);
        entity.setUpchargeFlat(new BigDecimal(Math.random()));
        entity.setUpchargePercent(new BigDecimal(Math.random()));
        entity.setUpchargeType(UpchargeType.FL);

        DotRegionEntity dot = new DotRegionEntity();
        dot.setId(1L);
        entity.setDotRegion(dot);

        for (LtlFuelGeoServicesEntity item : entity.getLtlFuelGeoServices()) {
            item.setOrigin(String.valueOf(Math.random()));
        }
        LtlFuelGeoServicesEntity geoEntity = new LtlFuelGeoServicesEntity();
        geoEntity.setOrigin(String.valueOf(Math.random()));
        entity.getLtlFuelGeoServices().add(geoEntity);

        return entity;
    }

    private void assertEntity(LtlFuelEntity expected, LtlFuelEntity actual) {
        Assert.assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        Assert.assertEquals(expected.getEffectiveDay(), actual.getEffectiveDay());
        Assert.assertEquals(expected.getExpirationDate(), actual.getExpirationDate());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
        Assert.assertEquals(expected.getUpchargeFlat(), actual.getUpchargeFlat());
        Assert.assertEquals(expected.getUpchargePercent(), actual.getUpchargePercent());
        Assert.assertEquals(expected.getUpchargeType(), actual.getUpchargeType());

        Assert.assertEquals(expected.getLtlFuelGeoServices().size(), actual.getLtlFuelGeoServices().size());
    }
}
