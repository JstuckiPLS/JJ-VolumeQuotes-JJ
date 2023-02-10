package com.pls.core.shared;

import org.apache.commons.lang3.StringUtils;

/**
 * Required update revenue option enumeration.
 * 
 * @author Brichak Aleksandr
 *
 */
public enum UpdateRevenueOption {
    MARGIN_PERCENT("MP", "Margin %"),
    MARGIN_VALUE("MV", "Margin $"),
    TOTAL_REVENUE_AMOUNT("TR", "Total Revenue Amount"),
    UPDATE_USING_COST_DIFF("CD", "Update using Cost Difference"),
    INVOICE_WITHOUT_MARKUP("WM", "Invoice without markup");

    private String code;

    private String description;

    UpdateRevenueOption(String code, String description) {
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
     * Method returns {@link UpdateRevenueOption} by code.
     *
     * @param code
     *            {@link UpdateRevenueOption} code
     * @return {@link UpdateRevenueOption}
     */
    public static UpdateRevenueOption getByCode(String code) {
        for (UpdateRevenueOption disputeCost : UpdateRevenueOption.values()) {
            if (StringUtils.equals(disputeCost.code, code)) {
                return disputeCost;
            }
        }
        throw new IllegalArgumentException("Can not get update revenue option by code: " + code);
    }
}
