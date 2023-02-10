package com.pls.core.domain.enums;

/**
 * Accessorial visibility enumeration.
 *
 * @author Mikhail Boldinov, 13/05/13
 */
public enum LtlAccessorialVisibility {
    SHIPPER("S"), CARRIER("C");

    private String code;

    LtlAccessorialVisibility(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Gets {@link LtlAccessorialVisibility} object by DB code.
     *
     * @param code accessorial visibility to find
     * @return instance of current enum
     */
    public static LtlAccessorialVisibility getAccessorialVisibilityByCode(String code) {
        for (LtlAccessorialVisibility accessorialVisibility : values()) {
            if (accessorialVisibility.code.equals(code)) {
                return accessorialVisibility;
            }
        }

        throw new IllegalArgumentException(String.format("Cannot get LtlAccessorialVisibility object by value: '%s'", code));
    }
}
