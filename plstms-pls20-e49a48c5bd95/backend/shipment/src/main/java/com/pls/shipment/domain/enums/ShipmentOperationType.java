package com.pls.shipment.domain.enums;

/**
 * Shipment Operation type enumeration.
 *
 * @author Mikhail Boldinov, 23/10/13
 */
public enum ShipmentOperationType {
    TENDER("00"), CANCELLATION("01"), UPDATE("04");

    private String code;

    ShipmentOperationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
