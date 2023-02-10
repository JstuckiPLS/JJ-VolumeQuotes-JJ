package com.pls.shipment.service.billing.audit;

import java.util.List;

import com.pls.core.shared.Reasons;
import com.pls.shipment.domain.LoadEntity;

/**
 * Service for Invoice Audit of Sid Harvey Customer.
 * 
 * @author Brichak Aleksandr
 */
public interface SidHarveyAuditReasonService {

    /**
     * Get billing audit Reasons for Sid Harvey Customer.
     * 
     * @param load
     *            {@link LoadEntity}
     * @return list of audit issues for specified load.
     */
    List<Reasons> getBillingAuditReasonForSidHarvey(LoadEntity load);

    /**
     * If Sid Harvey id return true.
     * 
     * @param id
     *            - Load id
     * @return true if Sid Harvey or false if not.
     */
    boolean isSidHarvey(Long id);

}
