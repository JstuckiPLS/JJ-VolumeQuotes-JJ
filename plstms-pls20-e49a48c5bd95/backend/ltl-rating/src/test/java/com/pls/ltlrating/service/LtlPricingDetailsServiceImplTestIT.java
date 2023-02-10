package com.pls.ltlrating.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlFakMapEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingGeoServicesEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;
import com.pls.ltlrating.service.impl.LtlPricingDetailsServiceImpl;

/**
 * Integration tests for {@link LtlPricingDetailsServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPricingDetailsServiceImplTestIT extends BaseServiceITClass {

    private static final Long EXPECTED_PROFILE_DETAIL_ID = 1L;
    private static final Long BLANKET_ENTITY_ID = 35L;
    private static final Long BLANKET_CSP_ENTITY_ID = 51L;
    private static final Long BLANKET_CSP_PROFILE_DETAIL_ID = 9L;
    private static final CommodityClass EXPECTED_ACTUAL_CLASS = CommodityClass.CLASS_500;
    private static final CommodityClass EXPECTED_MAPPING_CLASS = CommodityClass.CLASS_500;

    @Autowired
    private LtlPricingDetailsService sut;

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testSavePricingDetailDetachedEntity() throws Exception {
        LtlPricingDetailsEntity expectedEntity = sut.getPricingDetailById(1L);
        LtlPricingGeoServicesEntity removedEntity = expectedEntity.getGeoServices().iterator().next();
        expectedEntity.getGeoServices().remove(removedEntity);

        LtlPricingDetailsEntity attachedEntity = sut.savePricingDetail(expectedEntity);
        Assert.assertNotNull(attachedEntity);

        LtlPricingDetailsEntity actualEntity = sut.getPricingDetailById(1L);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(expectedEntity, actualEntity);
        Assert.assertEquals(expectedEntity.getGeoServices().size(), attachedEntity.getGeoServices().size());
    }

    @Test
    public void testSavePricingDetailDeleteGeoServices() throws Exception {
        LtlPricingDetailsEntity expectedBlanketEntity = sut.getPricingDetailById(BLANKET_ENTITY_ID);
        Assert.assertNotNull(expectedBlanketEntity);

        Set<LtlPricingGeoServicesEntity> expectedGeoServicesList = expectedBlanketEntity
                .getGeoServices();
        Assert.assertNotNull(expectedGeoServicesList);
        Assert.assertFalse(expectedGeoServicesList.isEmpty());

        LtlPricingGeoServicesEntity entityToRemove = expectedGeoServicesList.iterator().next();
        expectedGeoServicesList.remove(entityToRemove);

        LtlPricingDetailsEntity actualBlanketEntity = sut.savePricingDetail(expectedBlanketEntity);
        Assert.assertNotNull(actualBlanketEntity);

        LtlPricingDetailsEntity actualBlanketCspEntity = sut.getPricingDetailById(BLANKET_CSP_ENTITY_ID);
        Assert.assertNotNull(actualBlanketCspEntity);

        Set<LtlPricingGeoServicesEntity> actualGeoServicesList = actualBlanketCspEntity.getGeoServices();
        Assert.assertNotNull(actualGeoServicesList);
        Assert.assertTrue(actualGeoServicesList.isEmpty());
    }

    @Test
    public void testSavePricingDetailNotUpdateFAKMappings() throws Exception {
        LtlPricingDetailsEntity expectedBlanketEntity = sut.getPricingDetailById(BLANKET_ENTITY_ID);
        Assert.assertNotNull(expectedBlanketEntity);

        Set<LtlFakMapEntity> fakMappings = expectedBlanketEntity.getFakMapping();
        Assert.assertNotNull(fakMappings);
        Assert.assertFalse(fakMappings.isEmpty());

        LtlFakMapEntity expectedFakMappingEntity = fakMappings.iterator().next();
        Assert.assertNotNull(expectedFakMappingEntity);

        expectedFakMappingEntity.setMappingClass(EXPECTED_MAPPING_CLASS);
        expectedFakMappingEntity.setActualClass(EXPECTED_ACTUAL_CLASS);

        LtlPricingDetailsEntity actualBlanketEntity = sut.savePricingDetail(expectedBlanketEntity);
        Assert.assertNotNull(actualBlanketEntity);
        Assert.assertEquals(expectedBlanketEntity, actualBlanketEntity);

        LtlPricingDetailsEntity actualBlanketCspEntity = sut.getPricingDetailById(BLANKET_CSP_ENTITY_ID);
        Assert.assertNotNull(actualBlanketCspEntity);

        Set<LtlFakMapEntity> actualFakMappings = actualBlanketCspEntity.getFakMapping();
        Assert.assertNotNull(actualFakMappings);
        Assert.assertTrue(actualFakMappings.isEmpty());
    }

    @Test
    public void testSavePricingDetailAddFAKMappings() throws Exception {
        LtlPricingDetailsEntity expectedBlanketEntity = sut.getPricingDetailById(BLANKET_ENTITY_ID);
        Assert.assertNotNull(expectedBlanketEntity);

        Set<LtlFakMapEntity> fakMappings = expectedBlanketEntity.getFakMapping();
        Assert.assertNotNull(fakMappings);

        LtlFakMapEntity expectedFakMappingEntity = createRandomLtlFakMapEntity(expectedBlanketEntity);
        fakMappings.add(expectedFakMappingEntity);

        LtlPricingDetailsEntity actualBlanketEntity = sut.savePricingDetail(expectedBlanketEntity);
        Assert.assertNotNull(actualBlanketEntity);
        Assert.assertEquals(expectedBlanketEntity, actualBlanketEntity);

        LtlPricingDetailsEntity actualBlanketCspEntity = sut.getPricingDetailById(BLANKET_CSP_ENTITY_ID);
        Assert.assertNotNull(actualBlanketCspEntity);

        Set<LtlFakMapEntity> actualFakMappings = actualBlanketCspEntity.getFakMapping();
        Assert.assertNotNull(actualFakMappings);
        Assert.assertTrue(actualFakMappings.isEmpty());
    }

    @Test
    public void testSavePricingDetailDeleteFAKMappings() throws Exception {
        LtlPricingDetailsEntity expectedBlanketEntity = sut.getPricingDetailById(BLANKET_ENTITY_ID);
        Assert.assertNotNull(expectedBlanketEntity);

        Set<LtlFakMapEntity> expectedFakMappingsList = expectedBlanketEntity.getFakMapping();
        Assert.assertNotNull(expectedFakMappingsList);
        Assert.assertFalse(expectedFakMappingsList.isEmpty());

        LtlFakMapEntity expectedFakMappingEntity = expectedFakMappingsList.iterator().next();
        Assert.assertNotNull(expectedFakMappingEntity);
        expectedFakMappingsList.remove(expectedFakMappingEntity);

        LtlPricingDetailsEntity actualBlanketEntity = sut.savePricingDetail(expectedBlanketEntity);
        Assert.assertNotNull(actualBlanketEntity);
        Assert.assertEquals(expectedBlanketEntity, actualBlanketEntity);

        LtlPricingDetailsEntity actualBlanketCspEntity = sut.getPricingDetailById(BLANKET_CSP_ENTITY_ID);
        Assert.assertNotNull(actualBlanketCspEntity);

        Set<LtlFakMapEntity> actualFakMappingsList = actualBlanketCspEntity.getFakMapping();
        Assert.assertNotNull(actualFakMappingsList);
        Assert.assertTrue(actualFakMappingsList.isEmpty());
    }

    @Test
    public void testInactivatePricingDetail() {
        SecurityTestUtils.login("sysadmin");

        List<PricingDetailListItemVO> actualBlanketActiveList =
                sut.inactivatePricingDetails(Arrays.asList(BLANKET_ENTITY_ID), EXPECTED_PROFILE_DETAIL_ID, true);
        Assert.assertNotNull(actualBlanketActiveList);

        LtlPricingDetailsEntity actualBlanketEntity = sut.getPricingDetailById(BLANKET_ENTITY_ID);
        Assert.assertNotNull(actualBlanketEntity);
        Assert.assertEquals(Status.INACTIVE, actualBlanketEntity.getStatus());

        List<PricingDetailListItemVO> inactivePricingDetails = sut.getInactivePricingDetailsByProfileDetailId(BLANKET_CSP_PROFILE_DETAIL_ID);
        Assert.assertTrue("Status of Blanket/CSP profile should be ignored",
                inactivePricingDetails.stream().anyMatch(d -> BLANKET_CSP_ENTITY_ID.equals(d.getId())));
    }

    private LtlFakMapEntity createRandomLtlFakMapEntity(LtlPricingDetailsEntity pricingDetail) {
        LtlFakMapEntity entity = new LtlFakMapEntity();
        entity.setPricingDetail(pricingDetail);
        entity.setActualClass(CommodityClass.CLASS_100);
        entity.setMappingClass(CommodityClass.CLASS_110);

        return entity;
    }
}
