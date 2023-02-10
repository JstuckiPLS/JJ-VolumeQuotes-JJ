package com.pls.shipment.service;

import java.util.List;

import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.bo.LoadAuditBO;
import com.pls.shipment.domain.bo.LoadTrackingBO;
import com.pls.shipment.domain.bo.ShipmentEventBO;

/**
 * Service to process load tracking information.
 * 
 * @author Gleb Zgonikov
 */
public interface ShipmentEventService {


    /**
     * Find events for specified shipment.
     * 
     * @param shipmentId Not <code>null</code> ID.
     * 
     * @return Not <code>null</code> {@link List}.
     */
    List<ShipmentEventBO> findShipmentEvents(Long shipmentId);

    /**
     * Find shipment tracking data provided by carrier.
     *
     * @param shipmentId Not <code>null</code> ID.
     * @return Not <code>null</code> {@link List}.
     */
    List<LoadTrackingBO> findShipmentTracking(Long shipmentId);

    /**
     * Find shipment audit data.
     * 
     * @param shipmentId
     *            Not <code>null</code> ID.
     * @return Not <code>null</code> {@link List}.
     */
    List<LoadAuditBO> findShipmentAudit(Long shipmentId);

    /**
     * Saves the load event.
     * 
     * @param entity
     *            event to be saved.
     */
    void save(LoadEventEntity entity);
}
