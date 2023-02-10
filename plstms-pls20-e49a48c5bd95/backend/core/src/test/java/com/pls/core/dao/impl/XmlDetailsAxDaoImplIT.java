/**
 * 
 */
package com.pls.core.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.XmlDetailsAxDao;
import com.pls.core.domain.bo.XmlDetailsAxBO;
import com.pls.core.domain.enums.Currency;

/**
 * Test cases for {@link XmlDetailsAxDaoImpl} class.
 * 
 * @author Alexander Nalapko
 * 
 */
public class XmlDetailsAxDaoImplIT extends AbstractDaoTest {

    @Autowired
    private XmlDetailsAxDao dao;

    @Test
    public void getDetails() {
        XmlDetailsAxBO bo = dao.getDetails(1L);
        assertNotNull(bo);
        assertEquals(bo.getAccountNum(), "LT-1--1");
        assertEquals(bo.getAddress1(), "Inverness 2");
        assertEquals(bo.getAddress2(), null);
        assertEquals(bo.getAddressId(), Long.valueOf(100501));
        assertEquals(bo.getBillToId(), Long.valueOf(1));
        assertEquals(bo.getBillToName(), "Haynes International, INC.");
        assertEquals(bo.getCity(), "Denver");
        assertEquals(bo.getCompanyCode(), "LT");
        assertEquals(bo.getCostCenterCode(), "Pitt1");
        assertEquals(bo.getCountryCode(), "USA");
        assertEquals(bo.getCurrency(), Currency.USD);
        assertEquals(bo.getEmployerNum(), "20-9548043");
        assertEquals(bo.getGroupCode(), "LT");
        assertEquals(bo.getOrgId(), Long.valueOf(1));
        assertEquals(bo.getOrgName(), "PLS SHIPPER");
        assertEquals(bo.getPostalCode(), "62344");
        assertEquals(bo.getStateCode(), "CO");
        assertEquals(bo.getUnitCode(), "LT");
    }
}
