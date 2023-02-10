package com.pls.shipment.domain.enums;

/**
 * Status of shipments alerts entities.
 *
 * @author Denis Zhupinsky (Team International)
 */
public enum ShipmentAlertsStatus {
    ACTIVE("A"), ACKNOWLEDGED("K"), INACTIVE("I");

    private String status;

    ShipmentAlertsStatus(String status) {
        this.status = status;
    }

    /**
     * Get ShipmentAlertsStatus enum object by String status.
     *
     * @param status status to find
     * @return instance of current enum
     */
    public static ShipmentAlertsStatus getShipmentAlertsStatus(String status) {
        for (ShipmentAlertsStatus shipmentAlertsStatus : values()) {
            if (shipmentAlertsStatus.status.equals(status)) {
                return shipmentAlertsStatus;
            }
        }
        throw new IllegalArgumentException("Cannot get ShipmentAlertsStatus object by status: '" + status + "'");
    }

    public String getStatus() {
        return status;
    }
}
