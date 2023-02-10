package com.pls.core.shared;

/**
 * Enum class for Countries - USA, Canada and Mexico. We dont need to add other countries as of now. Will add as per
 * requirement.
 *
 * @author Hima Bindu Challa
 *
 */
public enum Countries {
    USA("USA"),
    MEXICO("MEX"),
    CANADA("CAN");

    private String code;

    public String getCode() {
        return code;
    }

    Countries(String code) {
        this.code = code;
    }
}
