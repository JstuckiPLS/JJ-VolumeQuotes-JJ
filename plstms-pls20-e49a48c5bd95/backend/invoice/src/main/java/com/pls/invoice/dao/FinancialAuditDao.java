package com.pls.invoice.dao;

import java.util.List;

import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.invoice.domain.bo.AuditBO;

/**
 * DAO for auditing invoices.
 *
 * @author Sergey Kirichenko
 */
public interface FinancialAuditDao {

    /**
     * Check whether load has active cost details with cost items or not.
     *
     * @param loadId load's id
     * @return true if there is at least one cost item otherwise false.
     */
    boolean isLoadHasCostItems(Long loadId);

    /**
     * Check whether financial adjustment has cost items or not.
     *
     * @param adjustmentId adjustment's id
     * @return true if there is at least one cost item otherwise false.
     */
    boolean isAdjustmentHasCostItems(Long adjustmentId);

    /**
     * Update financial status for adjustment.
     *
     * @param adjustmentId id of adjustment to update
     * @param adjustmentStatus new financial status for adjustment
     */
    void updateAdjustmentFinancialStatus(Long adjustmentId, ShipmentFinancialStatus adjustmentStatus);

    /**
     * Update invoice approved field in loads by load ids.
     *
     * @param loadIds
     *            ids of load to update
     * @param approved
     *            new invoice approve status
     */
    void updateInvoiceApproved(List<Long> loadIds, boolean approved);

    /**
     * Update invoice approved field in financial adjustments by ids.
     *
     * @param adjustmentIds
     *            ids of adjustments to update
     * @param approved
     *            new invoice approve status
     */
    void updateAdjustmentInvoiceApproved(List<Long> adjustmentIds, boolean approved);

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
}
