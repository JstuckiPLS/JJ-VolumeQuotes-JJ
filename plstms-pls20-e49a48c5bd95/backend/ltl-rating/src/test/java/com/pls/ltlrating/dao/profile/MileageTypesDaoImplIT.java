package com.pls.ltlrating.dao.profile;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.dao.MileageTypesDao;
import com.pls.ltlrating.domain.MileageTypesEntity;

/**
 * Test cases for {@link MileageTypesDao} class.
 *
 * @author Gleb Zgonikov
 */
public class MileageTypesDaoImplIT extends AbstractDaoTest {

    @Autowired
    private MileageTypesDao dao;

    @Test
    public void testGetAll() {
        List<MileageTypesEntity> entities = dao.getAll();
        Assert.assertNotNull(entities);
        Assert.assertTrue(entities.size() > 0);
    }
}
