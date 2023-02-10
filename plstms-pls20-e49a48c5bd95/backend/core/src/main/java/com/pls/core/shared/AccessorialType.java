package com.pls.core.shared;

/**
 * Enum class for accessorials type.
 *
 * @author Brichak Aleksandr
 *
 */
public enum AccessorialType {
    MISC("MS"),
    FUEL_SURCHARGE("FS"),
    SHIPPER_BASE_RATE("SRA"),
    CARRIER_BASE_RATE("CRA");

    private String type;

    public String getType() {
        return type;
    }

    AccessorialType(String type) {
        this.type = type;
    }
}
