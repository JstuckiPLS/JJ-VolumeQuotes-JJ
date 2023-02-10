package com.pls.ltlrating.dao.profile;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.dao.LtlPricingTypesDao;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;

/**
 * Test cases for {@link LtlPricingTypesDao} class.
 *
 * @author Gleb Zgonikov
 */
public class LtlPricingTypesDaoImplIT extends AbstractDaoTest {

    @Autowired
    private LtlPricingTypesDao dao;

    @Test
    public void testGetAll() {
        List<LtlPricingTypesEntity> entities = dao.getAll();
        Assert.assertNotNull(entities);
        Assert.assertTrue(entities.size() > 0);
    }
}
