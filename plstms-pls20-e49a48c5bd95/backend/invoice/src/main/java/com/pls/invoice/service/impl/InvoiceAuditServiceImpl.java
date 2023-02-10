package com.pls.invoice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.bo.AuditRecordsBO;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Reasons;
import com.pls.invoice.dao.FinancialAuditDao;
import com.pls.invoice.domain.bo.AuditBO;
import com.pls.invoice.service.InvoiceAuditService;
import com.pls.shipment.dao.BillingAuditReasonCodeDao;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;
import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.service.audit.LoadFinancialStatusTrackingService;
import com.pls.shipment.service.impl.BillingAuditService;

/**
 * Implementation of {@link InvoiceAuditService}.
 *
 * @author Alexander Kirichenko
 */
@Service
@Transactional
public class InvoiceAuditServiceImpl implements InvoiceAuditService {

    @Autowired
    private FinancialAuditDao auditDao;

    @Autowired
    private BillingAuditReasonCodeDao reasonCodeDao;

    @Autowired
    private BillingAuditReasonsDao billingAuditReasonsDao;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private BillingAuditService billingAuditService;

    @Autowired
    private ShipmentNoteDao shipmentNoteDao;

    @Autowired
    private LoadFinancialStatusTrackingService loadTrackingService;

    @Override
    public List<AuditBO> getInvoiceAuditData(Long userId) {
        return auditDao.getInvoiceAuditData(userId);
    }

    @Override
    public List<AuditBO> getPriceAuditData(Long userId) {
        return auditDao.getPriceAuditData(userId);
    }

    @Override
    public void updateInvoiceApproved(List<Long> loadIds, final boolean approved) {
        new BatchProcessor<Long>(new ArrayList<Long>(loadIds)) {
            @Override
            protected void processElements(List<Long> elementsToProcess) {
                auditDao.updateInvoiceApproved(elementsToProcess, approved);
            }
        }.process();
    }

    @Override
    public void updateAdjustmentInvoiceApproved(List<Long> adjustmentIds, final boolean approved) {
        new BatchProcessor<Long>(new ArrayList<Long>(adjustmentIds)) {
            @Override
            protected void processElements(List<Long> elementsToProcess) {
                auditDao.updateAdjustmentInvoiceApproved(elementsToProcess, approved);
            }
        }.process();
    }

    /**
     * Class to process batch query.
     *
     * @param <T> element type to process.
     */
    private abstract class BatchProcessor<T> {
        private static final int MAX_LIST_SIZE = 500;
        private final List<T> elements;

        /**
         * Takes list elements to process.
         *
         * @param elements to process
         */
        BatchProcessor(List<T> elements) {
            this.elements = elements;
        }

        /**
         * Executes elements with defined amount per time.
         */
        public void process() {
            do {
                List<T> elementsToProcess = elements.subList(0, Math.min(MAX_LIST_SIZE, elements.size()));
                processElements(elementsToProcess);
                elementsToProcess.clear();
            } while (!elements.isEmpty());
        }

        /**
         * Calls methods to process sub list of elements.
         *
         * @param elementsToProcess sub list of elements
         */
        protected abstract void processElements(List<T> elementsToProcess);
    }

    @Override
    public List<LdBillAuditReasonCodeEntity> getListAuditReasonCodeForReasonType() {
        return reasonCodeDao.getReasonCodeEntityForReasonType();
    }

    @Override
    public void saveNewManualReasonForLoad(AuditReasonBO auditReason) {
        if (auditReason.getAdjustmentId() != null) {
            billingAuditReasonsDao.deactivateAuditReasonsStatusForAdjustment(auditReason.getAdjustmentId());
        } else {
            billingAuditReasonsDao.deactivateManualBillingAuditReasonsStatusForLoad(auditReason.getLoadId());
        }
        billingAuditReasonsDao.createAndSave(auditReason.getCode(), auditReason.getLoadId(), auditReason.getNote(),
                auditReason.getAdjustmentId());
    }

    @Override
    public void sendToAudit(List<AuditRecordsBO> list, String code, String note, boolean isSendToInvoiceAudit) {
        for (AuditRecordsBO auditReason : list) {
            moveLoadToAudit(isSendToInvoiceAudit, getAuditReasonFromAuditRecords(auditReason, code, note));
        }
    }

    @Override
    public void approveAudit(List<AuditReasonBO> auditRecords) throws ApplicationException {
        checkIsCostItemsExist(auditRecords);
        for (AuditReasonBO auditRecord : auditRecords) {
            if (auditRecord.getAdjustmentId() != null) {
                auditDao.updateAdjustmentFinancialStatus(auditRecord.getAdjustmentId(),
                        ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);
                billingAuditReasonsDao.deactivateAuditReasonsStatusForAdjustment(auditRecord.getAdjustmentId());
            } else {
                ltlShipmentDao.updateLoadFinancialStatuses(auditRecord, ShipmentFinancialStatus.ACCOUNTING_BILLING);
                billingAuditReasonsDao.deactivateBillingAuditReasonsStatusForLoad(auditRecord.getLoadId());
                loadTrackingService.logLoadFinancialStatusEvent(auditRecord,
                        ShipmentFinancialStatus.ACCOUNTING_BILLING);
            }
        }
    }

    @Override
    public void setReadyForConsolidatedReason(List<AuditRecordsBO> auditRecords) {
        for (AuditRecordsBO auditReasonBO : auditRecords) {
            if (auditReasonBO.getAdjustmentId() == null) {
                billingAuditReasonsDao.deactivateBillingAuditReasonsStatusForLoad(auditReasonBO.getLoadId());
            } else {
                billingAuditReasonsDao.deactivateAuditReasonsStatusForAdjustment(auditReasonBO.getAdjustmentId());
            }
            billingAuditReasonsDao.createAndSave(Reasons.READY_FOR_CONSOLIDATED.getReasonCode(),
                    auditReasonBO.getLoadId(), null, auditReasonBO.getAdjustmentId());
        }
    }

    private void moveLoadToAudit(boolean isSendToInvoiceAudit, AuditReasonBO auditReason) {
        saveNewManualReasonForLoad(auditReason);
        if (auditReason.getAdjustmentId() == null) {
            ShipmentFinancialStatus loadFinnStatus = isSendToInvoiceAudit ? ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD
                    : ShipmentFinancialStatus.PRICING_AUDIT_HOLD;
            ltlShipmentDao.updateLoadFinancialStatuses(auditReason, loadFinnStatus);
            billingAuditService.updateBillingAuditReasonForLoad(auditReason.getLoadId(), ShipmentFinancialStatus.NONE);
            loadTrackingService.logLoadFinancialStatusEvent(auditReason, loadFinnStatus);
        } else {
            ShipmentFinancialStatus adjFinnStatus = isSendToInvoiceAudit ? ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD_ADJUSTMENT_ACCESSORIAL
                    : ShipmentFinancialStatus.PRICING_AUDIT_HOLD;
            auditDao.updateAdjustmentFinancialStatus(auditReason.getAdjustmentId(), adjFinnStatus);
        }
        LdBillAuditReasonCodeEntity reason = reasonCodeDao.find(auditReason.getCode());
        createNoteFromReason(auditReason.getLoadId(), reason.getDescription(), auditReason.getNote(), isSendToInvoiceAudit);
    }

    private void createNoteFromReason(Long loadId, String reasonDesc, String comment, boolean isSendToInvoiceAudit) {
        ShipmentNoteEntity note = new ShipmentNoteEntity();
        note.setLoadId(loadId);
        note.setNote(String.format("%s Reason %s Comment: %s", isSendToInvoiceAudit ? "Invoice Audit: " : "Billing Hold: ",
                reasonDesc, comment == null ? " -" : comment));
        shipmentNoteDao.saveOrUpdate(note);
    }

    private void checkIsCostItemsExist(List<AuditReasonBO> auditRecords) throws ApplicationException {
        for (AuditReasonBO auditRecord : auditRecords) {
            boolean costItemsExist = false;
            if (auditRecord.getAdjustmentId() != null) {
                costItemsExist = auditDao.isAdjustmentHasCostItems(auditRecord.getAdjustmentId());
            } else {
                costItemsExist = auditDao.isLoadHasCostItems(auditRecord.getLoadId());
            }
            if (!costItemsExist) {
                throw new ApplicationException("Please add cost details to the Order");
            }
        }
    }

    private AuditReasonBO getAuditReasonFromAuditRecords(AuditRecordsBO auditRecord, String code, String note) {
        AuditReasonBO auditReason = new AuditReasonBO();
        auditReason.setCode(code);
        auditReason.setNote(note);
        auditReason.setLoadId(auditRecord.getLoadId());
        auditReason.setAdjustmentId(auditRecord.getAdjustmentId());
        return auditReason;
    }

}
