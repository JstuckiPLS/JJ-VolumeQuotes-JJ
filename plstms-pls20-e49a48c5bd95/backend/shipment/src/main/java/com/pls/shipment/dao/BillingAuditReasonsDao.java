package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LdBillingAuditReasonsEntity;

/**
 * DAO for {@link LdBillingAuditReasonsEntity}.
 * 
 * @author Brichak Aleksandr
 * 
 */
public interface BillingAuditReasonsDao extends AbstractDao<LdBillingAuditReasonsEntity, Long> {

    /**
     * Update LdBillingAuditReasons status for Load.
     * 
     * @param loadId
     *            Load's id.
     */
    void deactivateBillingAuditReasonsStatusForLoad(Long loadId);

    /**
     * Deactivate {@link LdBillingAuditReasonsEntity} of manual type for Load.
     * 
     * @param loadId
     *            Load's id.
     */
    void deactivateManualBillingAuditReasonsStatusForLoad(Long loadId);

    /**
     * Update LdBillingAuditReasons status for Adjustment.
     * 
     * @param adjustmentId
     *            Adjustment's id.
     */
    void deactivateAuditReasonsStatusForAdjustment(Long adjustmentId);

    /**
     * Create {@link LdBillingAuditReasonsEntity} without saving.
     * 
     * @param reasonCd
     *            {@link LdBillingAuditReasonsEntity#getReasonCd(String)}.
     * @param loadId
     *            {@link LdBillingAuditReasonsEntity#getLoadId(Long)}.
     * @param comment
     *            {@link LdBillingAuditReasonsEntity#getComment()}.
     * @param finAccDetailId
     *            {@link LdBillingAuditReasonsEntity#getFinancialAccessorialDetailId()}.
     */
    void createAndSave(String reasonCd, Long loadId, String comment, Long finAccDetailId);

    /**
     * Create and Save {@link LdBillingAuditReasonsEntity}.
     * 
     * @param reasonCd
     *            {@link LdBillingAuditReasonsEntity#getReasonCd(String)}.
     * @param loadId
     *            {@link LdBillingAuditReasonsEntity#getLoadId(Long)}.
     * @param comment
     *            {@link LdBillingAuditReasonsEntity#getComment()}.
     * @param finAccDetailId
     *            {@link LdBillingAuditReasonsEntity#getFinancialAccessorialDetailId()}.
     * @return {@link LdBillingAuditReasonsEntity}
     */
    LdBillingAuditReasonsEntity create(String reasonCd, Long loadId, String comment, Long finAccDetailId);

}
