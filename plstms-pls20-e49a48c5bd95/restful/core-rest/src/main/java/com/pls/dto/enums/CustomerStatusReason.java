package com.pls.dto.enums;


/**
 * Customer status reason.
 * 
 * @author Denis Zhupinsky
 * 
 */
public enum CustomerStatusReason {
    CUSTOMER_REQUEST("CUST"),
    NO_ACTIVITY("NOACTIV"),
    OUT_OF_BUSINESS("OUTOFBUS"),
    ACTIVITY_REQUESTED("ACTOPREQ"),
    ENROLLMENT_ACCEPTED("ACCEPTED"),
    CREDIT_HOLD("CRHLD"),
    TAX_ID_EMPTY("TAXABS");

    private String value;

    /**
     * Constructor for status reason.
     * 
     * @param value
     *            field
     */
    CustomerStatusReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get enum value by it's value field.
     * 
     * @param value
     *            field
     * @return {@link CustomerStatusReason} value or null if value not found
     */
    public static CustomerStatusReason getByValue(String value) {
        if (value != null) {
            for (CustomerStatusReason reason : CustomerStatusReason.values()) {
                if (reason.value.equalsIgnoreCase(value)) {
                    return reason;
                }
            }
        }
        return null;
    }
}
