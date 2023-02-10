package com.pls.ltlrating.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlGuaranteedPriceDao;
import com.pls.ltlrating.domain.LtlGuaranBlockDestDetailsEntity;
import com.pls.ltlrating.domain.LtlGuaranteedBlockDestEntity;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.ChargeRuleTypeEnum;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.impl.LtlGuaranteedPriceServiceImpl;

/**
 * Use cases {@link LtlGuaranteedPriceServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlGuaranteedPriceServiceImplTestIT extends BaseServiceITClass {
    private static final Long GUARANTEED_BLANKET = 1L;

    @Autowired
    private LtlGuaranteedPriceService sut;

    @Autowired
    private LtlGuaranteedPriceDao dao;

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testSaveGuaranteedAddNewEntity() throws Exception {
        LtlGuaranteedPriceEntity expectedEntity = createRandomLtlGuaranteedPriceEntity();

        LtlGuaranteedPriceEntity actualEntity = sut.saveGuaranteedPrice(expectedEntity);
        Assert.assertNotNull(actualEntity);

        List<LtlGuaranteedPriceEntity> actualCopiedList = dao.findAllCspChildsCopyedFrom(actualEntity.getId());
        Assert.assertNotNull(actualCopiedList);
        Assert.assertFalse(actualCopiedList.isEmpty());

        LtlGuaranteedPriceEntity actualCopiedEntity = actualCopiedList.get(0);
        Assert.assertNotNull(actualCopiedEntity);
        assertGuaranteedEntity(actualEntity, actualCopiedEntity);
        Assert.assertEquals(actualEntity.getId(), actualCopiedEntity.getCopiedFrom());
    }

    @Test
    public void testSaveGuaranteedUpdateEntity() throws Exception {
        LtlGuaranteedPriceEntity expectedBlanketEntity = sut.getGuaranteedPriceById(GUARANTEED_BLANKET);
        Assert.assertNotNull(expectedBlanketEntity);

        expectedBlanketEntity = populateGuaranteedWithRandomValues(expectedBlanketEntity);
        LtlGuaranteedBlockDestEntity expectedBlockDestEntity = expectedBlanketEntity.getGuaranteedBlockDestinations().iterator().next();
        expectedBlockDestEntity = populateBlockDestWithRandomValues(expectedBlockDestEntity);
        expectedBlanketEntity.getGuaranteedBlockDestinations().add(createRandomLtlGuaranteedBlockDestEntity());

        LtlGuaranteedPriceEntity actualBlanketEntity = sut.saveGuaranteedPrice(expectedBlanketEntity);
        Assert.assertNotNull(actualBlanketEntity);

        List<LtlGuaranteedPriceEntity> actualCopiedList = dao.findAllCspChildsCopyedFrom(expectedBlanketEntity.getId());
        Assert.assertNotNull(actualCopiedList);
        Assert.assertFalse(actualCopiedList.isEmpty());
        LtlGuaranteedPriceEntity actualCopiedEntity = actualCopiedList.get(0);
        assertGuaranteedEntity(expectedBlanketEntity, actualCopiedEntity);
    }

    @Test
    public void testInactivateGuaranteedPricings() {
        SecurityTestUtils.login("sysadmin");

        LtlGuaranteedPriceEntity existingEntity = sut.getGuaranteedPriceById(GUARANTEED_BLANKET);
        Assert.assertNotNull(existingEntity);
        Assert.assertEquals(Status.ACTIVE, existingEntity.getStatus());

        List<LtlGuaranteedPriceEntity> existingCopiedList = dao.findAllCspChildsCopyedFrom(existingEntity.getId());
        Assert.assertNotNull(existingCopiedList);
        Assert.assertFalse(existingCopiedList.isEmpty());
        LtlGuaranteedPriceEntity existingCopiedEntity = existingCopiedList.get(0);
        Assert.assertNotNull(existingCopiedEntity);
        Assert.assertEquals(Status.ACTIVE, existingCopiedEntity.getStatus());

        List<Long> expectedIds = Arrays.asList(GUARANTEED_BLANKET);
        sut.inactivateGuaranteedPricings(expectedIds, 1L, true);
        flushAndClearSession();

        LtlGuaranteedPriceEntity actualEntity = sut.getGuaranteedPriceById(GUARANTEED_BLANKET);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(Status.INACTIVE, actualEntity.getStatus());

        List<LtlGuaranteedPriceEntity> actualCopiedList = dao.findAllCspChildsCopyedFrom(existingEntity.getId());
        Assert.assertNotNull(actualCopiedList);
        Assert.assertFalse(actualCopiedList.isEmpty());
        LtlGuaranteedPriceEntity actualCopiedEntity = actualCopiedList.get(0);
        Assert.assertNotNull(actualCopiedEntity);
        Assert.assertEquals(Status.INACTIVE, actualCopiedEntity.getStatus());
    }

    private LtlGuaranteedPriceEntity createRandomLtlGuaranteedPriceEntity() {
        LtlGuaranteedPriceEntity entity = new LtlGuaranteedPriceEntity();
        entity.setLtlPricProfDetailId(1L);
        entity.setApplyBeforeFuel(Boolean.FALSE);
        entity.setBollCarrierName(String.valueOf(Math.random()));
        entity.setChargeRuleType(ChargeRuleTypeEnum.FL);
        entity.setEffDate(new Date());
        entity.setExpDate(new Date());
        entity.setMinCost(new BigDecimal(Math.random() * 100));
        entity.setStatus(Status.ACTIVE);
        entity.setTime((long) Math.random() * 100);
        entity.setUnitCost(new BigDecimal(Math.random()));
        entity.getGuaranteedBlockDestinations().add(createRandomLtlGuaranteedBlockDestEntity());

        return entity;
    }

    private LtlGuaranteedBlockDestEntity createRandomLtlGuaranteedBlockDestEntity() {
        LtlGuaranteedBlockDestEntity entity = new LtlGuaranteedBlockDestEntity();
        entity.setOrigin(String.valueOf(Math.random()));
        entity.setDestination(String.valueOf(Math.random()));

        entity.getLtlGuaranDestinationDetails().clear();
        entity.getLtlGuaranOriginDetails().clear();
        entity.getLtlGuaranOriginDetails().add(
                new LtlGuaranBlockDestDetailsEntity(entity, entity.getOrigin(), GeoType.ORIGIN, 5, entity.getOrigin()));
        entity.getLtlGuaranDestinationDetails().add(
                new LtlGuaranBlockDestDetailsEntity(entity, entity.getDestination(), GeoType.DESTINATION, 5, entity.getDestination()));

        return entity;
    }

    private LtlGuaranteedPriceEntity populateGuaranteedWithRandomValues(LtlGuaranteedPriceEntity entity) {
        entity.setApplyBeforeFuel(Boolean.FALSE);
        entity.setBollCarrierName(String.valueOf(Math.random()));
        entity.setChargeRuleType(ChargeRuleTypeEnum.FL);
        entity.setEffDate(new Date());
        entity.setExpDate(new Date());
        entity.setMinCost(new BigDecimal(Math.random() * 100));
        entity.setStatus(Status.ACTIVE);
        entity.setTime((long) Math.random() * 100);
        entity.setUnitCost(new BigDecimal(Math.random()));

        return entity;
    }

    private LtlGuaranteedBlockDestEntity populateBlockDestWithRandomValues(LtlGuaranteedBlockDestEntity entity) {
        entity.setOrigin(String.valueOf(Math.random()));
        entity.setDestination(String.valueOf(Math.random()));

        entity.getLtlGuaranDestinationDetails().clear();
        entity.getLtlGuaranOriginDetails().clear();
        entity.getLtlGuaranDestinationDetails().add(
                new LtlGuaranBlockDestDetailsEntity(entity, entity.getDestination(), GeoType.DESTINATION, 5, entity.getDestination()));
        entity.getLtlGuaranOriginDetails().add(
                new LtlGuaranBlockDestDetailsEntity(entity, entity.getOrigin(), GeoType.ORIGIN, 5, entity.getOrigin()));

        return entity;
    }

    private void assertGuaranteedEntity(LtlGuaranteedPriceEntity expected, LtlGuaranteedPriceEntity actual) {
        Assert.assertEquals(expected.getBollCarrierName(), actual.getBollCarrierName());
        Assert.assertEquals(expected.getApplyBeforeFuel(), actual.getApplyBeforeFuel());
        Assert.assertEquals(expected.getChargeRuleType(), actual.getChargeRuleType());
        Assert.assertEquals(expected.getEffDate(), actual.getEffDate());
        Assert.assertEquals(expected.getMinCost(), actual.getMinCost());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
        Assert.assertEquals(expected.getTime(), actual.getTime());
        Assert.assertEquals(expected.getUnitCost(), actual.getUnitCost());

        Assert.assertNotNull(actual.getGuaranteedBlockDestinations());
        for (LtlGuaranteedBlockDestEntity item : actual.getGuaranteedBlockDestinations()) {
            setOriginDestination(item);
        }
        for (LtlGuaranteedBlockDestEntity item : expected.getGuaranteedBlockDestinations()) {
            Assert.assertTrue(actual.getGuaranteedBlockDestinations().contains(item));
        }
    }

    private void setOriginDestination(LtlGuaranteedBlockDestEntity item) {
        List<String> geoCodes = new ArrayList<String>();

        for (LtlGuaranBlockDestDetailsEntity detail : item.getLtlGuaranOriginDetails()) {
            geoCodes.add(detail.getGeoValue());
        }
        item.setOrigin(StringUtils.join(geoCodes, ','));

        geoCodes.clear();
        for (LtlGuaranBlockDestDetailsEntity detail : item.getLtlGuaranDestinationDetails()) {
            geoCodes.add(detail.getGeoValue());
        }
        item.setDestination(StringUtils.join(geoCodes, ','));
    }
}
