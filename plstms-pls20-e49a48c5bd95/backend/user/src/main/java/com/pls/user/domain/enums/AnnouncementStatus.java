package com.pls.user.domain.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Announcement status type enumeration.
 * 
 * @author Nalapko Alexander
 *
 */
public enum AnnouncementStatus {
    UNPUBLISHED("U"),
    PUBLISHED("P"),
    INACTIVE("I"),
    CANCEL("C");

    private String code;

    public String getCode() {
        return code;
    }

    AnnouncementStatus(String code) {
        this.code = code;
    }

    /**
     * Method returns {@link AnnouncementStatus} by string.
     * @param value - string
     * @return {@link AnnouncementStatus}
     */
    public static AnnouncementStatus getStatusByValue(String value) {
        String inValue = StringUtils.deleteWhitespace(value);

        for (AnnouncementStatus status : AnnouncementStatus.values()) {
            if (StringUtils.equals(status.code, inValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Can not get Status by value: " + value);
    }

    /**
     * Get character code for status value.
     * 
     * @return "P" for Published and "I" for inactive and "C" for canceled and "U" for Unpublished
     */
    public String getStatusCode() {
        return code;
    }
}
