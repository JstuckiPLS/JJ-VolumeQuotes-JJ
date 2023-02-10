package com.pls.shipment.domain.bo;

import java.util.HashSet;
import java.util.Set;

import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ShipmentAlertEntity;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;

/**
 * LTL shipment plus alerts information for shipment tracking boards.
 * 
 * @author Viacheslav Krot
 */
public class ShipmentTrackingBoardBO {
    private final LoadEntity shipment;

    private final Set<ShipmentAlertType> alertTypes = new HashSet<ShipmentAlertType>();

    private boolean newAlert;

    /**
     * Construct object.
     * 
     * @param shipment
     *            wrapped shipment.
     */
    public ShipmentTrackingBoardBO(LoadEntity shipment) {
        this.shipment = shipment;
    }

    public LoadEntity getShipment() {
        return shipment;
    }

    /**
     * Add alert information to current shipment.
     *
     * @param alert alert that was created for this shipment
     */
    public void addAlert(ShipmentAlertEntity alert) {
        alertTypes.add(alert.getType());

        newAlert = alert.getStatus() == ShipmentAlertsStatus.ACTIVE;
    }

    public Set<ShipmentAlertType> getAlertTypes() {
        return alertTypes;
    }

    public boolean isNewAlert() {
        return newAlert;
    }
}
