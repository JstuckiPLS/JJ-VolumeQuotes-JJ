package com.pls.core.shared;

/**
 * Enum class for Statuses - Yes / No.
 *
 * @author Viacheslav Krot.
 */
public enum StatusYesNo {
    //do not change the order of constants
    NO("N"),
    YES("Y");

    private String code;

    public String getCode() {
        return code;
    }

    StatusYesNo(String code) {
        this.code = code;
    }

}
