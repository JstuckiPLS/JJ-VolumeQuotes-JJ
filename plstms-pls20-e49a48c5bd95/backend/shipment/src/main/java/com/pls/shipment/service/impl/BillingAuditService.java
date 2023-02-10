package com.pls.shipment.service.impl;

import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.shipment.domain.LoadEntity;

/**
 * Service for billing audit issues.
 * 
 * @author Aleksandr Leshchenko
 */
public interface BillingAuditService {

    /**
     * Updates billing audit reasons for load and send email reasons if financial status's been changed.
     * 
     * @param loadId
     *            loadId which use to get loadEntity {@link LoadEntity#getId()}
     * 
     * @param status
     *            status which load had when came in response and before was likely to be modified {@link ShipmentFinancialStatus}
     * 
     */
    void updateBillingAuditReasonForLoad(Long loadId, ShipmentFinancialStatus status);

    /**
     * Updates billing audit reasons for load and send email reasons if financial status's been changed.
     * 
     * @param load
     *            loadId which use to get loadEntity {@link LoadEntity}
     * 
     * @param previousFinStatus
     *            status which load had when came in response and before was likely to be modified {@link ShipmentFinancialStatus}
     */
    void updateBillingAuditReasonForLoad(LoadEntity load, ShipmentFinancialStatus previousFinStatus);

    /**
     * Return Reason for Load.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @throws EntityNotFoundException
     *             if can't find entity
     * @return billing audit reason
     */
    String getBillingAuditReasonForLoad(Long loadId) throws EntityNotFoundException;
}

