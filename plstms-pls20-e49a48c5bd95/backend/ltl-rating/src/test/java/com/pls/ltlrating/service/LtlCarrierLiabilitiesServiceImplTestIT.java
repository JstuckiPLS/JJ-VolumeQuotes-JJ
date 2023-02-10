package com.pls.ltlrating.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;

/**
 * Integration tests for {@link LtlCarrierLiabilitiesService}.
 *
 * @author Artem Arapov
 *
 */
public class LtlCarrierLiabilitiesServiceImplTestIT extends BaseServiceITClass {

    private static final Long BLANKET_PROFILE_ID = 1L;

    private static final Long CHILD_CSP_PROFILE_ID = 8L;

    @Autowired
    private LtlCarrierLiabilitiesService sut;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Test
    public void testSaveCarrierLiabilities() throws Exception {
        List<LtlCarrierLiabilitiesEntity> expectedList = createListWithRandomValues();
        expectedList = sut.saveCarrierLiabilities(expectedList, BLANKET_PROFILE_ID);
        Assert.assertNotNull(expectedList);
        Assert.assertFalse(expectedList.isEmpty());

        List<LtlCarrierLiabilitiesEntity> actualList = sut.getCarrierLiabilitiesByProfileId(CHILD_CSP_PROFILE_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    private List<LtlCarrierLiabilitiesEntity> createListWithRandomValues() {
        List<LtlCarrierLiabilitiesEntity> result = new ArrayList<LtlCarrierLiabilitiesEntity>();
        LtlCarrierLiabilitiesEntity entity = populateEntityWithRandomValues(new LtlCarrierLiabilitiesEntity());
        entity.setFreightClass(CommodityClass.CLASS_100);
        result.add(entity);

        entity = populateEntityWithRandomValues(new LtlCarrierLiabilitiesEntity());
        entity.setFreightClass(CommodityClass.CLASS_200);
        result.add(entity);

        entity = populateEntityWithRandomValues(new LtlCarrierLiabilitiesEntity());
        entity.setFreightClass(CommodityClass.CLASS_500);
        result.add(entity);

        return result;
    }

    private LtlCarrierLiabilitiesEntity populateEntityWithRandomValues(LtlCarrierLiabilitiesEntity entity) {
        entity.setMaxNewProdLiabAmt(new BigDecimal(Math.random()));
        entity.setMaxUsedProdLiabAmt(new BigDecimal(Math.random()));
        entity.setNewProdLiabAmt(new BigDecimal(Math.random()));
        entity.setUsedProdLiabAmt(new BigDecimal(Math.random()));

        return entity;
    }
}