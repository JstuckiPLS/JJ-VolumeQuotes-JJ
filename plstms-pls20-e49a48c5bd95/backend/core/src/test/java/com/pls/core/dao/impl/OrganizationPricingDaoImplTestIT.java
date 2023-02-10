package com.pls.core.dao.impl;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;

/**
 * Test cases of using {@link OrganizationPricingDaoImpl}.
 * 
 * @author Artem Arapov
 *
 */
public class OrganizationPricingDaoImplTestIT extends AbstractDaoTest {

    private static final Long SHIPPER_ORG_ID = 1L;
    private static final Status EXPECTED_STATUS = Status.ACTIVE;
    private static final BigDecimal EXPECTED_MIN_ACEPT_MARGIN = new BigDecimal("1.00");
    private static final BigDecimal EXPECTED_DEFAULT_MARGIN = new BigDecimal("2.00");
    private static final BigDecimal EXPECTED_GS_CUST = new BigDecimal("3.00");
    private static final BigDecimal EXPECTED_GS_PLS = new BigDecimal("4.00");
    private static final StatusYesNo EXPECTED_INCLUDE_BENCHMARK_ACC = StatusYesNo.YES;
    private static final StatusYesNo EXPECTED_GAINSHARE = StatusYesNo.YES;

    @Autowired
    private OrganizationPricingDao sut;

    @Test
    public void testGetActivePricing() {
        OrganizationPricingEntity actualResult = sut.getActivePricing(SHIPPER_ORG_ID);

        Assert.assertNotNull(actualResult);
        Assert.assertEquals(Status.ACTIVE, actualResult.getStatus());
        Assert.assertEquals(SHIPPER_ORG_ID, actualResult.getId());
    }

    @Test
    public void testSaveOrUpdate() {
        OrganizationPricingEntity existedEntity = sut.find(SHIPPER_ORG_ID);
        Assert.assertNotNull(existedEntity);

        OrganizationPricingEntity expectedEntity = getRandomOrganizationPricing(existedEntity);

        sut.saveOrUpdate(expectedEntity);
        flushAndClearSession();

        OrganizationPricingEntity actualEntity = sut.find(SHIPPER_ORG_ID);
        System.out.println(expectedEntity);
        System.out.println(actualEntity);
        Assert.assertEquals(expectedEntity, actualEntity);
    }

    private OrganizationPricingEntity getRandomOrganizationPricing(OrganizationPricingEntity source) {
        source.setStatus(EXPECTED_STATUS);
        source.setMinAcceptMargin(EXPECTED_MIN_ACEPT_MARGIN);
        source.setDefaultMargin(EXPECTED_DEFAULT_MARGIN);
        source.setIncludeBenchmarkAcc(EXPECTED_INCLUDE_BENCHMARK_ACC);
        source.setGainshare(EXPECTED_GAINSHARE);
        source.setGsCustPct(EXPECTED_GS_CUST);
        source.setGsPlsPct(EXPECTED_GS_PLS);

        return source;
    }
}
