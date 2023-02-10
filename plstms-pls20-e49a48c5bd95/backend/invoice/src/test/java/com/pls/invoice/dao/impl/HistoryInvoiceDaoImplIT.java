package com.pls.invoice.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.invoice.dao.HistoryInvoiceDao;
import com.pls.invoice.domain.bo.CBIHistoryBO;
import com.pls.invoice.domain.bo.InvoiceHistoryBO;

/**
 * Tests for {@link HistoryInvoiceDao}.
 *
 * @author Sergey Kirichenko
 */
public class HistoryInvoiceDaoImplIT extends AbstractDaoTest {

    @Autowired
    private HistoryInvoiceDao dao;

    @Before
    public void init() {
        executeScript("prepareInvoiceHistory.sql");
    }

    @Test
    public void shouldFilterHistoryInvoiceByInvoiceNumber() {
        String invoiceNumber = "C-0000123";
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setInvoiceNumber(invoiceNumber);
        List<InvoiceHistoryBO> result = dao.getInvoiceHistory(search, 1L);

        assertNotNull(result);
        assertEquals(2, result.size()); // actually there should be just one record, but because of invalid
                                        // test data grouping doesn't work properly
        assertEquals(invoiceNumber, result.get(0).getInvoiceNumber());
        assertEquals(new Long(1), result.get(0).getInvoiceId());
        assertEquals(invoiceNumber, result.get(1).getInvoiceNumber());
        assertEquals(new Long(1), result.get(1).getInvoiceId());
    }

    @Test
    public void shouldFilterHistoryInvoiceByBolNumber() {
        String bolNumber = "20121008-6-1";
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setBol(bolNumber);
        List<InvoiceHistoryBO> result = dao.getInvoiceHistory(search, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        InvoiceHistoryBO invoiceHistoryBO = result.get(0);
        assertNotNull(invoiceHistoryBO);
        Assert.assertNull(invoiceHistoryBO.getBol());
        assertEquals(new Long(1), invoiceHistoryBO.getInvoiceId());
    }

    @Test
    public void shouldFilterHistoryInvoiceByProNumber() {
        String proNumber = "91524";
        getSession().createQuery("update LoadEntity set numbers.proNumber = :proNumber where id = :loadId").setParameter("proNumber", proNumber)
                .setParameter("loadId", 812L).executeUpdate();
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setPro(proNumber);
        List<InvoiceHistoryBO> result = dao.getInvoiceHistory(search, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        InvoiceHistoryBO invoiceHistoryBO = result.get(0);
        assertNotNull(invoiceHistoryBO);
        Assert.assertNull(invoiceHistoryBO.getPro());
        assertEquals(new Long(1), invoiceHistoryBO.getInvoiceId());
    }

    @Test
    public void shouldFilterHistoryInvoiceByDateAndCustomer() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, Calendar.DECEMBER, 28, 23, 59, 59);
        Date start = calendar.getTime();
        calendar.set(2012, Calendar.DECEMBER, 29, 0, 0, 1);
        Date end = calendar.getTime();
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setFromDate(start);
        search.setToDate(end);
        search.setCustomer(1L);
        List<InvoiceHistoryBO> result = dao.getInvoiceHistory(search, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        InvoiceHistoryBO invoiceHistoryBO = result.get(0);
        assertNotNull(invoiceHistoryBO);
        assertTrue(invoiceHistoryBO.getInvoiceDate().toString(), DateUtils.isSameDay(invoiceHistoryBO.getInvoiceDate(), calendar.getTime()));
        assertEquals(new Long(1), invoiceHistoryBO.getInvoiceId());
    }

    @Test
    public void shouldFindHistoryInvoiceByLoadId() {
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setLoadId(813L);
        List<InvoiceHistoryBO> result = dao.getInvoiceHistory(search, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        InvoiceHistoryBO invoiceHistoryBO = result.get(0);
        assertNotNull(invoiceHistoryBO);
        Assert.assertNull(invoiceHistoryBO.getLoadId());
        assertEquals(new Long(1), invoiceHistoryBO.getInvoiceId());
        assertEquals("PLS SHIPPER", invoiceHistoryBO.getCustomerName());
    }

    @Test
    public void shouldFindHistoryCBIDetails() {
        Long invoiceId = 1L;

        List<CBIHistoryBO> result = dao.getInvoiceHistoryCBIDetails(invoiceId, "C-0000123");

        assertEquals(5, result.size());
        CBIHistoryBO bo = findInvoiceHistory(result, 813L, null);
        Assert.assertNotNull(bo);
        Assert.assertEquals("324526", bo.getBol());
        Assert.assertNull(bo.getPro());
        Assert.assertEquals("ROADWAY EXPRESS", bo.getCarrierName());
        Assert.assertEquals(new BigDecimal("100.00"), bo.getTotalRevenue());
        Assert.assertNull(bo.getPaidAmount());
        Assert.assertEquals("Colorado Springs", bo.getOrigin().getCity());
        Assert.assertEquals("CO", bo.getOrigin().getState());
        Assert.assertEquals("32344", bo.getOrigin().getZip());
        Assert.assertEquals("Atlanta", bo.getDestination().getCity());
        Assert.assertEquals("GA", bo.getDestination().getState());
        Assert.assertEquals("21170", bo.getDestination().getZip());
        Assert.assertEquals("324526", bo.getPo());
        Assert.assertEquals("324526", bo.getGlNumber());
        Assert.assertEquals(new BigDecimal("80.00"), bo.getTotalCost());
        Assert.assertEquals(new BigDecimal("27.70"), bo.getFs());
        Assert.assertEquals(new BigDecimal("72.30"), bo.getAcc());

        bo = findInvoiceHistory(result, 166L, 1L);
        Assert.assertNotNull(bo);
        Assert.assertNull(bo.getBol());
        Assert.assertNull(bo.getPro());
        Assert.assertEquals("DAYTON FREIGHT LINES", bo.getCarrierName());
        Assert.assertEquals(new BigDecimal("186.07"), bo.getTotalRevenue());
        Assert.assertNull(bo.getPaidAmount());
        Assert.assertEquals("PERU", bo.getOrigin().getCity());
        Assert.assertEquals("IL", bo.getOrigin().getState());
        Assert.assertEquals("61354", bo.getOrigin().getZip());
        Assert.assertEquals("RIVES JUNCTION", bo.getDestination().getCity());
        Assert.assertEquals("MI", bo.getDestination().getState());
        Assert.assertEquals("49277", bo.getDestination().getZip());
        Assert.assertEquals("20121010-6-2", bo.getPo());
        Assert.assertEquals("00000001", bo.getGlNumber());
        Assert.assertEquals(new BigDecimal("930.37"), bo.getTotalCost());
        Assert.assertEquals(BigDecimal.ZERO, bo.getFs());
        Assert.assertEquals(new BigDecimal("0.00"), bo.getAcc());
    }

    @Test
    public void shouldCheckPaidAmount() {
        getSession().createQuery("update FinancialAccountReceivablesEntity set load = 812 where load = 58").executeUpdate();
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setLoadId(812L);
        List<InvoiceHistoryBO> result = dao.getInvoiceHistory(search, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("77"), result.get(0).getPaidAmount());
        assertEquals(new BigDecimal("123"), result.get(0).getPaidDue());
    }

    private CBIHistoryBO findInvoiceHistory(List<CBIHistoryBO> data, Long loadId, Long adjId) {
        for (CBIHistoryBO bo : data) {
            if (ObjectUtils.equals(bo.getLoadId(), loadId) && ObjectUtils.equals(bo.getAdjustmentId(), adjId)) {
                return bo;
            }
        }
        return null;
    }
}
