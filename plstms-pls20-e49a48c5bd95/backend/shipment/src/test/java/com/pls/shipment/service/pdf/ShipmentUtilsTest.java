package com.pls.shipment.service.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.shared.Status;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadVendorBillEntity;
import com.pls.shipment.service.ShipmentUtils;

/**
 *  Unit test case for ShipmentUtils.
 * 
 * @author Dmitriy Nefedchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentUtilsTest {
    private List<String> amCheckpoints = Arrays.asList("12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM",
            "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM");
    private List<String> pmCheckpoints = Arrays.asList("12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM",
            "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM");

    @Test
    public void testAmCheckpoints() {
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(0L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(100L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(200L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(300L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(400L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(500L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(600L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(700L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(800L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(900L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1000L)));
        assertTrue(amCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1100L)));
    }

    @Test
    public void testPmCheckpoints() {
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1200L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1300L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1400L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1500L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1600L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1700L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1800L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(1900L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(2000L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(2100L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(2200L)));
        assertTrue(pmCheckpoints.contains(ShipmentUtils.getGuaranteedTime(2300L)));
    }

    @Test
    public void testEndOfDayValue() {
        assertEquals("EOD", ShipmentUtils.getGuaranteedTime(2400L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCOnvertNegativeValue() {
        ShipmentUtils.getGuaranteedTime(-1L);
    }

    @Test
    public void testAmPmTime() {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 22);

        String amPmFormattedDate = ShipmentUtils.formatAmPm(time.getTime());

        Pattern pattern = Pattern.compile("\\d{1,2}:\\d{1,2}\\sA?P?M$");
        assertTrue(pattern.matcher(amPmFormattedDate).matches());
    }

    @Test
    public void testFrtBillDateWhenNotAvailableReqDocAndVendorBill() {
        LoadEntity load = getLoad();
        assertFalse(ShipmentUtils.isCanUpdateFrtBillDate(load));
        assertFalse(load.getVendorBillDetails() == null);
        assertTrue(load.isCustReqDocPresent());
        assertTrue(load.getVendorBillDetails().getFrtBillRecvFlag());
        assertFalse(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
    }

    private LoadEntity getLoad() {
        LoadEntity load = new LoadEntity();
        load.setId((long) (Math.random() * 100));
        LoadCostDetailsEntity cost = new LoadCostDetailsEntity();
        cost.setStatus(Status.ACTIVE);
        cost.setInvoiceNumber("invoiceNumber" + Math.random());
        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        load.setCustReqDocPresent(true);
        load.getCostDetails().add(cost);
        LoadVendorBillEntity vendorbil = new LoadVendorBillEntity();
        vendorbil.setFrtBillRecvFlag(true);
        load.setVendorBillDetails(vendorbil);
        CarrierInvoiceDetailsEntity carrierInvoiceDetails = new CarrierInvoiceDetailsEntity();
        List<CarrierInvoiceDetailsEntity> list = new ArrayList<CarrierInvoiceDetailsEntity>();
        list.add(carrierInvoiceDetails);
        load.getVendorBillDetails().setCarrierInvoiceDetails(list);
        return load;
    }
}
