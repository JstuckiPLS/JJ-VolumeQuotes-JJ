package com.pls.core.domain.enums;

/**
 * Required Field Shipment direction enumeration.
 * 
 * INBOUND is an order coming into the PLS customer facility OUTBOUND is an order leaving the PLS customer
 * facility BOTH is both.
 *
 * @author Alexander Nalapko
 */
public enum RequiredFieldShipmentDirection {
    INBOUND('I', "Inbound"), OUTBOUND('O', "Outbound"), BOTH('B', "Both");

    private char code;

    private String description;

    RequiredFieldShipmentDirection(char code, String description) {
        this.code = code;
        this.description = description;
    }

    public char getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method returns {@link RequiredFieldShipmentDirection} by code.
     *
     * @param code
     *            {@link RequiredFieldShipmentDirection} code
     * @return {@link RequiredFieldShipmentDirection}
     */
    public static RequiredFieldShipmentDirection getByCode(char code) {
        for (RequiredFieldShipmentDirection shipmentDirection : RequiredFieldShipmentDirection.values()) {
            if (shipmentDirection.code == code) {
                return shipmentDirection;
            }
        }
        throw new IllegalArgumentException("Can not get Shipment Direction by code: " + code);
    }

    /**
     * Compare {@link RequiredFieldShipmentDirection} with {@link ShipmentDirection}.
     * 
     * @param direction
     *            ShipmentDirection
     * @return boolean
     */
    public boolean isShipmentDirectionEquals(ShipmentDirection direction) {
        if (direction == null) {
            return false;
        }
        return this == RequiredFieldShipmentDirection.BOTH || String.valueOf(this.code).equals(direction.getCode());
    }
}
