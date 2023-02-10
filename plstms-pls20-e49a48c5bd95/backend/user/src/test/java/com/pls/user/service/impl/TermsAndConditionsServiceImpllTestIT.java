package com.pls.user.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.dao.PromoCodesDao;
import com.pls.core.domain.user.PromoCodeEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.user.service.TermsAndConditionsService;

/**
 * Test cases for {@link TermsAndConditionsServiceImpl} class.
 * 
 * @author Brichak Aleksandr
 */

public class TermsAndConditionsServiceImpllTestIT extends BaseServiceITClass {

    private static final Long SYSADMIN_USER = 1L;

    @Autowired
    private TermsAndConditionsService sut;

    @Autowired
    private PromoCodesDao promoCodeDao;

    @Test
    public void testaApplyTermsAndConditions() {
        SecurityTestUtils.login("SYSADMIN", SYSADMIN_USER, true, new String[] { "Test Capability" });
        PromoCodeEntity promoCode = promoCodeDao.getPromoCodeByUser();
        Assert.assertNull(promoCode);
        sut.applyTermsAndConditions();
        promoCode = promoCodeDao.getPromoCodeByUser();
        Assert.assertNotNull(promoCode);
        Assert.assertNotNull(promoCode.getTermsAndConditionsVersion());
        Assert.assertEquals((long) promoCode.getTermsAndConditionsVersion(), 1L);
    }

    @Test
    public void testaisTermsAndConditionsAccepted() {
        SecurityTestUtils.login("SYSADMIN", SYSADMIN_USER, true, new String[] { "Test Capability" });
        Assert.assertFalse(sut.isTermsAndConditionsAccepted());
        sut.applyTermsAndConditions();
        Assert.assertTrue(sut.isTermsAndConditionsAccepted());
    }
    }
