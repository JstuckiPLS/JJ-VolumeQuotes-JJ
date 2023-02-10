package com.pls.ltlrating.dao.profile;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.dao.LtlPricingCarrierTypesDao;
import com.pls.ltlrating.domain.LtlPricingCarrierTypesEntity;

/**
 * Test cases for {@link LtlPricingCarrierTypesDao} class.
 *
 * @author Gleb Zgonikov
 */
public class LtlRatingCarrierTypesDaoImplIT extends AbstractDaoTest {

    @Autowired
    private LtlPricingCarrierTypesDao dao;

    @Test
    public void testGetAll() {
        List<LtlPricingCarrierTypesEntity> entities = dao.getAll();
        Assert.assertNotNull(entities);
        Assert.assertTrue(entities.size() > 0);
    }
}
