package com.pls.shipment.service;

import com.pls.shipment.domain.LoadEntity;

/**
 * Service for operation on shipment alerts.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface ShipmentAlertService {
    /**
     * Find shipments in state of '30M', 'TDY', 'MSD', 'NDL' alert type and generate shipment alerts for them.
     * If there are alerts that are stale, they will be deactivated
     */
    void generateMinuteBasedShipmentAlerts();

    /**
     * Process shipment to find or disable existing alerts. Will create alert of 'Guaranteed' type if shipment has guaranteed condition,
     * and disable it otherwise. Also will disable all alerts if shipment is cancelled.
     *
     * @param shipment shipment to check
     */
    void processShipmentAlerts(LoadEntity shipment);

    /**
     * Check existing alerts for specified shipment, and deactivate them.
     *
     * @param shipment shipment to check
     */
    void deactivateAlerts(LoadEntity shipment);
}
