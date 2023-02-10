package com.pls.shipment.dao.impl;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.CarrierEdiCostTypesDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link CarrierEdiCostTypesDao}.
 *
 * @author Alexander Kirichenko
 */
public class CarrierEdiCostTypesDaoImplIT extends AbstractDaoTest {

    public static final long NOT_EXISTING_CARRIER_ID = 1L;
    public static final long CARRIER_ID = 13L;
    public static final String MISC = "MS";
    public static final String CDF = "CDF";
    public static final String DF = "DF";
    @Autowired
    private CarrierEdiCostTypesDao sut;

    @Test
    public void testGetAccessorialRefTypeByCarrierEdiCostType() {
        String accessorialRefType = sut.getAccessorialRefTypeByCarrierEdiCostType(NOT_EXISTING_CARRIER_ID, "PC");
        assertNotNull(accessorialRefType);
        assertEquals(MISC, accessorialRefType);

        accessorialRefType = sut.getAccessorialRefTypeByCarrierEdiCostType(CARRIER_ID, CDF);
        assertNotNull(accessorialRefType);
        assertEquals(DF, accessorialRefType);

        accessorialRefType = sut.getAccessorialRefTypeByCarrierEdiCostType(NOT_EXISTING_CARRIER_ID, null);
        assertNotNull(accessorialRefType);
        assertEquals(MISC, accessorialRefType);
    }

    @Test
    public void testGetRefTypeMapForCarrier() {
        Map<String, String> refTypeMapForCarrier = sut.getRefTypeMapForCarrier(CARRIER_ID);
        assertNotNull(refTypeMapForCarrier);
        assertFalse(refTypeMapForCarrier.isEmpty());
        assertEquals(2, refTypeMapForCarrier.size());
        assertTrue(refTypeMapForCarrier.containsKey(CDF));
        assertEquals(DF, refTypeMapForCarrier.get(CDF));

        refTypeMapForCarrier = sut.getRefTypeMapForCarrier(NOT_EXISTING_CARRIER_ID);
        assertNotNull(refTypeMapForCarrier);
        assertTrue(refTypeMapForCarrier.isEmpty());
    }
}
