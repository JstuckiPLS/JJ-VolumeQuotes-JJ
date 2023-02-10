package com.pls.shipment.domain.bo;

/**
 * Shipment alert types.
 *
 * @author Viacheslav Krot
 */
public enum  ShipmentAlertType {
    PICKUP_TODAY("TDY"),
    MISSED_PICKUP("MSD"),
    MISSED_DELIVERY("NDL"),
    THIRTY_MIN_TO_PICKUP("30M"),
    GUARANTEED_SERVICE("GRN"),
    DELIVERY_DATE_WO_PICKUP_DATE("DWP");

    private String type;

    ShipmentAlertType(String type) {
        this.type = type;
    }

    /**
     * Get ShipmentAlertType enum object by String type.
     *
     * @param type type to find
     * @return instance of current enum
     */
    public static ShipmentAlertType getShipmentAlertType(String type) {
        for (ShipmentAlertType shipmentAlertType : values()) {
            if (shipmentAlertType.type.equals(type)) {
                return shipmentAlertType;
            }
        }
        throw new IllegalArgumentException("Cannot get shipmentAlertType object by type: '" + type + "'");
    }

    public String getType() {
        return type;
    }
}
