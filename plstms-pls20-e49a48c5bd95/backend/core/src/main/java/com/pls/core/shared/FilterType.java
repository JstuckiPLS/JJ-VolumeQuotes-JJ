package com.pls.core.shared;

/**
 * Type of filtering value.
 * 
 * @author Maxim Medvedev
 */
public enum FilterType {
    /**
     * String is date or date range value.
     */
    DATES,

    /**
     * String is exactly the same as target value.
     */
    EXACT,

    /**
     * Specific type is processed by query builder implementation.
     */
    CUSTOM,

    /**
     * String is wildcard expression or part target value.
     */
    LIKE
}
