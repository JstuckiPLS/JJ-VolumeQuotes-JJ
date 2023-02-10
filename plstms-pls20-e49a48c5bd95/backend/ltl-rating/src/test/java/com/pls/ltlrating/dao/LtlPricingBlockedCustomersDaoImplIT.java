package com.pls.ltlrating.dao;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;

/**
 * Test for {@link com.pls.ltlrating.dao.impl.LtlPricingBlockedCustomersDaoImpl} class.
 *
 * @author Sergey Kirichenko
 */
public class LtlPricingBlockedCustomersDaoImplIT extends AbstractDaoTest {

    public static final long PROFILE_ID_WITH_BLOCKED = 1L;
    public static final long PROFILE_ID_WITHOUT_BLOCKED = 4L;

    @Autowired
    private LtlPricingBlockedCustomersDao sut;

    @Test
    public void testGetBlockedCustomerByProfileId() {
        List<LtlPricingBlockedCustomersEntity> result = sut.getExplicitlyBlockedCustomersByProfileId(PROFILE_ID_WITH_BLOCKED);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        result = sut.getExplicitlyBlockedCustomersByProfileId(PROFILE_ID_WITHOUT_BLOCKED);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }
}
