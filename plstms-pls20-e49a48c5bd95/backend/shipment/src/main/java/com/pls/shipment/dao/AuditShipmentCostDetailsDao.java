package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;

/**
 * DAO for {@link AuditShipmentCostDetailsEntity}.
 *
 * @author Brichak Aleksandr
 */
public interface AuditShipmentCostDetailsDao extends AbstractDao<AuditShipmentCostDetailsEntity, Long> {

    /**
     * Get audit shipment cost detail by load id.
     *
     * @param loadId
     *            - id of load
     * @return list of {@link AuditShipmentCostDetailsEntity}
     */
    AuditShipmentCostDetailsEntity findAuditShipmentCostDetailsByLoadId(Long loadId);
}
