package com.pls.core.shared;

import org.apache.commons.lang3.StringUtils;

/**
 * Enum class for Statuses - Active / Inactive and infuture if needed - Hold/Pending.
 *
 * @author Hima Bindu Challa
 *
 */
public enum Status {
    ACTIVE("A"),
    INACTIVE("I");

    private String code;

    public String getCode() {
        return code;
    }

    Status(String code) {
        this.code = code;
    }

    /**
     * Method returns {@link Status} by string.
     * @param value - string
     * @return {@link Status}
     */
    public static Status getStatusByValue(String value) {
        String inValue = StringUtils.deleteWhitespace(value);

        for (Status status : Status.values()) {
            if (StringUtils.equals(status.code, inValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Can not get Status by value: " + value);
    }

    /**
     * Get character code for status value.
     * 
     * @return "A" for Active and "I" for inactive
     */
    public String getStatusCode() {
        return code;
    }
}
