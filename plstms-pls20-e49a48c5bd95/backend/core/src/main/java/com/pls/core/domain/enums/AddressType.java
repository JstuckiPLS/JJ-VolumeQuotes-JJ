package com.pls.core.domain.enums;

/**
 * The Address Types.
 * 
 * @author Sergii Belodon
 */
public enum AddressType {
    SHIPPING("S", "Shipping"),
    FREIGHT_BILL("F", "Freight Bill To"),
    BOTH("B", "Both");

    private String code;
    private String value;

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    AddressType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * Get AddressType by code.
     *
     * @param code the code
     * @return AddressType
     */
    public static AddressType getByCode(String code) {
        for (AddressType addressType : AddressType.values()) {
            if (code.equals(addressType.code)) {
                return addressType;
            }
        }
        throw new IllegalArgumentException("Can not get Address Type by code: " + code);
    }

    /**
     * Get AddressType by name.
     *
     * @param name the name
     * @return AddressType
     */
    public static AddressType getByName(String name) {
        for (AddressType addressType : AddressType.values()) {
            if (name.equals(addressType.name())) {
                return addressType;
            }
        }
        throw new IllegalArgumentException("Can not get Address Type by name: " + name);
    }

    public static AddressType getDefault() {
        return AddressType.SHIPPING;
    }
}
