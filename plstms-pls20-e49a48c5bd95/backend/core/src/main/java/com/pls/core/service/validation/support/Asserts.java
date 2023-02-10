package com.pls.core.service.validation.support;

import com.pls.core.service.validation.ValidationError;

/**
 * Helper class containing common methods for data validation. For each failing assertion appropriate error
 * code and constraint name is put to ValidationContext.
 * 
 * @author Viacheslav Krot
 */
public class Asserts {
    private final ValidationContext context;

    /**
     * Constructor.
     * 
     * @param context
     *            field
     */
    public Asserts(ValidationContext context) {
        this.context = context;
    }

    /**
     * Check if value is not null.
     * 
     * @param value
     *            value to check.
     * @param constraintName
     *            constraint name to fail.
     */
    public void notNull(Object value, String constraintName) {
        if (value == null) {
            context.fail(constraintName, ValidationError.IS_NULL);
        }
    }

    /**
     * Check if string is not null and not empty.
     * 
     * @param value
     *            value to check.
     * @param constraintName
     *            constraint name to fail.
     */
    public void notEmpty(String value, String constraintName) {
        if (value == null || value.isEmpty()) {
            context.fail(constraintName, ValidationError.IS_EMPTY);
        }
    }

    /**
     * Check if value is true.
     * 
     * @param value
     *            value to check.
     * @param constraintName
     *            constraint name to fail.
     * @param error
     *            the error.
     */
    public void isTrue(boolean value, String constraintName, ValidationError error) {
        if (!value) {
            context.fail(constraintName, error);
        }
    }

    /**
     * Unconditionally fail validation.
     * 
     * @param constraintName
     *            constraint name to fail.
     * @param error
     *            the error.
     */
    public void fail(String constraintName, ValidationError error) {
        context.fail(constraintName, error);
    }
}
