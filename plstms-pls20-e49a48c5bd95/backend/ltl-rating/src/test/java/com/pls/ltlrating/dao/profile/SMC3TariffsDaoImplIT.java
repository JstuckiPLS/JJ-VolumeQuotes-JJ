package com.pls.ltlrating.dao.profile;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.dao.SMC3TariffsDao;
import com.pls.ltlrating.domain.SMC3TariffsEntity;

/**
 * Test cases for {@link SMC3TariffsDao} class.
 *
 * @author Gleb Zgonikov
 */
public class SMC3TariffsDaoImplIT extends AbstractDaoTest {

    @Autowired
    private SMC3TariffsDao dao;

    @Test
    public void testGetAll() {
        List<SMC3TariffsEntity> entities = dao.getAll();
        Assert.assertNotNull(entities);
        Assert.assertTrue(entities.size() > 0);
    }

}
