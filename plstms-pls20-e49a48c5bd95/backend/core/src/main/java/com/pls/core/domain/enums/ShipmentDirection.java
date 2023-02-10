package com.pls.core.domain.enums;

/**
 * Shipment direction enumeration.
 * <p/>
 * Two values are possible: INBOUND and OUTBOUND. OUTBOUND by default.
 * INBOUND is an order coming into the PLS customer facility
 * OUTBOUND is an order leaving the PLS customer facility
 *
 * @author Mikhail Boldinov, 30/10/13
 */
public enum ShipmentDirection {
    INBOUND("I", "Inbound"), OUTBOUND("O", "Outbound");

    private String code;

    private String description;

    ShipmentDirection(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method returns {@link ShipmentDirection} by code.
     *
     * @param code {@link ShipmentDirection} code
     * @return {@link ShipmentDirection}
     */
    public static ShipmentDirection getByCode(String code) {
        for (ShipmentDirection shipmentDirection : ShipmentDirection.values()) {
            if (shipmentDirection.code.equals(code)) {
                return shipmentDirection;
            }
        }
        throw new IllegalArgumentException("Can not get Shipment Direction by code: " + code);
    }
}
