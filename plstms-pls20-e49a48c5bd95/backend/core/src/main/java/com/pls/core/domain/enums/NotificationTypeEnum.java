package com.pls.core.domain.enums;

/**
 * The Enum NotificationTypeEnum.
 * @author Sergii Belodon
 */
public enum NotificationTypeEnum {
    DISPATCHED("Dispatched"),
    PICK_UP("Picked Up"),
    OUT_FOR_DELIVERY("Out For Delivery"),
    DELIVERED("Delivered"),
    DETAILS("Details"),
    AUDIT("Audit");

    private String name;

    NotificationTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
