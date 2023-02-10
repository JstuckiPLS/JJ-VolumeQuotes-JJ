package com.pls.shipment.service;

import java.util.List;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardAlertListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardBookedListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardListItemBO;

/**
 * Service getting shipments for tracking board. Gets shipments in specific statuses, with active alerts, low
 * benefits etc.
 * 
 * @author Viacheslav Krot
 */
public interface ShipmentTrackingBoardService {
    /**
     * Get shipments with alerts for the logged user.
     * 
     * @return list of shipments with alerts.
     */
    List<ShipmentTrackingBoardAlertListItemBO> getAlertShipments();

    /**
     * Acknowledge alerts for specified shipment.
     * 
     * @param shipmentId
     *            id of shipment for which alerts should be acknowledged
     */
    void acknowledgeAlerts(long shipmentId);

    /**
     * Get count of active alerts.
     * 
     * @return count of active alerts
     * 
     */
    Long countOfActiveAlerts();

    /**
     * Get shipments in BOOKED status.
     * 
     * @param personId
     *            User ID.
     * 
     * @return list of shipments in BOOKED status.
     */
    List<ShipmentTrackingBoardBookedListItemBO> getBookedShipments(Long personId);

    /**
     * Get shipments in OPEN status.
     * 
     * @param personId
     *            User ID.
     * @param search
     *            search
     * @return list of shipments in OPEN status.
     */
    List<ShipmentTrackingBoardListItemBO> getOpenShipments(Long personId, RegularSearchQueryBO search);

    /**
     * Get undelivered shipments.
     * 
     * @return undelivered shipments.
     */
    List<ShipmentTrackingBoardListItemBO> getUndeliveredShipments();

    /**
     * Get unbilled shipments.
     * 
     * @param personId
     *            User ID.
     * @return unbilled shipments.
     */
    List<ShipmentListItemBO> getUnbilledShipments(Long personId);

    /**
     * Get all shipments.
     * 
     * @param search
     *            search
     * @param userId
     *            id of user
     * @return list of shipments.
     */
    List<ShipmentListItemBO> getAllShipments(RegularSearchQueryBO search, Long userId);

    /**
     * Get hold shipments.
     * 
     * @return hold shipments.
     */
    List<ShipmentListItemBO> getHoldShipments();
}
