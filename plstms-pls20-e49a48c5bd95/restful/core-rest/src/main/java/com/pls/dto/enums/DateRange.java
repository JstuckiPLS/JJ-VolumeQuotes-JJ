package com.pls.dto.enums;

/**
 * Relative Date Range values. Used as part of {@link com.pls.dto.query.DateRangeQueryDTO}.
 * {@link com.pls.dto.query.DateRangeQueryDTO#dateRange} field uses names of this values
 * (for example: someDateRangeQueryDTO.setDateRange(DateRange.TODAY.name());).
 * <p/>
 * {@link DateRange#label} used to display DateRange values on UI side. DateRange labels are currently not support
 * localization, it's values used directly.
 *
 * @author Artem Arapov
 */
public enum DateRange {
    TODAY("Today"),
    WEEK("This week"),
    MONTH("This month"),
    QUARTER("This quarter"),
    YEAR("This year"),
    DEFAULT("Manual select");

    private String label;

    DateRange(String label) {
        this.label = label;
    }

    /**
     * Returns not localized label of current DateRange value to display on UI side.
     *
     * @return string representation of DateRange value.
     */
    public String getLabel() {
        return label;
    }
}
