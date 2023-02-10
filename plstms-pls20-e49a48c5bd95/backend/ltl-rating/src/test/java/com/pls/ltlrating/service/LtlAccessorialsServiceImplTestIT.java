package com.pls.ltlrating.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlAccessorialsDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlAccGeoServicesEntity;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.bo.AccessorialListItemVO;
import com.pls.ltlrating.domain.enums.DistanceUOM;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.LtlMarginType;
import com.pls.ltlrating.domain.enums.WeightUOM;
import com.pls.ltlrating.service.impl.LtlAccessorialsServiceImpl;

/**
 * Integration tests for {@link LtlAccessorialsServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlAccessorialsServiceImplTestIT extends BaseServiceITClass {

    private static final Long MAPPING_ID = 3L;
    private static final Long CARRIER_ID = 15L;
    private static final LtlAccessorialsMappingEntity FAKE_ENTITY_1 =
            new LtlAccessorialsMappingEntity("FS", "FSCarrier", true, CARRIER_ID);
    private static final LtlAccessorialsMappingEntity FAKE_ENTITY_2 =
            new LtlAccessorialsMappingEntity("NC", "NCCarrier", false, CARRIER_ID);

    private static final Long BLANKET_ENTITY_ID = 1L;
    private static final Long BLANKET_CSP_ENTITY_ID = 314L;
    private static final Long BLANKET_PROFILE_DETAIL_ID = 1L;
    private static final Long CURRENT_USER = 1L;
    private static final Long BLANKET_CSP_PROFILE_ID = 8L;

    @Autowired
    private LtlAccessorialsService sut;

    @Autowired
    private LtlAccessorialsDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Autowired
    private OrganizationPricingDao orgPriceDao;

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testSaveAccessorialsAddNewEntity() {
        LtlAccessorialsEntity accessorial = new LtlAccessorialsEntity();
        accessorial = changeEntityWithRandomValues(accessorial);
        accessorial.setLtlPricProfDetailId(BLANKET_PROFILE_DETAIL_ID);

        LtlAccessorialsEntity actualBlanketEntity = sut.saveAccessorial(accessorial);
        Assert.assertNotNull(actualBlanketEntity);

        List<LtlAccessorialsEntity> actualCSPList = dao.findAllCspChildsCopyedFrom(actualBlanketEntity.getId());
        Assert.assertNotNull(actualCSPList);
        Assert.assertFalse(actualCSPList.isEmpty());

        LtlAccessorialsEntity actualCSPAccessorial = actualCSPList.get(0);
        Assert.assertNotNull(actualCSPAccessorial);
        assertAccessorialsEntity(actualBlanketEntity, actualCSPAccessorial);
        Assert.assertEquals(LtlMarginType.MC.name(), actualCSPAccessorial.getMarginType());
        BigDecimal expectedDefaultMargin = getDefaultMargin(BLANKET_CSP_PROFILE_ID);
        Assert.assertEquals(expectedDefaultMargin, actualCSPAccessorial.getUnitMargin());
        Assert.assertEquals(actualBlanketEntity.getId(), actualCSPAccessorial.getCopiedFrom());
    }

    @Test
    public void testSaveAccessorialsUpdateCSPChilds() {
        LtlAccessorialsEntity expectedEntity = sut.getAccessorialById(BLANKET_ENTITY_ID);
        Assert.assertNotNull(expectedEntity);

        expectedEntity = changeEntityWithRandomValues(expectedEntity);

        LtlAccessorialsEntity actualEntity = sut.saveAccessorial(expectedEntity);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(expectedEntity, actualEntity);
        assertAccessorialsEntity(expectedEntity, actualEntity);

        LtlAccessorialsEntity actualCspEntity = sut.getAccessorialById(BLANKET_CSP_ENTITY_ID);
        Assert.assertNotNull(actualCspEntity);
        assertAccessorialsEntity(expectedEntity, actualCspEntity);
    }

    @Test
    public void testInactivateAccessorialsWithBlanketType() {
        SecurityTestUtils.login("sysadmin");

        List<AccessorialListItemVO> activeBlanketList =
                sut.inactivateAccessorials(Arrays.asList(BLANKET_ENTITY_ID), BLANKET_PROFILE_DETAIL_ID, true);
        Assert.assertNotNull(activeBlanketList);
        Assert.assertFalse(activeBlanketList.isEmpty());

        LtlAccessorialsEntity actualBlanketEntity = sut.getAccessorialById(BLANKET_ENTITY_ID);
        Assert.assertNotNull(actualBlanketEntity);
        Assert.assertEquals(Status.INACTIVE, actualBlanketEntity.getStatus());

        LtlAccessorialsEntity actualCSPEntity = sut.getAccessorialById(BLANKET_CSP_ENTITY_ID);
        Assert.assertNotNull(actualCSPEntity);
        Assert.assertEquals(Status.INACTIVE, actualCSPEntity.getStatus());
    }

    @Test
    public void testGetAccMappingById() {
        LtlAccessorialsMappingEntity actualAccMapping = sut.getAccMappingById(MAPPING_ID);

        Assert.assertNotNull(actualAccMapping);
        assertEqualsMapping(FAKE_ENTITY_1, actualAccMapping);
    }

    @Test
    public void testGetAccMappingByCarrierId() {
        List<LtlAccessorialsMappingEntity> fakeList = new ArrayList<>();
        fakeList.add(FAKE_ENTITY_1);
        fakeList.add(FAKE_ENTITY_2);
        List<LtlAccessorialsMappingEntity> actualList = sut.getAccessorialsMapping(CARRIER_ID);

        Assert.assertNotNull(actualList);
        Assert.assertEquals(actualList.size(), 2);
        assertEqualsMapping(fakeList.get(0), actualList.get(0));
        assertEqualsMapping(fakeList.get(1), actualList.get(1));
    }

    @Test
    public void testSaveAccMapping() {
        List<LtlAccessorialsMappingEntity> realMappingList = sut.getAccessorialsMapping(CARRIER_ID);
        Assert.assertTrue(realMappingList.get(0).getDefaultAccessorial());

        realMappingList.get(0).setDefaultAccessorial(false);
        realMappingList.get(0).setCarrierCode("FSCarrierChanged");
        sut.saveAccessorialsMapping(realMappingList);

        LtlAccessorialsMappingEntity changedEntity = sut.getAccMappingById(MAPPING_ID);
        Assert.assertFalse(changedEntity.getDefaultAccessorial());
        Assert.assertEquals("FSCarrierChanged", changedEntity.getCarrierCode());
    }

    private void assertEqualsMapping(LtlAccessorialsMappingEntity fake, LtlAccessorialsMappingEntity actual) {
        Assert.assertEquals(fake.getCarrierCode(), actual.getCarrierCode());
        Assert.assertEquals(fake.getPlsCode(), actual.getPlsCode());
        Assert.assertEquals(fake.getCarrierId(), actual.getCarrierId());
        Assert.assertEquals(fake.getDefaultAccessorial(), actual.getDefaultAccessorial());
    }

    private LtlAccessorialsEntity changeEntityWithRandomValues(LtlAccessorialsEntity entity) {
        entity.setCostApplDistUom(DistanceUOM.KM);
        entity.setCostApplMaxDist((long) (Math.random() * 100));
        entity.setCostApplMaxWt((long) (Math.random() * 100));
        entity.setCostApplMinDist((long) (Math.random() * 100));
        entity.setCostApplMinWt((long) (Math.random() * 100));
        entity.setCostApplWtUom(WeightUOM.KG);
        entity.setCostType(LtlCostType.DC);
        entity.setAccessorialType(String.valueOf(Math.random()));
        entity.setEffDate(new Date());
        entity.setExpDate(new Date());
        entity.setStatus(Status.ACTIVE);
        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setCreatedDate(new Date());
        changeGeoServicesWithRandomValue(entity.getLtlAccGeoServicesEntities());

        return entity;
    }

    private void changeGeoServicesWithRandomValue(List<LtlAccGeoServicesEntity> list) {
        for (LtlAccGeoServicesEntity item : list) {
            item.setOrigin(String.valueOf(Math.random()));
            item.setDestination(String.valueOf(Math.random()));
        }
    }

    private void assertAccessorialsEntity(LtlAccessorialsEntity expected, LtlAccessorialsEntity actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getAccessorialType(), actual.getAccessorialType());
        Assert.assertEquals(expected.getCostType(), actual.getCostType());
        Assert.assertEquals(expected.getNotes(), actual.getNotes());
        Assert.assertEquals(expected.getCostApplDistUom(), actual.getCostApplDistUom());
        Assert.assertEquals(expected.getCostApplMaxDist(), actual.getCostApplMaxDist());
        Assert.assertEquals(expected.getCostApplMaxWt(), actual.getCostApplMaxWt());
        Assert.assertEquals(expected.getCostApplMinDist(), actual.getCostApplMinDist());
        Assert.assertEquals(expected.getCostApplMinWt(), actual.getCostApplMinWt());
        Assert.assertEquals(expected.getUnitCost(), actual.getUnitCost());
        Assert.assertEquals(expected.getEffDate(), actual.getEffDate());
        Assert.assertEquals(expected.getExpDate(), actual.getExpDate());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
        assertAccGeoServicesList(expected.getLtlAccGeoServicesEntities(), actual.getLtlAccGeoServicesEntities());
    }

    private void assertAccGeoServicesList(List<LtlAccGeoServicesEntity> expected, List<LtlAccGeoServicesEntity> actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.size(), actual.size());

        for (LtlAccGeoServicesEntity item : expected) {
            Assert.assertTrue(actual.contains(item));
        }
    }

    private BigDecimal getDefaultMargin(Long profileId) {
        LtlPricingApplicableCustomersEntity applicableCustomer = profileDao.findActivePricingApplicableCustomer(profileId);
        OrganizationPricingEntity orgPricing = orgPriceDao.getActivePricing(applicableCustomer.getCustomer().getId());

        return orgPricing.getDefaultMargin();
    }
}