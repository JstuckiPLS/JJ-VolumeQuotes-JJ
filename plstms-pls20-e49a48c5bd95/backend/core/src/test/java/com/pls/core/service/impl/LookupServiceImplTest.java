package com.pls.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.impl.LookupValueDaoImpl;
import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.enums.LookupGroup;

/**
 * Test cases for {@link com.pls.core.service.impl.LookupServiceImpl} class.
 *
 * @author Sergey Vovchuk
 */
@RunWith(MockitoJUnitRunner.class)
public class LookupServiceImplTest {

    @InjectMocks
    private LookupServiceImpl sut;

    @Mock
    private LookupValueDaoImpl dao;

    @Test
    public void testGetGLNumberComponents() {
        List<LookupGroup> lookupValues = Arrays.asList(LookupGroup.CMP_NUM, LookupGroup.BRN_NUM, LookupGroup.SBR_NUM,
                LookupGroup.DIV_NUM);
        List<LookupValueEntity> expected = new ArrayList<LookupValueEntity>();
        expected.add(getNewBo());
        expected.add(getNewBo());
        expected.add(getNewBo());

        Mockito.when(dao.findLookupValuesByGroup(lookupValues)).thenReturn(expected);

        List<LookupValueEntity> actual = sut.getGLNumberComponents();
        Mockito.verify(dao, Mockito.times(1)).findLookupValuesByGroup(lookupValues);

        Assert.assertEquals(actual.size(), expected.size());
        Assert.assertEquals(actual.get(0).getDescription(), expected.get(0).getDescription());
        Assert.assertEquals(actual.get(0).getLookupGroup(), expected.get(0).getLookupGroup());
    }

    private LookupValueEntity getNewBo() {
        LookupValueEntity bo = new LookupValueEntity();
  //      bo.setLookupGroup("group" + Math.random());
        bo.setDescription("desc" + Math.random());
        return bo;
    }
}

