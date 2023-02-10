package com.pls.invoice.domain.bo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Enum for possible Invoice Statuses.
 * 
 * @author Aleksandr Leshchenko
 */
public enum InvoiceReleaseStatus {
    SUCCESS("S"),
    FAILURE("F"),
    CANCELLED("C"),
    REPROCESS("R");

    private String code;

    InvoiceReleaseStatus(String code) {
        this.code = code;
    }

    /**
     * Method returns {@link InvoiceReleaseStatus} by string.
     * 
     * @param value
     *            - string
     * @return {@link InvoiceReleaseStatus}
     */
    public static InvoiceReleaseStatus getStatusByValue(String value) {
        String inValue = StringUtils.deleteWhitespace(value);

        for (InvoiceReleaseStatus status : InvoiceReleaseStatus.values()) {
            if (StringUtils.equals(status.code, inValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Can not get Invoice Status by value: " + value);
    }

    /**
     * Get character code for status value.
     * 
     * @return "S" for Success, "F" for failure, "C" for Cancelled
     */
    public String getStatusCode() {
        return code;
    }
}
