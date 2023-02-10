package com.pls.shipment.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.PromoCodesDao;
import com.pls.core.domain.user.PromoCodeEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.shipment.dao.CarrierEdiCostTypesDao;

/**
 * Test cases for {@link CarrierEdiCostTypesDao}.
 *
 * @author Brichak Aleksandr
 */
public class PromoCodesDaoImplIT extends AbstractDaoTest {

    @Autowired
    private PromoCodesDao sut;

    @Test
    public void testShoudSaveNewPromoCodeEntity() throws EntityNotFoundException {

        PromoCodeEntity promoCode = new PromoCodeEntity();
        UserEntity user = new UserEntity();
        user.setId(1L);
        promoCode.setAccountExecutive(user);
        promoCode.setCode("PROMO_CODE_TEST1");
        promoCode.setPercentage(BigDecimal.ONE);
        promoCode.setStatus(com.pls.core.shared.Status.ACTIVE);
        promoCode = sut.saveOrUpdate(promoCode);
        getSession().flush();
        PromoCodeEntity actualPromoCode = sut.get(promoCode.getId());
        assertEquals(actualPromoCode.getAccountExecutive().getId(), user.getId());
        assertEquals(actualPromoCode.getStatus(), promoCode.getStatus());
        assertEquals(actualPromoCode.getCode(), promoCode.getCode());
        assertEquals(actualPromoCode.getPercentage(), promoCode.getPercentage());
    }

    @Test
    public void testIsPromoCodeUnique() throws EntityNotFoundException {
        boolean isUnique = sut.isPromoCodeUnique("PROMO_CODE_TEST", 1L);
        assertTrue(isUnique);
        PromoCodeEntity promoCode = new PromoCodeEntity();
        UserEntity user = new UserEntity();
        user.setId(1L);
        promoCode.setAccountExecutive(user);
        promoCode.setCode("PROMO_CODE_TEST");
        promoCode.setPercentage(BigDecimal.ONE);
        promoCode.setStatus(com.pls.core.shared.Status.ACTIVE);
        sut.saveOrUpdate(promoCode);
        getSession().flush();
        isUnique = sut.isPromoCodeUnique("PROMO_CODE_TEST", 2L);
        Assert.assertFalse(isUnique);
    }
}

