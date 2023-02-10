package com.pls.core.shared;

import org.apache.commons.lang3.StringUtils;

/**
 * Required Dispute Cost enumeration.
 * 
 * @author Brichak Aleksandr
 *
 */
public enum DisputeCost {
    ACCOUNT_EXEC("AE", "Account Exec"), FINANCE("F", "Finance Review"), RESOLVED_NEW_VB("R", "Resolved - New VB");

    private String code;

    private String description;

    DisputeCost(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method returns {@link DisputeCost} by code.
     *
     * @param code
     *            {@link DisputeCost} code
     * @return {@link DisputeCost}
     */
    public static DisputeCost getByCode(String code) {
        for (DisputeCost disputeCost : DisputeCost.values()) {
            if (StringUtils.equals(disputeCost.code, code)) {
                return disputeCost;
            }
        }
        throw new IllegalArgumentException("Can not get Dispute Cost by code: " + code);
    }
}
