package com.pls.invoice.service;

import java.util.List;

import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.bo.AuditRecordsBO;
import com.pls.core.exception.ApplicationException;
import com.pls.invoice.domain.bo.AuditBO;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Service to work with invoice audit.
 *
 * @author Alexander Kirichenko
 */
public interface InvoiceAuditService {
    /**
     * Get data for invoice audit that includes shipments and adjustments.
     * 
     * @param userId
     *            id of current User.
     * 
     * @return data for invoice audit {@link AuditBO}
     */
    List<AuditBO> getInvoiceAuditData(Long userId);

    /**
     * Get data for price audit that includes shipments and adjustments.
     * 
     * @param userId
     *            ID of current user.
     * @return data for price audit {@link AuditBO}
     */
    List<AuditBO> getPriceAuditData(Long userId);

    /**
     * Update invoice approved field in loads by load ids.
     *
     * @param loadIds ids of load to update
     * @param approved new invoice approve status
     */
    void updateInvoiceApproved(List<Long> loadIds, boolean approved);

    /**
     * Update invoice approved field in adjustment by ids.
     *
     * @param adjustmentIds ids of adjustment to update
     * @param approved new invoice approve status
     */
    void updateAdjustmentInvoiceApproved(List<Long> adjustmentIds, boolean approved);

    /**
     * Returns a list depending on the transmitted ReasonType.
     * 
     * @return List for {@link LdBillAuditReasonCodeEntity}.
     */
    List<LdBillAuditReasonCodeEntity> getListAuditReasonCodeForReasonType();

    /**
     * Save ldBillingAuditReasonsEntity.
     * 
     * @param auditReason
     *            {@link AuditReasonBO}.
     */
    void saveNewManualReasonForLoad(AuditReasonBO auditReason);

    /**
     * Send load or adjustment to audit. if adjustment ID is present then adjustment will be send to price
     * audit otherwise it will be load.
     * 
     * @param auditReason
     *            contains parameters for Price Audit History,
     *            {@link com.pls.shipment.domain.FinancialAccessorialsEntity#getId()} and
     *            {@link LoadEntity#getId()}
     * 
     * @param code
     *            contains the code for Audit Records
     * @param note
     *            contains the note for Audit Records
     * 
     * @param isSendToInvoiceAudit
     *            true if the send to Invoice Audit.
     */
    void sendToAudit(List<AuditRecordsBO> auditReason, String code, String note, boolean isSendToInvoiceAudit);


    /**
     * Approve loads or adjustments. If adjustment ID is present then adjustment will be approved otherwise it
     * will be load.
     * 
     * @param auditRecords
     *            list of items to be approved
     * @throws ApplicationException
     *             if cost items check failed
     */
    void approveAudit(List<AuditReasonBO> auditRecords) throws ApplicationException;

    /**
     * Add new manual reason {@link Reasons#READY_FOR_CONSOLIDATED}. It need to be able to indicate when load
     * is ready to move to the Consolidated Invoice Screen.
     * 
     * @param auditRecords
     *            contains list of load which ready to move to the Consolidated Invoice Screen.
     */
    void setReadyForConsolidatedReason(List<AuditRecordsBO> auditRecords);

}
