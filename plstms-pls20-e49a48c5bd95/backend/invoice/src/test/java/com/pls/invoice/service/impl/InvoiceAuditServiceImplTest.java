package com.pls.invoice.service.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.exception.ApplicationException;
import com.pls.invoice.dao.FinancialAuditDao;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.service.audit.LoadFinancialStatusTrackingService;

/**
 * Unit tests for Invoice Audit Service Impl.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceAuditServiceImplTest extends BaseServiceITClass {

    @Mock
    private FinancialAuditDao auditDao;

    @Mock
    private BillingAuditReasonsDao billingAuditReasonsDao;

    @Mock
    private LtlShipmentDao ltlShipmentDao;

    @InjectMocks
    private InvoiceAuditServiceImpl sut;

    @Mock
    private LoadFinancialStatusTrackingService loadTrackingService;

    @Test
    public void shouldApproveLoadsAndAdjustments() throws ApplicationException {
        Long loadId1 = (long) (Math.random() * 100);
        Long loadId2 = (long) (Math.random() * 100) + 101;
        Long loadId3 = (long) (Math.random() * 100) + 202;
        Long adjId1 = (long) (Math.random() * 100) + 303;
        Long adjId2 = (long) (Math.random() * 100) + 404;
        AuditReasonBO load1 = new AuditReasonBO(loadId1, null);
        AuditReasonBO load2 = new AuditReasonBO(loadId2, adjId1);
        AuditReasonBO load3 = new AuditReasonBO(loadId3, null);
        AuditReasonBO adj2 = new AuditReasonBO(null, adjId2);
        List<AuditReasonBO> auditRecords = Arrays.asList(load1, load2, load3, adj2);

        Mockito.when(auditDao.isLoadHasCostItems(loadId1)).thenReturn(true);
        Mockito.when(auditDao.isLoadHasCostItems(loadId3)).thenReturn(true);
        Mockito.when(auditDao.isAdjustmentHasCostItems(adjId1)).thenReturn(true);
        Mockito.when(auditDao.isAdjustmentHasCostItems(adjId2)).thenReturn(true);

        ArgumentCaptor<AuditReasonBO> captor = ArgumentCaptor.forClass(AuditReasonBO.class);
        sut.approveAudit(auditRecords);
        Mockito.verify(loadTrackingService, Mockito.times(2))
        .logLoadFinancialStatusEvent(captor.capture(), Mockito.eq(ShipmentFinancialStatus.ACCOUNTING_BILLING));
        List<AuditReasonBO> allValues = captor.getAllValues();
        Assert.assertEquals(2, allValues.size());
        Assert.assertEquals(load1.getLoadId(), allValues.get(0).getLoadId());
        Assert.assertNull(allValues.get(0).getAdjustmentId());
        Assert.assertEquals(load3.getLoadId(), allValues.get(1).getLoadId());
        Assert.assertNull(allValues.get(1).getAdjustmentId());

        Mockito.verify(auditDao).isLoadHasCostItems(loadId1);
        Mockito.verify(auditDao).isLoadHasCostItems(loadId3);
        Mockito.verify(auditDao).isAdjustmentHasCostItems(adjId1);
        Mockito.verify(auditDao).isAdjustmentHasCostItems(adjId2);

        Mockito.verify(auditDao).updateAdjustmentFinancialStatus(adjId1, ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);
        Mockito.verify(billingAuditReasonsDao).deactivateAuditReasonsStatusForAdjustment(adjId1);
        Mockito.verify(auditDao).updateAdjustmentFinancialStatus(adjId2, ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);
        Mockito.verify(billingAuditReasonsDao).deactivateAuditReasonsStatusForAdjustment(adjId2);

        Mockito.verify(ltlShipmentDao).updateLoadFinancialStatuses(load1, ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Mockito.verify(billingAuditReasonsDao).deactivateBillingAuditReasonsStatusForLoad(loadId1);
        Mockito.verify(ltlShipmentDao).updateLoadFinancialStatuses(load3, ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Mockito.verify(billingAuditReasonsDao).deactivateBillingAuditReasonsStatusForLoad(loadId3);

        Mockito.verifyNoMoreInteractions(auditDao);
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
        Mockito.verifyNoMoreInteractions(billingAuditReasonsDao);
    }

    @Test
    public void shouldNotApproveLoadsAndAdjustmentsWhenLoadHasNoCosts() throws ApplicationException {
        Long loadId1 = (long) (Math.random() * 100);
        Long loadId2 = (long) (Math.random() * 100) + 101;
        Long loadId3 = (long) (Math.random() * 100) + 202;
        Long adjId1 = (long) (Math.random() * 100) + 303;
        Long adjId2 = (long) (Math.random() * 100) + 404;
        List<AuditReasonBO> auditRecords = Arrays.asList(new AuditReasonBO(loadId1, null), new AuditReasonBO(loadId2, adjId1),
                new AuditReasonBO(loadId3, null), new AuditReasonBO(null, adjId2));

        Mockito.when(auditDao.isLoadHasCostItems(loadId1)).thenReturn(true);
        Mockito.when(auditDao.isLoadHasCostItems(loadId3)).thenReturn(false);
        Mockito.when(auditDao.isAdjustmentHasCostItems(adjId1)).thenReturn(true);
        Mockito.when(auditDao.isAdjustmentHasCostItems(adjId2)).thenReturn(true);

        boolean exception = false;
        try {
            sut.approveAudit(auditRecords);
        } catch (ApplicationException e) {
            Assert.assertEquals("Please add cost details to the Order", e.getMessage());
            exception = true;
        }
        Assert.assertTrue("Expected Exception but not thrown", exception);

        Mockito.verify(auditDao).isLoadHasCostItems(loadId1);
        Mockito.verify(auditDao).isLoadHasCostItems(loadId3);
        Mockito.verify(auditDao).isAdjustmentHasCostItems(adjId1);

        Mockito.verifyNoMoreInteractions(auditDao);
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
        Mockito.verifyNoMoreInteractions(billingAuditReasonsDao);
    }

    @Test
    public void shouldNotApproveLoadsAndAdjustmentsWhenAdjustmentHasNoCosts() throws ApplicationException {
        Long loadId1 = (long) (Math.random() * 100);
        Long loadId2 = (long) (Math.random() * 100) + 101;
        Long loadId3 = (long) (Math.random() * 100) + 202;
        Long adjId1 = (long) (Math.random() * 100) + 303;
        Long adjId2 = (long) (Math.random() * 100) + 404;
        List<AuditReasonBO> auditRecords = Arrays.asList(new AuditReasonBO(loadId1, null), new AuditReasonBO(loadId2, adjId1),
                new AuditReasonBO(loadId3, null), new AuditReasonBO(null, adjId2));

        Mockito.when(auditDao.isLoadHasCostItems(loadId1)).thenReturn(true);
        Mockito.when(auditDao.isLoadHasCostItems(loadId3)).thenReturn(true);
        Mockito.when(auditDao.isAdjustmentHasCostItems(adjId1)).thenReturn(false);
        Mockito.when(auditDao.isAdjustmentHasCostItems(adjId2)).thenReturn(true);

        boolean exception = false;
        try {
            sut.approveAudit(auditRecords);
        } catch (ApplicationException e) {
            Assert.assertEquals("Please add cost details to the Order", e.getMessage());
            exception = true;
        }
        Assert.assertTrue("Expected Exception but not thrown", exception);

        Mockito.verify(auditDao).isLoadHasCostItems(loadId1);
        Mockito.verify(auditDao).isAdjustmentHasCostItems(adjId1);

        Mockito.verifyNoMoreInteractions(auditDao);
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
        Mockito.verifyNoMoreInteractions(billingAuditReasonsDao);
    }

}