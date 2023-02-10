package com.pls.invoice.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.Currency;
import com.pls.invoice.dao.FinancialBoardDao;
import com.pls.invoice.domain.bo.ConsolidatedInvoiceBO;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.enums.ApprovedStatus;
import com.pls.shipment.domain.bo.ZipBO;

/**
 * Tests for {@link FinancialBoardDao}.
 *
 * @author Sergey Kirichenko
 */
public class FinBoardDaoImplIT extends AbstractDaoTest {
    private static final Long USER_ID = 1L;
    private static final Long BILL_TO_ID = 21L;

    @Autowired
    private FinancialBoardDao dao;

    @Test
    public void shouldFindConsolidatedInvoiceData() {
        List<ConsolidatedInvoiceBO> result = dao.getConsolidatedInvoiceData(USER_ID);
        Collections.sort(result, new Comparator<ConsolidatedInvoiceBO>() {
            @Override
            public int compare(ConsolidatedInvoiceBO o1, ConsolidatedInvoiceBO o2) {
                String names1 = o1.getCustomerName() + ":" + o1.getBillToName();
                String names2 = o2.getCustomerName() + ":" + o2.getBillToName();
                return names1.compareTo(names2);
            }
        });
        assertNotNull(result);
        assertEquals(2, result.size());
        //First and second records for PLS SHIPPER customer and Northern branch bill to
        checkNothernBranchInvoice(result.get(0));
        //Third and forth records for PLS SHIPPER customer and Southern branch bill to
        checkSouthernBranchInvoice(result.get(1));
    }

    @Test
    public void shouldFindConsolidatedInvoiceDataWithActiveOrInActiveOrganization() {
        updateOrganizationStatus(1L, true);
        List<ConsolidatedInvoiceBO> result = dao.getConsolidatedInvoiceData(USER_ID);
        assertNotNull(result);
        assertEquals(2, result.size());
        updateOrganizationStatus(1L, false);
        result = dao.getConsolidatedInvoiceData(USER_ID);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    private void checkSouthernBranchInvoice(ConsolidatedInvoiceBO bo) {
        assertNotNull(bo);
        assertEquals("PLS SHIPPER", bo.getCustomerName());
        assertEquals("Southern branch", bo.getBillToName());
        assertEquals("Thursday @ 05:00 PM EST", bo.getInvoiceDateInfo());
        assertFalse(bo.isIncludeCarrierRate());
        assertEquals(bo.getSendBy().length(), 0);
        assertEquals(Currency.USD, bo.getCurrency());
        assertEquals(ApprovedStatus.NONE, bo.getApproved());
    }

    private void checkNothernBranchInvoice(ConsolidatedInvoiceBO bo) {
        assertNotNull(bo);
        assertEquals("PLS SHIPPER", bo.getCustomerName());
        assertEquals("Northern branch", bo.getBillToName());
        assertEquals("Daily @ 11:00 AM EST", bo.getInvoiceDateInfo());
        assertFalse(bo.isIncludeCarrierRate());
        assertEquals(bo.getSendBy().length(), 0);
        assertEquals(Currency.USD, bo.getCurrency());
        assertEquals(ApprovedStatus.NONE, bo.getApproved());
    }

    @Test
    public void shouldNotFindConsolidatedInvoiceDataForWrongUser() {
        List<ConsolidatedInvoiceBO> result = dao.getConsolidatedInvoiceData(0L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindConsolidatedLoads() throws Exception {
        List<InvoiceBO> result = dao.getConsolidatedLoads(USER_ID, BILL_TO_ID);
        assertNotNull(result);
        assertEquals(4, result.size());
        MathContext mc = new MathContext(0, RoundingMode.HALF_UP);
        InvoiceBO bo = result.get(0);
        assertNotNull(bo);
        assertEquals(new Long(719), bo.getLoadId());
        assertNull(bo.getAdjustmentId());
        assertEquals("ROADWAY EXPRESS", bo.getCarrierName());
        assertEquals("01-1", bo.getBolNumber());
        assertEquals("01-1", bo.getPoNumber());
        assertEquals("01-1", bo.getGlNumber());
        assertEquals(new BigDecimal("304.58", mc), bo.getRevenue());
        assertEquals(new BigDecimal("276.89", mc), bo.getCost());
        assertEquals(new BigDecimal("27.69", mc), bo.getMarginAmt());
        assertEquals(new BigDecimal("9.09", mc), bo.getMargin());
        assertEquals(new BigDecimal("0", mc), bo.getPaidAmount());
        assertEquals(new BigDecimal("0.00", mc), bo.getAcc());
        assertEquals(new BigDecimal("58.95", mc), bo.getFs());
        checkAddress(bo.getOrigin(), "STRONGSVILLE", "OH", "44136");
        checkAddress(bo.getDestination(), "DETROIT", "MI", "48211");
        assertEquals(Currency.USD, bo.getCurrency());
        assertFalse(bo.getApproved());
        assertEquals(BILL_TO_ID, bo.getBillToId());
        assertEquals("PLS SHIPPER", bo.getCustomerName());
        assertEquals("Southern branch", bo.getBillToName());
        assertTrue("actual date was " + bo.getDeliveredDate(),
                DateUtils.isSameDay(new SimpleDateFormat("MM/dd/yyyy").parse("12/16/2013"), bo.getDeliveredDate()));
        assertNull(bo.getProNumber());
        assertEquals("LTL", bo.getNetworkName());
        assertFalse(bo.getDoNotInvoice());
        assertFalse(bo.getMissingPaymentsTerms());
        bo = result.get(1);
        assertNotNull(bo);
        assertEquals(new Long(720), bo.getLoadId());
        assertNull(bo.getAdjustmentId());
        assertEquals("ROADWAY EXPRESS", bo.getCarrierName());
        assertEquals("01-2", bo.getBolNumber());
        assertEquals("01-2", bo.getPoNumber());
        assertEquals("01-2", bo.getGlNumber());
        assertEquals(new BigDecimal("304.58", mc), bo.getRevenue());
        assertEquals(new BigDecimal("276.89", mc), bo.getCost());
        assertEquals(new BigDecimal("27.69", mc), bo.getMarginAmt());
        assertEquals(new BigDecimal("9.09", mc), bo.getMargin());
        assertEquals(new BigDecimal("0", mc), bo.getPaidAmount());
        assertEquals(new BigDecimal("0.00", mc), bo.getAcc());
        assertEquals(new BigDecimal("58.95", mc), bo.getFs());
        checkAddress(bo.getOrigin(), "STRONGSVILLE", "OH", "44136");
        checkAddress(bo.getDestination(), "DETROIT", "MI", "48211");
        assertEquals(Currency.USD, bo.getCurrency());
        assertFalse(bo.getApproved());
        assertEquals(BILL_TO_ID, bo.getBillToId());
        assertEquals("PLS SHIPPER", bo.getCustomerName());
        assertEquals("Southern branch", bo.getBillToName());
        assertTrue("actual date was " + bo.getDeliveredDate(),
                DateUtils.isSameDay(new SimpleDateFormat("MM/dd/yyyy").parse("12/16/2013"), bo.getDeliveredDate()));
        assertEquals("1108540566", bo.getProNumber());
        assertEquals("LTL", bo.getNetworkName());
        assertFalse(bo.getDoNotInvoice());
        assertFalse(bo.getMissingPaymentsTerms());
        bo = result.get(2);
        assertNotNull(bo);
        assertEquals(new Long(723), bo.getLoadId());
        assertEquals(new Long(11), bo.getAdjustmentId());
        assertEquals("ROADWAY EXPRESS", bo.getCarrierName());
        assertEquals("01-1", bo.getBolNumber());
        assertEquals("01-1", bo.getPoNumber());
        assertEquals("01-1", bo.getGlNumber());
        assertEquals(new BigDecimal("35.10", mc), bo.getRevenue());
        assertEquals(new BigDecimal("27.85", mc), bo.getCost());
        assertEquals(new BigDecimal("7.25", mc), bo.getMarginAmt());
        assertEquals(new BigDecimal("20.66", mc), bo.getMargin());
        assertEquals(new BigDecimal("10.50", mc), bo.getPaidAmount());
        assertEquals(new BigDecimal("0.00", mc), bo.getAcc());
        assertEquals(new BigDecimal("35.10", mc), bo.getFs());
        checkAddress(bo.getOrigin(), "STRONGSVILLE", "OH", "44136");
        checkAddress(bo.getDestination(), "DETROIT", "MI", "48211");
        assertEquals(Currency.USD, bo.getCurrency());
        assertFalse(bo.getApproved());
        assertEquals(BILL_TO_ID, bo.getBillToId());
        assertEquals("PLS SHIPPER", bo.getCustomerName());
        assertEquals("Southern branch", bo.getBillToName());
        assertTrue("actual date was " + bo.getDeliveredDate(),
                DateUtils.isSameDay(new SimpleDateFormat("MM/dd/yyyy").parse("12/16/2013"), bo.getDeliveredDate()));
        assertNull(bo.getProNumber());
        assertEquals("LTL", bo.getNetworkName());
        assertFalse(bo.getDoNotInvoice());
        assertFalse(bo.getMissingPaymentsTerms());
        bo = result.get(3);
        assertNotNull(bo);
        assertEquals(new Long(725), bo.getLoadId());
        assertEquals(new Long(16), bo.getAdjustmentId());
        assertEquals("FEDEX FREIGHT PRIORITY", bo.getCarrierName());
        assertEquals("20121007-6-1", bo.getBolNumber());
        assertEquals("20121007-6-2", bo.getPoNumber());
        assertEquals("00000001", bo.getGlNumber());
        assertEquals(new BigDecimal("30.47", mc), bo.getRevenue());
        assertEquals(new BigDecimal("25.12", mc), bo.getCost());
        assertEquals(new BigDecimal("5.35", mc), bo.getMarginAmt());
        assertEquals(new BigDecimal("17.56", mc), bo.getMargin());
        assertEquals(new BigDecimal("0", mc), bo.getPaidAmount());
        assertEquals(new BigDecimal("0.00", mc), bo.getAcc());
        assertEquals(new BigDecimal("0", mc), bo.getFs());
        checkAddress(bo.getOrigin(), "CLARKS SUMMIT", "PA", "18411");
        checkAddress(bo.getDestination(), "ADAIRSVILLE", "GA", "30103");
        assertEquals(Currency.USD, bo.getCurrency());
        assertFalse(bo.getApproved());
        assertEquals(BILL_TO_ID, bo.getBillToId());
        assertEquals("PLS SHIPPER", bo.getCustomerName());
        assertEquals("Southern branch", bo.getBillToName());
        assertTrue("actual date was " + bo.getDeliveredDate(),
                DateUtils.isSameDay(new SimpleDateFormat("MM/dd/yyyy").parse("10/11/2012"), bo.getDeliveredDate()));
        assertNull(bo.getProNumber());
        assertEquals("LTL", bo.getNetworkName());
        assertFalse(bo.getDoNotInvoice());
        assertFalse(bo.getMissingPaymentsTerms());
    }

    private void checkAddress(ZipBO zipBo, String city, String state, String zip) {
        assertEquals(city, zipBo.getCity());
        assertEquals(state, zipBo.getState());
        assertEquals(zip, zipBo.getZip());
    }

    @Test
    public void shouldNotFindConsolidatedLoadsForWrongBillTo() {
        List<InvoiceBO> result = dao.getConsolidatedLoads(USER_ID, 0L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
