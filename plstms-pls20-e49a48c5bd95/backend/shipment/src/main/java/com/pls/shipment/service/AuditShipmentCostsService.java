package com.pls.shipment.service;

import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.AuditShipmentCostsBO;
import com.pls.shipment.domain.bo.AuditShipmentCostsOptionsBO;

/**
 * Audit shipment costs Service.
 * 
 * @author Brichak Aleksandr
 */
public interface AuditShipmentCostsService {

    /**
     * Method saves audit shipment costs options and Accessorials for Load.
     * 
     * @param shipmentId
     *            shipment identifier
     * @param auditCostBO
     *            - {@link AuditShipmentCostsOptionsBO}
     * @param auditShipmentCostDetails
     *            {@link AuditShipmentCostDetailsEntity}
     * @return single {@link LoadEntity}.
     */
    LoadEntity saveAuditShipmentCostsOptions(Long shipmentId, AuditShipmentCostsOptionsBO auditCostBO,
            AuditShipmentCostDetailsEntity auditShipmentCostDetails);

    /**
     * Method get Invoice Additional Details.
     * 
     * @param loadId
     *            shipment identifier
     * @return {@link AuditShipmentCostsBO}
     */
    AuditShipmentCostDetailsEntity getInvoiceAdditionalDetails(Long loadId);
}
