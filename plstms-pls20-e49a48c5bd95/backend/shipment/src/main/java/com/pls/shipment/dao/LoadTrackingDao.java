package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.bo.LoadAuditBO;
import com.pls.shipment.domain.bo.LoadTrackingBO;

/**
 * DAO for {@link LoadTrackingEntity}.
 *
 * @author Mikhail Boldinov, 05/03/14
 */
public interface LoadTrackingDao extends AbstractDao<LoadTrackingEntity, Long> {

    /**
     * Get list of {@link LoadTrackingEntity} by shipmentId.
     *
     * @param shipmentId shipment id
     * @return list of found shipments
     */
    List<LoadTrackingBO> findShipmentTracking(Long shipmentId);

    /**
     * Get list of {@link LoadAuditBO} by shipmentId.
     * 
     * @param loadId
     *            load id
     * @return list of {@link LoadAuditBO}
     */
    List<LoadAuditBO> findShipmentAudit(Long loadId);
}
