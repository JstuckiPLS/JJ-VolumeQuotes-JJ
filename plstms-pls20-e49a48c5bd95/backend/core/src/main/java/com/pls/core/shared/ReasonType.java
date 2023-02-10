package com.pls.core.shared;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum class for Reason Type - Auto, Invoice, Price and Required Identifiers.
 * 
 * @author Brichak Aleksandr
 * 
 */
public enum ReasonType {
    AUTO("A"), INVOICE("I"), PRICE("P"), REQUIRED_IDENTIFIERS("R");

    private String code;

    public String getCode() {
        return code;
    }

    ReasonType(String code) {
        this.code = code;
    }

    /**
     * Method returns {@link ReasonType} by string.
     * @param value - string
     * @return {@link ReasonType}
     */
    public static ReasonType getTypeByValue(String value) {
        String inValue = StringUtils.deleteWhitespace(value);

        for (ReasonType type : ReasonType.values()) {
            if (StringUtils.equals(type.code, inValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Can not get  Reason Type by value: " + value);
    }

    /**
     * Get character code for Reason type value.
     * 
     * @return "A" for Auto and "I" for Invoice and "P" for Price and "R" for Required Identifiers
     */
    @JsonValue
    public String geReasonTypeCode() {
        return code;
    }
}
