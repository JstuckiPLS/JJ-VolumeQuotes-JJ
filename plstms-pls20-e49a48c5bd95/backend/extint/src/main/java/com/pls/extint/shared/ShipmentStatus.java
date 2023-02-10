package com.pls.extint.shared;

/**
 * Statuses for shipment orders. Added only the statuses that will be captured by Tracking API.
 * 
 * @author Pavani Challa
 */
public enum ShipmentStatus {
    /**
     * "In transit" status (Picked up).
     */
    IN_TRANSIT("PP"),

    /**
     * "Delivered" status (Confirmed Delivery).
     */
    DELIVERED("CD"),

    /**
     * "Out for Delivery" status (Destination Arrived).
     */
    OUT_FOR_DELIVERY("GA");

    private String code;

    ShipmentStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
