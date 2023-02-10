package com.pls.core.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.bo.CarrierInfoBO;
import com.pls.core.domain.bo.SimpleCarrierInfoBO;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;

/**
 * Test cases for {@link CarrierDaoImplTestIT} class.
 * 
 * @author Alexander Nalapko
 * 
 */
public class CarrierDaoImplTestIT extends AbstractDaoTest {

    private static final String SEARCH_CARRIER = "A";
    private static final String CARRIER_NAME = "AKS";
    private static final String CARRIER_SCAC = "AVRT";
    private static final String CARRIER_MC = "362427";
    private static final String NON_EXISTING = "XXXXX";

    @Before
    public void setUp() {
        SecurityTestUtils.login("USER");
    }

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Autowired
    private CarrierDao dao;

    @Test
    public void testFindCarrierInfoByName() throws Exception {
        List<CarrierInfoBO> list = dao.findCarrierByName(SEARCH_CARRIER, 10, 0);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertFalse(list.get(0).getName().equals(CARRIER_NAME));
    }

    @Test
    public void testFindByScac() {
        CarrierEntity carrier = dao.findByScac(NON_EXISTING);
        assertNull(carrier);

        carrier = dao.findByScac(CARRIER_SCAC);
        assertNotNull(carrier);

        carrier = dao.findByScac(CARRIER_SCAC.toLowerCase());
        assertNotNull(carrier);
    }

    @Test
    public void testFindByScacAndMC() {
        CarrierEntity carrier = dao.findByScacAndMC(NON_EXISTING, "");
        assertNull(carrier);

        carrier = dao.findByScacAndMC(CARRIER_SCAC, "");
        assertNotNull(carrier);

        carrier = dao.findByScacAndMC(CARRIER_SCAC, NON_EXISTING);
        assertNull(carrier);

        carrier = dao.findByScacAndMC(CARRIER_SCAC, CARRIER_MC);
        assertNotNull(carrier);

        carrier = dao.findByScacAndMC(CARRIER_SCAC.toLowerCase(), CARRIER_MC.toLowerCase());
        assertNotNull(carrier);
    }

    @Test
    public void testFindByMc() {
        CarrierEntity carrier = dao.findByMcNumber(NON_EXISTING);
        assertNull(carrier);

        carrier = dao.findByMcNumber(CARRIER_MC);
        assertNotNull(carrier);

        carrier = dao.findByMcNumber(CARRIER_MC.toLowerCase());
        assertNotNull(carrier);
    }

    @Test
    public void testSaveCarrier() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setName("name");
        carrier.setMcNumber("000");
        carrier.setScac("SCAC");
        carrier.setStatus(OrganizationStatus.ACTIVE);

        dao.saveOrUpdate(carrier);

        flushAndClearSession();
    }

    @Test
    public void testGetCarrierInfos() {
        List<SimpleCarrierInfoBO> list = dao.getCarrierInfos(null, "AVRT", null);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    public void testFindById() {
        CarrierEntity carrier = dao.find(6L);
        assertNotNull(carrier);
        assertNotNull(carrier.getOrgServiceEntity());
        assertEquals(carrier.getId(), carrier.getOrgServiceEntity().getOrgId());
        assertNotNull(carrier.getRejectedCustomers());
        assertFalse(carrier.getRejectedCustomers().isEmpty());
    }

    @Test
    public void shouldLoadDefaultCarrier() {
        CarrierInfoBO defaultCarrier = dao.getDefaultCarrier();
        assertNotNull(defaultCarrier);
    }

    @Test
    public void shouldRejectEDIForCustomer() {
        Boolean result = dao.rejectEdiForCustomer("ALUK", "BOB BAKER");
        assertTrue(result);
    }

    @Test
    public void shouldNotRejectEDIForCustomer() {
        Boolean result = dao.rejectEdiForCustomer("ALUK", "6118686148");
        assertFalse(result);
    }
}
