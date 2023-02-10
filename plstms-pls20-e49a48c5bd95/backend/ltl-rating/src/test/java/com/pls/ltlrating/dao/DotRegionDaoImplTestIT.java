package com.pls.ltlrating.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.dao.impl.DotRegionDaoImpl;
import com.pls.ltlrating.domain.DotRegionEntity;

/**
 * Test cases for {@link DotRegionDaoImpl}.
 *
 * @author Artem Arapov
 *
 */
public class DotRegionDaoImplTestIT extends AbstractDaoTest {

    @Autowired
    private DotRegionDao sut;

    @Test
    public void testGetAll() {
        List<DotRegionEntity> actualList = sut.getAll();

        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }
}
