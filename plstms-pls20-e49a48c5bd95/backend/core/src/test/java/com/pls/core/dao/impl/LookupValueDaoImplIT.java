package com.pls.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.LookupValueDao;
import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.enums.LookupGroup;

/**
 * IT for {@link com.pls.core.dao.impl.LookupValueDaoImpl} .
 * 
 * @author Sergey Vovchuk
 */
public class LookupValueDaoImplIT extends AbstractDaoTest {

    @Autowired
    private LookupValueDao dao;

    @Test
    public void testFindLookupValuesForGLNum() throws Exception {
        List<LookupValueEntity> result = dao.findLookupValuesByGroup(
                Arrays.asList(LookupGroup.CMP_NUM, LookupGroup.BRN_NUM, LookupGroup.SBR_NUM, LookupGroup.DIV_NUM));
        Assert.assertNotNull(result);
        Assert.assertEquals(161, result.size());
    }

    @Test
    public void testFindLookupValuesForFreightCharge() throws Exception {
        List<LookupValueEntity> result = dao
                .findLookupValuesByGroup(Arrays.asList(LookupGroup.FRT_BILL, LookupGroup.FRT_TYPE));
        Assert.assertNotNull(result);
        Assert.assertEquals(55, result.size());
    }
}