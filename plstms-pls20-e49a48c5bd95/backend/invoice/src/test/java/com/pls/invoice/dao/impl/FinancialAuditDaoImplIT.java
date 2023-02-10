package com.pls.invoice.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.invoice.dao.FinancialAuditDao;
import com.pls.invoice.domain.bo.AuditBO;
import com.pls.shipment.domain.FinancialAccessorialsEntity;

/**
 * Tests for {@link FinancialAuditDao}.
 *
 * @author Sergey Kirichenko
 */
public class FinancialAuditDaoImplIT extends AbstractDaoTest {
    private static final Long USER_ID = 1L;

    @Autowired
    private FinancialAuditDao dao;

    @Test
    public void shouldGetInvoiceAuditDataForUser() throws Exception {
        executeScript("prepareInvoiceAudit.sql");

        List<AuditBO> result = dao.getInvoiceAuditData(USER_ID);
        Assert.assertEquals(5, result.size());
        Collections.sort(result, new Comparator<AuditBO>() {
            @Override
            public int compare(AuditBO o1, AuditBO o2) {
                return o1.getLoadId().compareTo(o2.getLoadId());
            }
        });

        Assert.assertEquals(new Long(56), result.get(0).getLoadId());
        Assert.assertEquals(new Long(3), result.get(0).getAdjustmentId());
        Assert.assertEquals("00000054", result.get(0).getBol());
        Assert.assertEquals("00000054", result.get(0).getPo());
        Assert.assertEquals("00000054", result.get(0).getPro());
        Assert.assertEquals(new BigDecimal("121.50"), result.get(0).getRevenue());
        Assert.assertEquals(new BigDecimal("110.90"), result.get(0).getCost());
        Assert.assertEquals(new BigDecimal("8.72"), result.get(0).getMargin());
        Assert.assertEquals(new BigDecimal("8.11"), result.get(0).getVendorBillAmount());
        Assert.assertTrue(result.get(0).getDeliveryDate().toString(),
                DateUtils.isSameDay(DateUtils.parseDate("2010-01-08", "yyyy-MM-dd"), result.get(0).getDeliveryDate()));
        Assert.assertEquals("Mail", result.get(0).getCarrierName());
        Assert.assertEquals("PLS SHIPPER", result.get(0).getCustomerName());
        Assert.assertEquals("LTL", result.get(0).getNetworkName());
        Assert.assertEquals("admin sysadmin", result.get(0).getAccExecName());

        Assert.assertEquals(new Long(59), result.get(1).getLoadId());
        Assert.assertEquals(new Long(6), result.get(1).getAdjustmentId());
        Assert.assertEquals("00000057", result.get(1).getBol());
        Assert.assertEquals("00000057", result.get(1).getPo());
        Assert.assertEquals("00000057", result.get(1).getPro());
        Assert.assertEquals(new BigDecimal("324.50"), result.get(1).getRevenue());
        Assert.assertEquals(new BigDecimal("230.23"), result.get(1).getCost());
        Assert.assertEquals(new BigDecimal("29.05"), result.get(1).getMargin());
        Assert.assertEquals(new BigDecimal("8.12"), result.get(1).getVendorBillAmount());
        Assert.assertTrue(result.get(1).getDeliveryDate().toString(),
                DateUtils.isSameDay(DateUtils.parseDate("2010-01-12", "yyyy-MM-dd"), result.get(1).getDeliveryDate()));
        Assert.assertEquals("Missing Document", result.get(1).getReason());
        Assert.assertEquals("Mail", result.get(1).getCarrierName());
        Assert.assertEquals("PLS SHIPPER", result.get(1).getCustomerName());
        Assert.assertEquals("LTL", result.get(1).getNetworkName());
        Assert.assertEquals("admin sysadmin", result.get(1).getAccExecName());

        Assert.assertEquals(new Long(60), result.get(2).getLoadId());
        Assert.assertEquals(new Long(7), result.get(2).getAdjustmentId());
        Assert.assertEquals("00000058", result.get(2).getBol());
        Assert.assertEquals("00000058", result.get(2).getPo());
        Assert.assertEquals("00000058", result.get(2).getPro());
        Assert.assertEquals(new BigDecimal("121.23"), result.get(2).getRevenue());
        Assert.assertEquals(new BigDecimal("90.34"), result.get(2).getCost());
        Assert.assertEquals(new BigDecimal("25.48"), result.get(2).getMargin());
        Assert.assertNull(result.get(2).getVendorBillAmount());
        Assert.assertTrue(result.get(2).getDeliveryDate().toString(),
                DateUtils.isSameDay(
                        DateUtils.parseDate("2008-04-11", "yyyy-MM-dd"),
                        result.get(2).getDeliveryDate()));
        Assert.assertNull(result.get(2).getReason());
        Assert.assertEquals("Mail", result.get(2).getCarrierName());
        Assert.assertEquals("PLS SHIPPER", result.get(2).getCustomerName());
        Assert.assertEquals("LTL", result.get(2).getNetworkName());
        Assert.assertEquals("admin sysadmin", result.get(2).getAccExecName());

        Assert.assertEquals(new Long(560), result.get(3).getLoadId());
        Assert.assertNull(result.get(3).getAdjustmentId());
        Assert.assertEquals("20121006-15-1", result.get(3).getBol());
        Assert.assertEquals("20121006-15-2", result.get(3).getPo());
        Assert.assertNull(result.get(3).getPro());
        Assert.assertEquals(new BigDecimal("772.29"), result.get(3).getRevenue());
        Assert.assertEquals(new BigDecimal("154.46"), result.get(3).getCost());
        Assert.assertEquals(new BigDecimal("80.00"), result.get(3).getMargin());
        Assert.assertEquals(new BigDecimal("10.35"), result.get(3).getVendorBillAmount());
        Assert.assertTrue(result.get(3).getDeliveryDate().toString(),
                DateUtils.isSameDay(
                        DateUtils.parseDate("2012-10-10", "yyyy-MM-dd"),
                        result.get(3).getDeliveryDate()));
        // Assert.assertEquals("Missing Document, Cost Diff",
        // result.get(3).getReason());
        Assert.assertEquals("ESTES EXPRESS LINES", result.get(3).getCarrierName());
        Assert.assertEquals("PLS SHIPPER", result.get(3).getCustomerName());
        Assert.assertEquals("LTL", result.get(3).getNetworkName());
        Assert.assertEquals("admin sysadmin", result.get(3).getAccExecName());

        Assert.assertEquals(new Long(561), result.get(4).getLoadId());
        Assert.assertNull(result.get(4).getAdjustmentId());
        Assert.assertEquals("20121006-15-1", result.get(4).getBol());
        Assert.assertEquals("20121006-15-2", result.get(4).getPo());
        Assert.assertEquals("TEST1", result.get(4).getPro());
        Assert.assertEquals(new BigDecimal("93.70"), result.get(4).getRevenue());
        Assert.assertEquals(new BigDecimal("468.52"), result.get(4).getCost());
        Assert.assertEquals(new BigDecimal("-400.02"), result.get(4).getMargin());
        Assert.assertEquals(new BigDecimal("12.00"), result.get(4).getVendorBillAmount());
        Assert.assertTrue(result.get(4).getDeliveryDate().toString(),
                DateUtils.isSameDay(
                        DateUtils.parseDate("2012-10-07", "yyyy-MM-dd"),
                        result.get(4).getDeliveryDate()));
        Assert.assertNull(result.get(4).getReason());
        Assert.assertEquals("FEDEX FREIGHT ECONOMY", result.get(4).getCarrierName());
        Assert.assertEquals("PLS SHIPPER", result.get(4).getCustomerName());
        Assert.assertEquals("LTL", result.get(4).getNetworkName());
        Assert.assertEquals("admin sysadmin", result.get(4).getAccExecName());
    }

    @Ignore
    @Test
    public void shouldGetPriceAuditDataForUser() throws Exception {
        executeScript("preparePriceAudit.sql");

        List<AuditBO> result = dao.getPriceAuditData(USER_ID);
        Assert.assertEquals(5, result.size());
        Collections.sort(result, new Comparator<AuditBO>() {
            @Override
            public int compare(AuditBO o1, AuditBO o2) {
                return o1.getLoadId().compareTo(o2.getLoadId());
            }
        });

        Assert.assertEquals(new Long(56), result.get(0).getLoadId());
        Assert.assertEquals(new Long(3), result.get(0).getAdjustmentId());
        Assert.assertEquals("00000054", result.get(0).getBol());
        Assert.assertEquals("00000054", result.get(0).getPro());
        Assert.assertEquals(new BigDecimal("8.72"), result.get(0).getMargin());
        Assert.assertTrue(result.get(0).getDeliveryDate().toString(),
                DateUtils.isSameDay(DateUtils.parseDate("2010-01-08", "yyyy-MM-dd"), result.get(0).getDeliveryDate()));
        Assert.assertEquals("ANTG", result.get(0).getScac());
        Assert.assertEquals("PLS SHIPPER", result.get(0).getCustomerName());
        Assert.assertEquals("admin sysadmin", result.get(0).getAccExecName());
        Assert.assertEquals("LTL", result.get(0).getNetworkName());
        // Assert.assertEquals("Missing Document, Cost Diff",
        // result.get(0).getReason());
        Assert.assertTrue(result.get(0).getPriceAuditDate().toString(),
                DateUtils.isSameDay(DateUtils.parseDate("2036-10-19", "yyyy-MM-dd"), result.get(0).getPriceAuditDate()));

        Assert.assertEquals(new Long(59), result.get(1).getLoadId());
        Assert.assertEquals(new Long(6), result.get(1).getAdjustmentId());
        Assert.assertEquals("00000057", result.get(1).getBol());
        Assert.assertEquals("00000057", result.get(1).getPro());
        Assert.assertEquals(new BigDecimal("29.05"), result.get(1).getMargin());
        Assert.assertTrue(result.get(1).getDeliveryDate().toString(),
                DateUtils.isSameDay(
                        DateUtils.parseDate("2010-01-12", "yyyy-MM-dd"),
                        result.get(1).getDeliveryDate()));
        Assert.assertEquals("ANTG", result.get(1).getScac());
        Assert.assertEquals("PLS SHIPPER", result.get(1).getCustomerName());
        Assert.assertEquals("admin sysadmin", result.get(1).getAccExecName());
        Assert.assertEquals("LTL", result.get(1).getNetworkName());
        Assert.assertEquals("Missing Document", result.get(1).getReason());
        Assert.assertTrue(result.get(1).getPriceAuditDate().toString(),
                DateUtils.isSameDay(DateUtils.parseDate("2035-10-18", "yyyy-MM-dd"), result.get(1).getPriceAuditDate()));

        Assert.assertEquals(new Long(60), result.get(2).getLoadId());
        Assert.assertEquals(new Long(7), result.get(2).getAdjustmentId());
        Assert.assertEquals("00000058", result.get(2).getBol());
        Assert.assertEquals("00000058", result.get(2).getPro());
        Assert.assertEquals(new BigDecimal("25.48"), result.get(2).getMargin());
        Assert.assertTrue(result.get(2).getDeliveryDate().toString(),
                DateUtils.isSameDay(
                        DateUtils.parseDate("2008-04-11", "yyyy-MM-dd"),
                        result.get(2).getDeliveryDate()));
        Assert.assertEquals("ANTG", result.get(2).getScac());
        Assert.assertEquals("PLS SHIPPER", result.get(2).getCustomerName());
        Assert.assertEquals("admin sysadmin", result.get(2).getAccExecName());
        Assert.assertEquals("LTL", result.get(2).getNetworkName());
        Assert.assertNull(result.get(2).getReason());
        Assert.assertNull(result.get(2).getPriceAuditDate());

        Assert.assertEquals(new Long(560), result.get(3).getLoadId());
        Assert.assertNull(result.get(3).getAdjustmentId());
        Assert.assertEquals("20121006-15-1", result.get(3).getBol());
        Assert.assertNull(result.get(3).getPro());
        Assert.assertEquals(new BigDecimal("80"), result.get(3).getMargin());
        Assert.assertTrue(result.get(3).getDeliveryDate().toString(),
                DateUtils.isSameDay(
                        DateUtils.parseDate("2012-10-10", "yyyy-MM-dd"),
                        result.get(3).getDeliveryDate()));
        Assert.assertEquals("EXLA", result.get(3).getScac());
        Assert.assertEquals("PLS SHIPPER", result.get(3).getCustomerName());
        Assert.assertEquals("admin sysadmin", result.get(3).getAccExecName());
        Assert.assertEquals("LTL", result.get(3).getNetworkName());
        Assert.assertEquals("Cost Diff, Missing Document", result.get(3).getReason());
        Assert.assertTrue(result.get(3).getPriceAuditDate().toString(),
                DateUtils.isSameDay(DateUtils.parseDate("2035-10-20", "yyyy-MM-dd"), result.get(3).getPriceAuditDate()));

        Assert.assertEquals(new Long(561), result.get(4).getLoadId());
        Assert.assertNull(result.get(4).getAdjustmentId());
        Assert.assertEquals("20121006-15-1", result.get(4).getBol());
        Assert.assertNull(result.get(4).getPro());
        Assert.assertEquals(new BigDecimal("-400.02"), result.get(4).getMargin());
        Assert.assertTrue(result.get(4).getDeliveryDate().toString(),
                DateUtils.isSameDay(
                        DateUtils.parseDate("2012-10-07", "yyyy-MM-dd"),
                        result.get(4).getDeliveryDate()));
        Assert.assertEquals("FXNL", result.get(4).getScac());
        Assert.assertEquals("PLS SHIPPER", result.get(4).getCustomerName());
        Assert.assertEquals("admin sysadmin", result.get(4).getAccExecName());
        Assert.assertEquals("LTL", result.get(4).getNetworkName());
        Assert.assertNull(result.get(4).getReason());
        Assert.assertNull(result.get(4).getPriceAuditDate());
    }

    @Test
    public void shouldCheckLoadWithCostItems() {
        assertTrue(dao.isLoadHasCostItems(415L));
        getSession().createQuery("update CostDetailItemEntity ci set ci.financialAccessorials.id = 1 where ci.costDetails.id = 295").executeUpdate();
        flushAndClearSession();
        assertFalse(dao.isLoadHasCostItems(415L));
    }

    @Test
    public void shouldCheckAdjustmentWithCostItems() {
        assertTrue(dao.isAdjustmentHasCostItems(3L));
        getSession().createQuery("update CostDetailItemEntity ci set ci.financialAccessorials.id = null where ci.financialAccessorials.id = 3").
                executeUpdate();
        flushAndClearSession();
        assertFalse(dao.isAdjustmentHasCostItems(3L));
    }

    @Test
    public void shouldUpdateAdjustmentFinancialStatus() {
        SecurityTestUtils.login("USER");
        FinancialAccessorialsEntity adjustment = (FinancialAccessorialsEntity) getSession().get(FinancialAccessorialsEntity.class, 8L);
        assertNotNull(adjustment);
        assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL, adjustment.getFinancialStatus());

        dao.updateAdjustmentFinancialStatus(8L, ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        flushAndClearSession();

        adjustment = (FinancialAccessorialsEntity) getSession().get(FinancialAccessorialsEntity.class, 8L);
        assertNotNull(adjustment);
        assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE, adjustment.getFinancialStatus());
    }
}
