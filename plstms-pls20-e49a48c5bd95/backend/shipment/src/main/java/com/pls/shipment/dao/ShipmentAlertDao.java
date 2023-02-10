package com.pls.shipment.dao;

import java.util.Collection;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.ShipmentAlertEntity;
import com.pls.shipment.domain.bo.ShipmentAlertType;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardAlertListItemBO;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;

/**
 * DAO for {@link ShipmentAlertEntity}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface ShipmentAlertDao extends AbstractDao<ShipmentAlertEntity, Long> {
    /**
     * Get available alerts of specified status for customer user.
     * 
     * @param userId
     *            id id of PLS User.
     * @param statuses
     *            statuses of alerts to find
     * @return list of available {@link ShipmentAlertEntity}
     */
    List<ShipmentTrackingBoardAlertListItemBO> getAlertsForUser(Long userId, ShipmentAlertsStatus... statuses);

    /**
     * Find existing alerts for specified shipment.
     *
     * @param shipmentIds list of shipment ids for which alerts should be found
     * @return list of {@link ShipmentAlertEntity}
     */
    List<ShipmentAlertEntity> findAlertsByShipment(List<Long> shipmentIds);

    /**
     * Acknowledge alerts for specified shipment.
     *
     * @param shipmentId id of shipment for which alerts need to be acknowledged
     * @param acknowledgedUserId id of user who acknowledge that alert
     */
    void acknowledgeAlerts(long shipmentId, long acknowledgedUserId);

    /**
     * Get alerts count of specified status for customer user.
     * 
     * @param userId
     *            id of PLS User.
     * @param status
     *            status of alert to find
     * @return list of available {@link ShipmentAlertEntity}
     */
    Long getAlertsCount(Long userId, ShipmentAlertsStatus status);

    /**
     * Get all shipment alerts of specified statuses.
     *
     * @param statuses statuses in which alerts need to be found
     * @return list of shipment alerts
     */
    List<ShipmentAlertEntity> getByStatus(ShipmentAlertsStatus... statuses);

    /**
     * Update status for shipment alerts.
     *
     * @param alertsIds shipment alerts ids
     * @param status status that need to be set
     */
    void updateStatus(Collection<Long> alertsIds, ShipmentAlertsStatus status);

    /**
     * Remove shipment alerts.
     *
     * @param shipmentId shipment id of alert
     * @param types remove only for alerts of specified types, if not specified, alerts of all types will be removed
     */
    void removeAlerts(Long shipmentId, ShipmentAlertType... types);

    /**
     * Find and generate time based alerts for shipments.
     */
    void generateNewTimeAlerts();

    /**
     * Delete obsolete time alerts.
     */
    void removeOutdatedTimeAlerts();

    /**
     * Get shipment alert for load with specified type if it exists.
     * 
     * @param shipmentId
     *            shipment id of alert
     * @param type
     *            {@link ShipmentAlertType}
     * @return {@link ShipmentAlertEntity} or <code>null</code>
     */
    ShipmentAlertEntity getShipmentAlert(Long shipmentId, ShipmentAlertType type);
}
