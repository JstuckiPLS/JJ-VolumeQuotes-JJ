package com.pls.invoice.dao.impl;

import java.math.BigInteger;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.WeekDays;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.BillToInvoiceProcessingTimeBO;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;


/**
 * Tests for {@link FinancialInvoiceDao}.
 *
 * @author Aleksandr Leshchenko
 */
public class FinancialInvoiceDaoImplIT extends AbstractDaoTest {

    @Autowired
    private FinancialInvoiceDao dao;

    @Test
    public void shouldUpdateInvoicedLoad() {
        LoadEntity loadEntity = getSession().get(LoadEntity.class, 812L);
        Integer version = loadEntity.getVersion();
        ShipmentFinancialStatus finalizationStatus = loadEntity.getFinalizationStatus();
        Date modifiedDate = loadEntity.getModification().getModifiedDate();
        Long modifiedBy = loadEntity.getModification().getModifiedBy();
        Date glDate = loadEntity.getGlDate();

        LocalDateTime invoiceDate = LocalDateTime.now().plusDays(-1);

        dao.updateLoadsFinancialStatuses(Arrays.asList(812L), Date.from(invoiceDate.atZone(ZoneId.of("UTC")).toInstant()), 1L);

        getSession().flush();
        getSession().clear();
        loadEntity = getSession().get(LoadEntity.class, 812L);
        Assert.assertEquals(new Integer(version + 1), loadEntity.getVersion());
        Assert.assertNotEquals(finalizationStatus, loadEntity.getFinalizationStatus());
        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE, loadEntity.getFinalizationStatus());
        Assert.assertFalse(DateUtils.isSameDay(modifiedDate, loadEntity.getModification().getModifiedDate()));
        Assert.assertTrue(DateUtils.isSameDay(new Date(), loadEntity.getModification().getModifiedDate()));
        Assert.assertNotEquals(modifiedBy, loadEntity.getModification().getModifiedBy());
        Assert.assertEquals(new Long(1), loadEntity.getModification().getModifiedBy());
        Assert.assertTrue(glDate == null || !DateUtils.isSameDay(glDate, loadEntity.getGlDate()));
        Assert.assertTrue(DateUtils.isSameDay(Date.from(invoiceDate.atZone(ZoneId.of("UTC")).toInstant()), loadEntity.getGlDate()));
    }

    @Test
    public void shouldGetLoadsByInvoiceId() {
        executeScript("prepareInvoiceHistory.sql");

        Long invoiceId = 1L;

        List<LoadEntity> loads = dao.getLoadsByInvoiceId(invoiceId);

        Assert.assertEquals(3, loads.size());
        Assert.assertEquals(new Long(812), loads.get(0).getId());
        Assert.assertEquals(new Long(813), loads.get(1).getId());
        Assert.assertEquals(new Long(816), loads.get(2).getId());
    }

    @Test
    public void shouldGetAdjustmentsByInvoiceId() {
        executeScript("prepareInvoiceHistory.sql");

        Long invoiceId = 1L;

        List<FinancialAccessorialsEntity> adjustments = dao.getAdjustmentsByInvoiceId(invoiceId);

        Assert.assertEquals(3, adjustments.size());
        Assert.assertEquals(new Long(5), adjustments.get(0).getId());
        Assert.assertEquals(new Long(2), adjustments.get(1).getId());
        Assert.assertEquals(new Long(1), adjustments.get(2).getId());
    }

    @Test
    public void shouldGetWeeklyBillToIdsForAutomaticProcessing() {
        ProcessingPeriod period = ProcessingPeriod.WEEKLY;
        int dayOfWeek = Calendar.THURSDAY;
        Integer minutes = null;

        getSession().createQuery("update FinancialAccessorialsEntity set invoiceApproved = 'Y'").executeUpdate();

        List<BillToInvoiceProcessingTimeBO> result = dao.getBillToIdsForAutomaticProcessing(period, dayOfWeek, minutes);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(21L, result.get(0).getBillToId().longValue());
        Assert.assertEquals(1020, result.get(0).getProcessingTime().intValue());
        Assert.assertEquals("US/Eastern", result.get(0).getTimeZoneName());
        Assert.assertTrue(formatDate(new Date()) + " " + formatDate(result.get(0).getFilterLoadsDate()),
                DateUtils.isSameDay(new Date(), result.get(0).getFilterLoadsDate()));
    }

    @Test
    public void shouldGetWeeklyBillToIdsForAutomaticProcessingWithFutureReleaseDay() {
        ProcessingPeriod period = ProcessingPeriod.WEEKLY;
        int dayOfWeek = Calendar.THURSDAY;
        Integer minutes = null;

        getSession().createQuery("update InvoiceSettingsEntity set releaseDayOfWeek = :dayOfWeek").setParameter("dayOfWeek", WeekDays.Thursday)
                .executeUpdate();
        getSession().createQuery("update FinancialAccessorialsEntity set invoiceApproved = 'Y'").executeUpdate();

        List<BillToInvoiceProcessingTimeBO> result = dao.getBillToIdsForAutomaticProcessing(period, dayOfWeek, minutes);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(21L, result.get(0).getBillToId().longValue());
        Assert.assertEquals(1020, result.get(0).getProcessingTime().intValue());
        Assert.assertEquals("US/Eastern", result.get(0).getTimeZoneName());
        Calendar invoiceDate = Calendar.getInstance();
        invoiceDate.add(Calendar.DAY_OF_YEAR, -7);
        Assert.assertTrue(formatDate(invoiceDate.getTime()) + " " + formatDate(result.get(0).getFilterLoadsDate()),
                DateUtils.isSameDay(invoiceDate.getTime(), result.get(0).getFilterLoadsDate()));
    }

    @Test
    public void shouldGetDailyBillToIdsForAutomaticProcessing() {
        ProcessingPeriod period = ProcessingPeriod.DAILY;
        int dayOfWeek = Calendar.MONDAY;
        Integer minutes = 659;

        getSession().createQuery("update FinancialAccessorialsEntity set invoiceApproved = 'Y'").executeUpdate();

        List<BillToInvoiceProcessingTimeBO> result = dao.getBillToIdsForAutomaticProcessing(period, dayOfWeek, minutes);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(new Long(22), result.get(0).getBillToId());
        Assert.assertEquals(660, result.get(0).getProcessingTime().intValue());
        Assert.assertEquals("US/Eastern", result.get(0).getTimeZoneName());
        Assert.assertTrue(formatDate(new Date()) + " " + formatDate(result.get(0).getFilterLoadsDate()),
                DateUtils.isSameDay(new Date(), result.get(0).getFilterLoadsDate()));
    }

    @Test
    public void shouldGetNextInvoiceId() {
        BigInteger expectedInvoiceId = (BigInteger) getSession().createSQLQuery("SELECT NEXTVAL('flatbed.INVOICE_HISTORY_INV_ID_SEQ')")
                .uniqueResult();

        long nextInvoiceId = dao.getNextInvoiceId();

        Assert.assertEquals(expectedInvoiceId.longValue() + 1, nextInvoiceId);
    }

    @Test
    public void shouldGetNextCBIInvoiceNumber() {
        BigInteger expectedInvoiceNumber = (BigInteger) getSession().createSQLQuery("SELECT NEXTVAL('flatbed.CBI_INVOICE_NUMB_SEQ')")
                .uniqueResult();

        long nextInvoiceId = dao.getNextCBIInvoiceSequenceNumber();

        Assert.assertEquals(expectedInvoiceNumber.longValue() + 1, nextInvoiceId);
    }

    @Test
    public void shouldGetAllLoadsIdsByInvoiceId() {
        executeScript("prepareInvoiceHistory.sql");
        getSession().createQuery("update LoadEntity set finalizationStatus = 'FP', billToId = 6 where id in :loadIds")
                .setParameterList("loadIds", Arrays.asList(812L, 813L, 816L)).executeUpdate();
        getSession().createQuery("update CostDetailItemEntity set billTo.id = 6 where financialAccessorials.id in :adjIds")
                .setParameterList("adjIds", Arrays.asList(1L, 2L, 5L)).executeUpdate();
        Long invoiceId = 1L;

        List<Long> loadsIds = dao.getAllLoadsIds(invoiceId, 6L);

        Assert.assertEquals(6, loadsIds.size());
        Assert.assertTrue(loadsIds.toString(), loadsIds.containsAll(Arrays.asList(58L, 160L, 166L, 812L, 813L, 816L)));
    }

    @Test
    public void shouldGetBillToByInvoiceIdFromLoad() {
        Collection<Long> loadsIds = Arrays.asList(3574951L, 3571622L);
        Long invoiceId = 2L;
        Long userId = (long) (Math.random() * 100);
        getSession().createQuery("update LoadEntity set finalizationStatus = 'FP' where id in :loadIds").setParameterList("loadIds", loadsIds)
                .executeUpdate();
        dao.insertLoadsForReProcess(invoiceId, loadsIds, userId);

        BillToEntity billTo = dao.getBillToByInvoiceId(invoiceId);

        Assert.assertNotNull(billTo);
        Assert.assertEquals(new Long(1), billTo.getId());
    }

    @Test
    public void shouldGetBillToByInvoiceIdFromAdjustment() {
        Collection<Long> adjustmentsIds = Arrays.asList(8L, 10L, 20L, 23L);
        Long invoiceId = 2L;
        Long userId = (long) (Math.random() * 100);
        getSession().createQuery("update FinancialAccessorialsEntity set financialStatus = 'FP' where id in :adjustmentsIds")
                .setParameterList("adjustmentsIds", adjustmentsIds).executeUpdate();
        dao.insertAdjustmentsForReProcess(invoiceId, adjustmentsIds, userId);

        BillToEntity billTo = dao.getBillToByInvoiceId(invoiceId);

        Assert.assertNotNull(billTo);
        Assert.assertEquals(new Long(6), billTo.getId());
    }

    private String formatDate(Date date) {
        return DateFormat.getInstance().format(date);
    }
}
