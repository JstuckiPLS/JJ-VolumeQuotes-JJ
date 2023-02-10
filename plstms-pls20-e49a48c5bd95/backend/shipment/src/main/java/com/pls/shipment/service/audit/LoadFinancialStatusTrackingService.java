package com.pls.shipment.service.audit;

import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.shipment.domain.LoadEntity;

/**
 * The Interface LoadTrackingService.
 * 
 * @author Sergii Belodon
 */
public interface LoadFinancialStatusTrackingService {

    /**
     * Logs updating of {@link LoadEntity} financial status.
     *
     * @param auditReason - parameters for logging.
     * @param loadStatus the load status
     * @throws Throwable that pjp is throwing.
     */
    void logLoadFinancialStatusEvent(AuditReasonBO auditReason, ShipmentFinancialStatus loadStatus);

}
