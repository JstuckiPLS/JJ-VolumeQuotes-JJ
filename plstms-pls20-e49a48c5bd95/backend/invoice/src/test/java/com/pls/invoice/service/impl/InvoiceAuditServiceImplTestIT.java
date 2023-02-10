package com.pls.invoice.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.bo.AuditRecordsBO;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.invoice.service.InvoiceAuditService;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LdBillingAuditReasonsEntity;
import com.pls.shipment.domain.LoadEntity;
//import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.service.ShipmentNoteService;
import com.pls.shipment.service.ShipmentService;

/**
 * Integration test for Invoice Audit Service.
 * 
 * @author Brichak Aleksandr
 * 
 */
public class InvoiceAuditServiceImplTestIT extends BaseServiceITClass {

    @Autowired
    private InvoiceAuditService invoiceAuditService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentNoteService shipmentNoteService;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Before
    public void setUp() throws Exception {
        SecurityTestUtils.login("test", 2L, 1L);
    }

    @Test
    public void shouldSendLoadToInvoiceAudit() throws EntityNotFoundException {
        Long shipmentId = 1L;
        ltlShipmentDao.updateLoadFinancialStatuses(new AuditReasonBO(shipmentId), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        LoadEntity shipment = shipmentService.findById(shipmentId);
        Assert.assertNotNull(shipment);

        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getBillingAuditReasons());
        Assert.assertTrue(shipment.getBillingAuditReasons().isEmpty());

        AuditReasonBO auditReason = bildAuditReasonBO();
        List<AuditRecordsBO> auditRecords = bildAuditRecordsBO(shipmentId, null);

        invoiceAuditService.sendToAudit(auditRecords, auditReason.getCode(), auditReason.getNote(), true);
        getSession().refresh(shipment);
        Assert.assertNotNull(shipment);
        Assert.assertNotNull(shipmentNoteService.findShipmentNotes(shipmentId));
        Assert.assertEquals(shipmentNoteService.findShipmentNotes(shipmentId).get(0).getNote(),
                "Invoice Audit:  Reason Miscellaneous Comment: Test Note");
        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        Assert.assertNotNull(shipment.getBillingAuditReasons());
    }

    @Test
    public void shouldSendLoadToPriceAudit() throws EntityNotFoundException {
        Long shipmentId = 1L;
        ltlShipmentDao.updateLoadFinancialStatuses(new AuditReasonBO(shipmentId), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        LoadEntity shipment = shipmentService.findById(shipmentId);
        Assert.assertNotNull(shipment);

        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getBillingAuditReasons());
        AuditReasonBO auditReason = bildAuditReasonBO();
        List<AuditRecordsBO> auditRecords = bildAuditRecordsBO(shipmentId, null);
        invoiceAuditService.sendToAudit(auditRecords, auditReason.getCode(), auditReason.getNote(), false);
        getSession().refresh(shipment);
        Assert.assertNotNull(shipment);
        Assert.assertNotNull(shipmentNoteService.findShipmentNotes(shipmentId));
        Assert.assertEquals(shipmentNoteService.findShipmentNotes(shipmentId).get(0).getNote(),
                "Billing Hold:  Reason Miscellaneous Comment: Test Note");
        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.PRICING_AUDIT_HOLD);
        Assert.assertNotNull(shipment.getBillingAuditReasons());
    }

    @Test
    public void shouldSendAdjustmentToInvoiceAudit() throws EntityNotFoundException {
        Long shipmentId = 631L;
        ltlShipmentDao.updateLoadFinancialStatuses(new AuditReasonBO(shipmentId), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        LoadEntity shipment = shipmentService.findById(shipmentId);
        Assert.assertNotNull(shipment);

        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getFinancialAccessorials());
        Assert.assertTrue(shipment.getFinancialAccessorials().iterator().hasNext());
        Assert.assertEquals(shipment.getFinancialAccessorials().iterator().next().getFinancialStatus(),
                ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);

        Assert.assertNotNull(shipment.getBillingAuditReasons());

        AuditReasonBO auditReason = bildAuditReasonBO();
        List<AuditRecordsBO> auditRecords = bildAuditRecordsBO(shipmentId, 20L);

        invoiceAuditService.sendToAudit(auditRecords, auditReason.getCode(), auditReason.getNote(), true);
        getSession().refresh(shipment);

        Assert.assertNotNull(shipment);

        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getFinancialAccessorials());
        Assert.assertTrue(shipment.getFinancialAccessorials().iterator().hasNext());
        for (Iterator<FinancialAccessorialsEntity> iterator = shipment.getFinancialAccessorials().iterator(); iterator
                .hasNext();) {
            FinancialAccessorialsEntity financialAccessorials = (FinancialAccessorialsEntity) iterator.next();
            if (Status.ACTIVE.equals(financialAccessorials.getStatus())) {
                getSession().refresh(financialAccessorials);
                Assert.assertEquals(financialAccessorials.getFinancialStatus(),
                        ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD_ADJUSTMENT_ACCESSORIAL);
            }
        }
    }

    @Test
    public void shouldSendAdjustmentToPriceAudit() throws EntityNotFoundException {
        Long shipmentId = 631L;
        ltlShipmentDao.updateLoadFinancialStatuses(new AuditReasonBO(shipmentId), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        LoadEntity shipment = shipmentService.findById(shipmentId);
        Assert.assertNotNull(shipment);

        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getFinancialAccessorials());
        Assert.assertTrue(shipment.getFinancialAccessorials().iterator().hasNext());
        Assert.assertEquals(shipment.getFinancialAccessorials().iterator().next().getFinancialStatus(),
                ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);

        Assert.assertNotNull(shipment.getBillingAuditReasons());
        AuditReasonBO auditReason = bildAuditReasonBO();
        List<AuditRecordsBO> auditRecords = bildAuditRecordsBO(shipmentId, 20L);
        invoiceAuditService.sendToAudit(auditRecords, auditReason.getCode(), auditReason.getNote(), false);
        getSession().refresh(shipment);
        Assert.assertNotNull(shipment);
        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getFinancialAccessorials());
        Assert.assertTrue(shipment.getFinancialAccessorials().iterator().hasNext());
        for (Iterator<FinancialAccessorialsEntity> iterator = shipment.getFinancialAccessorials().iterator(); iterator
                .hasNext();) {
            FinancialAccessorialsEntity financialAccessorials = (FinancialAccessorialsEntity) iterator.next();
            if (Status.ACTIVE.equals(financialAccessorials.getStatus())) {
                getSession().refresh(financialAccessorials);
                Assert.assertEquals(financialAccessorials.getFinancialStatus(),
                        ShipmentFinancialStatus.PRICING_AUDIT_HOLD);
            }
        }
    }

    @Test
    public void shouldSetReadyForConsolidatedReasonForAdjustment() throws EntityNotFoundException {
        Long shipmentId = 631L;
        ltlShipmentDao.updateLoadFinancialStatuses(new AuditReasonBO(shipmentId),
                ShipmentFinancialStatus.ACCOUNTING_BILLING);
        LoadEntity shipment = shipmentService.findById(shipmentId);
        Assert.assertNotNull(shipment);

        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getFinancialAccessorials());
        Assert.assertTrue(shipment.getFinancialAccessorials().iterator().hasNext());
        Assert.assertEquals(shipment.getFinancialAccessorials().iterator().next().getFinancialStatus(),
                ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);

        Assert.assertNotNull(shipment.getBillingAuditReasons());
        AuditReasonBO auditReason = bildAuditReasonBO();
        List<AuditRecordsBO> auditRecords = bildAuditRecordsBO(shipmentId, 20L);
        invoiceAuditService.sendToAudit(auditRecords, auditReason.getCode(), auditReason.getNote(), true);
        getSession().refresh(shipment);

        Assert.assertNotNull(shipment);
        Set<FinancialAccessorialsEntity> financialAccess = shipment.getFinancialAccessorials();
        Assert.assertEquals(financialAccess.size(), 1);

        FinancialAccessorialsEntity accessorial = financialAccess.iterator().next();
        getSession().refresh(accessorial);


        Assert.assertEquals(financialAccess.iterator().next().getBillingAuditReasons().iterator().next().getReasonCd(),
                "MS");

        invoiceAuditService.setReadyForConsolidatedReason(auditRecords);
        getSession().refresh(shipment);
        getSession().refresh(accessorial);

        Assert.assertEquals(financialAccess.iterator().next().getBillingAuditReasons().iterator().next().getReasonCd(),
                "CR");
    }

    @Test
    public void shouldSetReadyConsolidatedReasonForLoad() throws EntityNotFoundException {
        Long shipmentId = 1L;
        ltlShipmentDao.updateLoadFinancialStatuses(new AuditReasonBO(shipmentId),
                ShipmentFinancialStatus.ACCOUNTING_BILLING);
        LoadEntity shipment = shipmentService.findById(shipmentId);
        Assert.assertNotNull(shipment);

        Assert.assertNotNull(shipment.getFinalizationStatus());
        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING);
        Assert.assertNotNull(shipment.getBillingAuditReasons());
        Assert.assertTrue(shipment.getBillingAuditReasons().isEmpty());

        AuditReasonBO auditReason = bildAuditReasonBO();
        List<AuditRecordsBO> auditRecords = bildAuditRecordsBO(shipmentId, null);

        invoiceAuditService.sendToAudit(auditRecords, auditReason.getCode(), auditReason.getNote(), true);
        getSession().refresh(shipment);
        Assert.assertNotNull(shipment);
        Set<LdBillingAuditReasonsEntity> billingAuditReasons = shipment.getBillingAuditReasons();
        Assert.assertNotNull(billingAuditReasons);
        Assert.assertEquals(billingAuditReasons.size(), 1);
        Assert.assertEquals(billingAuditReasons.iterator().next().getReasonCd(), "MS");

        Assert.assertEquals(shipment.getFinalizationStatus(), ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);

        invoiceAuditService.setReadyForConsolidatedReason(auditRecords);
        getSession().refresh(shipment);
        billingAuditReasons = shipment.getBillingAuditReasons();
        Assert.assertNotNull(billingAuditReasons);
        Assert.assertEquals(billingAuditReasons.size(), 1);
        Assert.assertEquals(billingAuditReasons.iterator().next().getReasonCd(), "CR");
    }

    private AuditReasonBO bildAuditReasonBO() {
        AuditReasonBO auditReason = new AuditReasonBO();
        auditReason.setNote("Test Note");
        auditReason.setCode("MS");
        return auditReason;
    }

    private List<AuditRecordsBO> bildAuditRecordsBO(Long shipmentId, Long adjustmentId) {
        ArrayList<AuditRecordsBO> result = new ArrayList<AuditRecordsBO>();
        AuditRecordsBO auditReason = new AuditRecordsBO();
        auditReason.setLoadId(shipmentId);
        auditReason.setAdjustmentId(adjustmentId);
        result.add(auditReason);
        return result;
    }
}