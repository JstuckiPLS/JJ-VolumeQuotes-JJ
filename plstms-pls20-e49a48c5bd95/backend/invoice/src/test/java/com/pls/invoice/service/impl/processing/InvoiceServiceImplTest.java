package com.pls.invoice.service.impl.processing;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.shared.Status;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Test for {@link InvoiceServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceImplTest {
    private static final Date DATE = new Date();

    @Mock
    private FinancialInvoiceDao invoiceDao;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Test
    public void shouldGetInvoicesSortedByGLNumber() {
        Long invoiceId = (long) (Math.random() * 100);

        LoadEntity load1 = getLoad(1L, "bol1", "gl2", getDate(-1), InvoiceSortType.GL_NUM);
        LoadEntity load2 = getLoad(3L, "bol1", "gl4", getDate(-1), InvoiceSortType.GL_NUM);
        LoadEntity load3 = getLoad(5L, "bol1", "gl3", getDate(-1), InvoiceSortType.GL_NUM);
        FinancialAccessorialsEntity adj1 = getAdjustment(1L, 4L, "bol1", "gl1", getDate(-1), InvoiceSortType.GL_NUM);
        FinancialAccessorialsEntity adj2 = getAdjustment(3L, 4L, "bol1", "gl1", getDate(-1), InvoiceSortType.GL_NUM);
        FinancialAccessorialsEntity adj3 = getAdjustment(5L, 2L, "bol1", "gl5", getDate(-1), InvoiceSortType.GL_NUM);
        List<LoadEntity> loads = Arrays.asList(load1, load2, load3);
        List<FinancialAccessorialsEntity> adjustments = Arrays.asList(adj1, adj2, adj3);
        Collections.shuffle(loads);
        Collections.shuffle(adjustments);
        Mockito.when(invoiceDao.getLoadsByInvoiceId(invoiceId)).thenReturn(loads);
        Mockito.when(invoiceDao.getAdjustmentsByInvoiceId(invoiceId)).thenReturn(adjustments);

        List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);

        Assert.assertEquals(6, invoices.size());
        Assert.assertSame(adj1, invoices.get(0).getAdjustment());
        Assert.assertSame(adj2, invoices.get(1).getAdjustment());
        Assert.assertSame(adj3, invoices.get(5).getAdjustment());
        Assert.assertSame(load1, invoices.get(2).getLoad());
        Assert.assertSame(load2, invoices.get(4).getLoad());
        Assert.assertSame(load3, invoices.get(3).getLoad());
    }

    @Test
    public void shouldGetInvoicesSortedByBOLNumber() {
        Long invoiceId = (long) (Math.random() * 100);

        LoadEntity load1 = getLoad(1L, "bol5", "gl1", getDate(-1), InvoiceSortType.BOL);
        LoadEntity load2 = getLoad(3L, "bol4", "gl1", getDate(-1), InvoiceSortType.BOL);
        LoadEntity load3 = getLoad(5L, "bol1", "gl1", getDate(-1), InvoiceSortType.BOL);
        FinancialAccessorialsEntity adj1 = getAdjustment(1L, 4L, "bol3", "gl1", getDate(-1), InvoiceSortType.BOL);
        FinancialAccessorialsEntity adj2 = getAdjustment(3L, 4L, "bol3", "gl1", getDate(-1), InvoiceSortType.BOL);
        FinancialAccessorialsEntity adj3 = getAdjustment(5L, 2L, "bol2", "gl1", getDate(-1), InvoiceSortType.BOL);
        List<LoadEntity> loads = Arrays.asList(load1, load2, load3);
        List<FinancialAccessorialsEntity> adjustments = Arrays.asList(adj1, adj2, adj3);
        Collections.shuffle(loads);
        Collections.shuffle(adjustments);
        Mockito.when(invoiceDao.getLoadsByInvoiceId(invoiceId)).thenReturn(loads);
        Mockito.when(invoiceDao.getAdjustmentsByInvoiceId(invoiceId)).thenReturn(adjustments);

        List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);

        Assert.assertEquals(6, invoices.size());
        Assert.assertSame(adj1, invoices.get(2).getAdjustment());
        Assert.assertSame(adj2, invoices.get(3).getAdjustment());
        Assert.assertSame(adj3, invoices.get(1).getAdjustment());
        Assert.assertSame(load1, invoices.get(5).getLoad());
        Assert.assertSame(load2, invoices.get(4).getLoad());
        Assert.assertSame(load3, invoices.get(0).getLoad());
    }

    @Test
    public void shouldGetInvoicesSortedByDeliveryDate() {
        Long invoiceId = (long) (Math.random() * 100);

        LoadEntity load1 = getLoad(1L, "bol1", "gl1", getDate(-1), InvoiceSortType.DELIV_DATE);
        LoadEntity load2 = getLoad(3L, "bol1", "gl1", getDate(-4), InvoiceSortType.DELIV_DATE);
        LoadEntity load3 = getLoad(5L, "bol1", "gl1", getDate(-3), InvoiceSortType.DELIV_DATE);
        FinancialAccessorialsEntity adj1 = getAdjustment(1L, 4L, "bol1", "gl1", getDate(-2), InvoiceSortType.DELIV_DATE);
        FinancialAccessorialsEntity adj2 = getAdjustment(3L, 4L, "bol1", "gl1", getDate(-2), InvoiceSortType.DELIV_DATE);
        FinancialAccessorialsEntity adj3 = getAdjustment(5L, 2L, "bol1", "gl1", getDate(-5), InvoiceSortType.DELIV_DATE);
        List<LoadEntity> loads = Arrays.asList(load1, load2, load3);
        List<FinancialAccessorialsEntity> adjustments = Arrays.asList(adj1, adj2, adj3);
        Collections.shuffle(loads);
        Collections.shuffle(adjustments);
        Mockito.when(invoiceDao.getLoadsByInvoiceId(invoiceId)).thenReturn(loads);
        Mockito.when(invoiceDao.getAdjustmentsByInvoiceId(invoiceId)).thenReturn(adjustments);

        List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);

        Assert.assertEquals(6, invoices.size());
        Assert.assertSame(adj1, invoices.get(3).getAdjustment());
        Assert.assertSame(adj2, invoices.get(4).getAdjustment());
        Assert.assertSame(adj3, invoices.get(0).getAdjustment());
        Assert.assertSame(load1, invoices.get(5).getLoad());
        Assert.assertSame(load2, invoices.get(1).getLoad());
        Assert.assertSame(load3, invoices.get(2).getLoad());
    }

    @Test
    public void shouldGetInvoicesSortedByLoadId() {
        Long invoiceId = (long) (Math.random() * 100);

        LoadEntity load1 = getLoad(1L, "bol1", "gl1", getDate(-1), InvoiceSortType.LOAD_ID);
        LoadEntity load2 = getLoad(3L, "bol1", "gl1", getDate(-4), InvoiceSortType.LOAD_ID);
        LoadEntity load3 = getLoad(5L, "bol1", "gl1", getDate(-3), InvoiceSortType.LOAD_ID);
        FinancialAccessorialsEntity adj1 = getAdjustment(1L, 4L, "bol1", "gl1", getDate(-2), InvoiceSortType.LOAD_ID);
        FinancialAccessorialsEntity adj2 = getAdjustment(3L, 4L, "bol1", "gl1", getDate(-2), InvoiceSortType.LOAD_ID);
        FinancialAccessorialsEntity adj3 = getAdjustment(5L, 2L, "bol1", "gl1", getDate(-5), InvoiceSortType.LOAD_ID);
        List<LoadEntity> loads = Arrays.asList(load1, load2, load3);
        List<FinancialAccessorialsEntity> adjustments = Arrays.asList(adj1, adj2, adj3);
        Collections.shuffle(loads);
        Collections.shuffle(adjustments);
        Mockito.when(invoiceDao.getLoadsByInvoiceId(invoiceId)).thenReturn(loads);
        Mockito.when(invoiceDao.getAdjustmentsByInvoiceId(invoiceId)).thenReturn(adjustments);

        List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);

        Assert.assertEquals(6, invoices.size());
        Assert.assertSame(adj1, invoices.get(3).getAdjustment());
        Assert.assertSame(adj2, invoices.get(4).getAdjustment());
        Assert.assertSame(adj3, invoices.get(1).getAdjustment());
        Assert.assertSame(load1, invoices.get(0).getLoad());
        Assert.assertSame(load2, invoices.get(2).getLoad());
        Assert.assertSame(load3, invoices.get(5).getLoad());
    }

    @Test
    public void shouldGetInvoicesSortedWithDifferentBillTo() {
        Long invoiceId = (long) (Math.random() * 100);

        LoadEntity load1 = getLoad(1L, "bol1", "gl1", getDate(-1), InvoiceSortType.LOAD_ID);
        load1.getBillTo().setId(1L);
        LoadEntity load2 = getLoad(3L, "bol1", "gl1", getDate(-4), InvoiceSortType.DELIV_DATE);
        load2.getBillTo().setId(2L);
        LoadEntity load3 = getLoad(5L, "bol1", "gl1", getDate(-3), InvoiceSortType.LOAD_ID);
        load3.getBillTo().setId(1L);
        FinancialAccessorialsEntity adj1 = getAdjustment(1L, 4L, "bol1", "gl1", getDate(-2), InvoiceSortType.DELIV_DATE);
        adj1.getCostDetailItems().iterator().next().getBillTo().setId(2L);
        FinancialAccessorialsEntity adj2 = getAdjustment(3L, 4L, "bol1", "gl1", getDate(-2), InvoiceSortType.LOAD_ID);
        adj2.getCostDetailItems().iterator().next().getBillTo().setId(1L);
        FinancialAccessorialsEntity adj3 = getAdjustment(5L, 2L, "bol1", "gl1", getDate(-5), InvoiceSortType.DELIV_DATE);
        adj3.getCostDetailItems().iterator().next().getBillTo().setId(2L);
        List<LoadEntity> loads = Arrays.asList(load1, load2, load3);
        List<FinancialAccessorialsEntity> adjustments = Arrays.asList(adj1, adj2, adj3);
        Collections.shuffle(loads);
        Collections.shuffle(adjustments);
        Mockito.when(invoiceDao.getLoadsByInvoiceId(invoiceId)).thenReturn(loads);
        Mockito.when(invoiceDao.getAdjustmentsByInvoiceId(invoiceId)).thenReturn(adjustments);

        List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);

        Assert.assertEquals(6, invoices.size());
        Assert.assertSame(adj1, invoices.get(5).getAdjustment());
        Assert.assertSame(adj2, invoices.get(1).getAdjustment());
        Assert.assertSame(adj3, invoices.get(3).getAdjustment());
        Assert.assertSame(load1, invoices.get(0).getLoad());
        Assert.assertSame(load2, invoices.get(4).getLoad());
        Assert.assertSame(load3, invoices.get(2).getLoad());
    }

    private Date getDate(int amount) {
        return DateUtils.addDays(DATE, amount);
    }

    private LoadEntity getLoad(long loadId, String bol, String glNum, Date deliveryDate, InvoiceSortType sortType) {
        LoadEntity load = new LoadEntity();
        load.setBillTo(getBillTo(sortType));
        load.setId(loadId);
        load.getNumbers().setBolNumber(bol);
        load.getNumbers().setGlNumber(glNum);
        load.addLoadDetails(new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION));
        load.getDestination().setDeparture(deliveryDate);
        LoadCostDetailsEntity cost = new LoadCostDetailsEntity();
        cost.setStatus(Status.ACTIVE);
        cost.setInvoiceNumber("invoiceNumber" + Math.random());
        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        load.getCostDetails().add(cost);
        return load;
    }

    private BillToEntity getBillTo(InvoiceSortType sortType) {
        BillToEntity billTo = new BillToEntity();
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setSortType(sortType);
        billTo.setInvoiceSettings(invoiceSettings);
        return billTo;
    }

    private FinancialAccessorialsEntity getAdjustment(long adjustmentId, long loadId, String bol, String glNum, Date deliveryDate,
            InvoiceSortType sortType) {
        FinancialAccessorialsEntity adjustment = new FinancialAccessorialsEntity();
        adjustment.setInvoiceNumber("invoiceNumber" + Math.random());
        adjustment.setLoad(getLoad(loadId, bol, glNum, deliveryDate, null));
        CostDetailItemEntity costItem = new CostDetailItemEntity();
        costItem.setBillTo(getBillTo(sortType));
        adjustment.setCostDetailItems(Stream.of(costItem).collect(Collectors.toSet()));
        adjustment.setId(adjustmentId);
        return adjustment;
    }
}
