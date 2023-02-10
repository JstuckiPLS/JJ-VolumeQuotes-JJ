package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;

/**
 * DAO for {@link LdBillAuditReasonCodeEntity}.
 * 
 * @author Brichak Aleksandr
 * 
 */
public interface BillingAuditReasonCodeDao extends AbstractDao<LdBillAuditReasonCodeEntity, String> {

    /**
     * Returns a list depending on the transmitted ReasonType.
     * 
     * @return List for {@link LdBillAuditReasonCodeEntity}.
     */
    List<LdBillAuditReasonCodeEntity> getReasonCodeEntityForReasonType();

    /**
     * Return reason for code(id).
     * 
     * @param code
     *            Reason Code - id.
     * 
     * @return {@link LdBillAuditReasonCodeEntity}.
     */
    LdBillAuditReasonCodeEntity getReasonEntityForReasonCode(String code);

}
