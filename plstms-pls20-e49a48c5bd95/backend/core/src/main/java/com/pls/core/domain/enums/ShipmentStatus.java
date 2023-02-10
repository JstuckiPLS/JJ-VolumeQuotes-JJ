package com.pls.core.domain.enums;

import org.apache.commons.lang3.ArrayUtils;


/**
 * Statuses for shipment orders.
 * 
 * @author Gleb Zgonikov
 */
public enum ShipmentStatus implements Status {


    /**
     * "Open" status.
     */
    OPEN("OPEN"),

    /**
     * "Blocked" status.
     */
    BOOKED("BOOKED"),

    /**
     * Pending Payment.
     */
    PENDING_PAYMENT("PENDING PAYMENT"),

    /**
     * "Dispatched" status.
     */
    DISPATCHED("DISPATCHED"),

    /**
     * "In transit" status.
     */
    IN_TRANSIT("IN TRANSIT"),

    /**
     * "Delivered" status.
     */
    DELIVERED("DELIVERED"),

    /**
     * "Canceled" status.
     */
    CANCELLED("CANCELLED"),

    /**
     * "Out for Delivery" status.
     */
    OUT_FOR_DELIVERY("OUT FOR DELIVERY");

    private final String description;

    private static final ShipmentStatus[] PICKEDUP_LIST = { ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED,
            ShipmentStatus.OUT_FOR_DELIVERY };

    private static final ShipmentStatus[] ACTIVE_LIST = {ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED, ShipmentStatus.IN_TRANSIT,
            ShipmentStatus.OUT_FOR_DELIVERY, ShipmentStatus.DELIVERED};

    ShipmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get true if status pickedUp.
     * @param status - ShipmentStatus
     * @return boolean
     */
    public static Boolean isPickedUp(ShipmentStatus status) {
        return ArrayUtils.contains(PICKEDUP_LIST, status);
    }

    /**
     * Returns <code>true</code> if argument is not {@link ShipmentStatus#OPEN} or {@link ShipmentStatus#CANCELLED}.
     * 
     * @param status - status to be verified
     * @return <code>true</code> if argument within active list, otherwise returns <code>false</code>.
     */
    public static Boolean isActive(ShipmentStatus status) {
        return ArrayUtils.contains(ACTIVE_LIST, status);
    }
}
