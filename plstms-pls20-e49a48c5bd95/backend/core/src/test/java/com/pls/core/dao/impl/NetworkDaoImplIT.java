package com.pls.core.dao.impl;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.NetworkDao;
import com.pls.core.domain.bo.NetworkListItemBO;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;

/**
 * IT for {@link com.pls.core.dao.impl.NetworkDaoImpl} .
 * 
 * @author Brichak Aleksandr
 */
public class NetworkDaoImplIT extends AbstractDaoTest {

    @Autowired
    private NetworkDao dao;

    @Test
    public void getAllNetworks() throws Exception {
        List<NetworkListItemBO> networks = dao.getAllNetworks();
        Assert.assertNotNull(networks);
        Assert.assertEquals(14, networks.size());
    }

    @Test
    public void testGetUnitAndCostCenterCodes() {
        UnitAndCostCenterCodesBO result = dao.getUnitAndCostCenterCodes(1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getUnitCode(), "LT");
        Assert.assertEquals(result.getCostCenterCode(), "Pitt1");

        result = dao.getUnitAndCostCenterCodes(3L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getUnitCode(), "N2");
        Assert.assertEquals(result.getCostCenterCode(), "Pitt1");
    }
}
