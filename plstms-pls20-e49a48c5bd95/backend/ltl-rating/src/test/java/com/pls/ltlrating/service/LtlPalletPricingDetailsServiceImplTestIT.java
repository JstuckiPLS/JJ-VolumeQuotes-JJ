package com.pls.ltlrating.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPalletPricingDetailsDao;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.WeightUOM;
import com.pls.ltlrating.service.impl.LtlPalletPricingDetailsServiceImpl;

/**
 * Integration tests for {@link LtlPalletPricingDetailsServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPalletPricingDetailsServiceImplTestIT extends BaseServiceITClass {

    private static final Long BLANKET_PROFILE_DETAIL_ID = 1L;

    private static final Long BLANKET_DETAIL_ID = 1L;

    @Autowired
    private LtlPalletPricingDetailsService sut;

    @Autowired
    private LtlPalletPricingDetailsDao dao;

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testSavePalletPricingEntity() throws Exception {
        List<LtlPalletPricingDetailsEntity> expectedList = sut.findActiveAndEffective(BLANKET_DETAIL_ID);
        for (LtlPalletPricingDetailsEntity item : expectedList) {
            populateEntityWithRandomValues(item);
        }
        LtlPalletPricingDetailsEntity expectedEntity = populateEntityWithRandomValues(new LtlPalletPricingDetailsEntity());
        expectedEntity.setProfileDetailId(BLANKET_DETAIL_ID);
        expectedList.add(expectedEntity);

        sut.saveList(expectedList, BLANKET_DETAIL_ID);

        List<LtlPalletPricingDetailsEntity> actualList = sut.findActiveAndEffective(BLANKET_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        asserLists(expectedList, actualList);
    }

    @Test
    public void testInactivate() {
        SecurityTestUtils.login("sysadmin");

        LtlPalletPricingDetailsEntity existingEntity = dao.find(BLANKET_DETAIL_ID);
        Assert.assertNotNull(existingEntity);
        Assert.assertEquals(Status.ACTIVE, existingEntity.getStatus());

        sut.inactivate(BLANKET_DETAIL_ID, BLANKET_PROFILE_DETAIL_ID);
        flushAndClearSession();

        LtlPalletPricingDetailsEntity actualEntity = dao.find(BLANKET_DETAIL_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(Status.INACTIVE, actualEntity.getStatus());

        List<LtlPalletPricingDetailsEntity> actualChildList = dao.findAllCspChildsCopyedFrom(BLANKET_DETAIL_ID);
        Assert.assertNotNull(actualChildList);
        Assert.assertFalse(actualChildList.isEmpty());
        LtlPalletPricingDetailsEntity actualChild = actualChildList.get(0);
        Assert.assertNotNull(actualChild);
        Assert.assertEquals(Status.INACTIVE, actualChild.getStatus());
    }

    private LtlPalletPricingDetailsEntity populateEntityWithRandomValues(LtlPalletPricingDetailsEntity entity) {
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

        entity.setCostApplMaxWt(new BigDecimal(Math.random()));
        entity.setCostApplMinWt(new BigDecimal(Math.random()));
        entity.setCostApplWtUom(WeightUOM.KG);
        entity.setCostType(LtlCostType.CW);
        entity.setEffDate(today);
        entity.setExpDate(tomorrow);
        entity.setMaxQuantity((long) Math.random() * 100);
        entity.setMinQuantity((long) Math.random() * 100);
        entity.setServiceType(LtlServiceType.DIRECT);
        entity.setStatus(Status.ACTIVE);
        entity.setTransitTime((long) Math.random() * 100);
        entity.setUnitCost(new BigDecimal(Math.random()));
        entity.setZoneFrom(1L);
        entity.setZoneTo(2L);

        return entity;
    }

    private void asserLists(List<LtlPalletPricingDetailsEntity> expectedList, List<LtlPalletPricingDetailsEntity> actualList) {
        for (LtlPalletPricingDetailsEntity expected : expectedList) {
            boolean isEqual = false;
            for (LtlPalletPricingDetailsEntity actual : actualList) {
                isEqual = equals(expected, actual);
                if (isEqual) {
                    break;
                }
            }
            Assert.assertTrue(isEqual);
        }
    }

    private boolean equals(LtlPalletPricingDetailsEntity lhs, LtlPalletPricingDetailsEntity rhs) {
        if (lhs == rhs) {
            return true;
        } else {
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(lhs.getCostApplMaxWt(), rhs.getCostApplMaxWt())
                    .append(lhs.getCostApplMinWt(), rhs.getCostApplMinWt())
                    .append(lhs.getCostApplWtUom(), rhs.getCostApplWtUom())
                    .append(lhs.getCostType(), rhs.getCostType()).append(lhs.getEffDate(), rhs.getEffDate())
                    .append(lhs.getExpDate(), rhs.getExpDate()).append(lhs.getMaxQuantity(), rhs.getMaxQuantity())
                    .append(lhs.getMinQuantity(), rhs.getMinQuantity())
                    .append(lhs.getServiceType(), rhs.getServiceType()).append(lhs.getStatus(), rhs.getStatus())
                    .append(lhs.getTransitTime(), rhs.getTransitTime()).append(lhs.getUnitCost(), rhs.getUnitCost())
                    .append(lhs.getZoneFrom(), rhs.getZoneFrom()).append(lhs.getZoneTo(), rhs.getZoneTo())
                    .append(lhs.getIsExcludeFuel(), rhs.getIsExcludeFuel());

            return builder.isEquals();
        }
    }
}
