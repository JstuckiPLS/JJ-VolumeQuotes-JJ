package com.pls.core.domain.enums;

/**
 * The Enum AddressNotificationDirectionEnum.
 * 
 * @author Sergii Belodon
 */
public enum AddressNotificationDirectionEnum {
    ORIGIN("O"),
    DESTINATION("D"),
    BOTH("B");

    String code;

    /**
     * Instantiates a new address notification direction enum.
     *
     * @param code the code
     */
    AddressNotificationDirectionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Gets the by code.
     *
     * @param code the code
     * @return the by code
     */
    public static AddressNotificationDirectionEnum getByCode(String code) {
        for (AddressNotificationDirectionEnum value : AddressNotificationDirectionEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
