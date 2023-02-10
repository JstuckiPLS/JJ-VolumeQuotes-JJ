package com.pls.shipment.dao.impl;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.bo.LostSavingsReportBO;
import com.pls.core.domain.bo.ReportsBO;
import com.pls.shipment.dao.ShipmentReportDao;
import com.pls.shipment.domain.bo.CreationReportBO;
import com.pls.shipment.domain.bo.FreightAnalysisReportBO;
import com.pls.shipment.domain.enums.DateTypeOption;

/**
 * Test cases for {@link ShipmentReportDaoImp}.
 * 
 * @author Brichak Aleksandr
 * 
 */
public class ShipmentReportDaoImplIT extends AbstractDaoTest {
    DateFormat format = new SimpleDateFormat("mm/dd/yyyy", Locale.US);

    private static final Long CUSTOMER_ID = 1L;

    private static final Long SALES_ORDER_LOAD_ID = 99999L;

    private static final Long EDI_VENDOR_LOAD_ID = 99988L;

    private static final Long MANUAL_VENDOR_LOAD_ID = 99987L;

    private static final Long ENTRY_QUOTE_LOAD_ID = 99986L;

    @Autowired
    private ShipmentReportDao sut;

    @Test
    public void shouldGetActivityReport() throws ParseException {
        Date endDate = format.parse("08/12/2015");
        Date startDate = format.parse("08/12/2012");
        List<FreightAnalysisReportBO> result = sut.getActivityReport(CUSTOMER_ID, null, startDate, endDate, DateTypeOption.GL);
        Assert.assertNotNull(result);
        Assert.assertEquals(19, result.size());
        FreightAnalysisReportBO report = result.iterator().next();
        Assert.assertNotNull(report.getAccessorials());
        Assert.assertEquals(1, report.getAccessorials().size());
        Assert.assertNotNull(report.getProducts());
        Assert.assertEquals(1, report.getProducts().size());
    }

    @Test
    public void shouldGetSavingsReport()  throws ParseException {
        Date endDate = format.parse("08/12/2015");
        Date startDate = format.parse("08/12/2012");
        List<FreightAnalysisReportBO> result = sut.getSavingsReport(CUSTOMER_ID, null, startDate, endDate);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetReportData() {
        Calendar calendar = DateUtility.getCalenderInstance();
        calendar.set(2008, 4, 9);
        List<ReportsBO> reportsBO = sut.getUnbilledReport(CUSTOMER_ID, null, calendar.getTime());
        Assert.assertNotNull(reportsBO);
        Assert.assertFalse(reportsBO.isEmpty());
        Assert.assertEquals(17, reportsBO.size());
    }

    @Test
    public void testGetLostSavingsReport() throws ParseException {
        Long customerId = 1L;
        Calendar startDate = DateUtility.getCalenderInstance();
        startDate.set(2008, 0, 20);
        Calendar endDate = DateUtility.getCalenderInstance();
        endDate.set(2012, 8, 28);
        String sortOrder = "UserName";
        List<LostSavingsReportBO> lsoReportBO = sut.getLostSavingsReport(customerId, null, startDate.getTime(),
                endDate.getTime(), sortOrder);
        Assert.assertNotNull(lsoReportBO);
        Assert.assertFalse(lsoReportBO.isEmpty());
        LostSavingsReportBO report = lsoReportBO.iterator().next();
        Assert.assertNotNull(report.getAccessorials());
        Assert.assertNotNull(report.getLostSavingsMaterials());
    }

    @Test
    public void testGetCreationReportForAll() throws ParseException {
        Calendar startDate = DateUtility.getCalenderInstance();
        startDate.set(2012, 11, 11);
        Calendar endDate = DateUtility.getCalenderInstance();
        endDate.set(2012, 11, 28);
        List<CreationReportBO> result = sut.getCreationReport(CUSTOMER_ID, null, startDate.getTime(), endDate.getTime(), false);
        CreationReportBO report = result.get(0);
        Assert.assertEquals(148, result.size());
        Assert.assertTrue(report.getCreatedDate().after(startDate.getTime()) && report.getCreatedDate().before(endDate.getTime()));
        Assert.assertNotNull(report.getCreationMethod());
    }

    @Test
    public void testGetCreationReportForInvoiced() throws ParseException {
        Calendar startDate = DateUtility.getCalenderInstance();
        startDate.set(2012, 11, 11);
        Calendar endDate = DateUtility.getCalenderInstance();
        endDate.set(2012, 11, 28);
        List<CreationReportBO> result = sut.getCreationReport(CUSTOMER_ID, null, startDate.getTime(), endDate.getTime(), true);
        Assert.assertEquals(9, result.size());
        for (CreationReportBO report : result) {
            Assert.assertTrue(report.getCreatedDate().after(startDate.getTime()) && report.getCreatedDate().before(endDate.getTime()));
            Assert.assertNotNull(report.getCreationMethod());
            Assert.assertNotNull(report.getInvoiceNumber());
        }
    }

    @Test
    public void testCreationMethodValue() {
        executeScript("creationReportData.sql");
        Calendar startDate = DateUtility.getCalenderInstance();
        startDate.set(2015, 8, 10);
        Calendar endDate = DateUtility.getCalenderInstance();
        endDate.set(2015, 12, 30);
        List<CreationReportBO> result = sut.getCreationReport(CUSTOMER_ID, null, startDate.getTime(), endDate.getTime(), false);
        Assert.assertEquals(4, result.size());
        for (CreationReportBO report : result) {
            switch (report.getCreationMethod()) {
            case "Sales Order":
                Assert.assertEquals(SALES_ORDER_LOAD_ID, report.getLoadId());
                break;
            case "EDI Vendor Bill":
                Assert.assertEquals(EDI_VENDOR_LOAD_ID, report.getLoadId());
                break;
            case "Manual Vendor Bill":
                Assert.assertEquals(MANUAL_VENDOR_LOAD_ID, report.getLoadId());
                break;
            case "Quote/Shipment Entry":
                Assert.assertEquals(ENTRY_QUOTE_LOAD_ID, report.getLoadId());
                break;
            default:
                fail();
                break;
            }
            Assert.assertTrue(report.getCreatedDate().after(startDate.getTime()) && report.getCreatedDate().before(endDate.getTime()));
            Assert.assertNotNull(report.getCreationMethod());
        }
    }
}
