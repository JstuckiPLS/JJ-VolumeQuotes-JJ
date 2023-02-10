package com.pls.core.service.validation.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.service.validation.ValidationError;

/**
 * Validation context holds validation errors raised by validators. The violations information is stored in
 * the map. The map's key is <code>constraintName</code>, value - validation error code.
 * 
 * @author Viacheslav Krot
 * 
 */
public class ValidationContext {
    private Map<String, ValidationError> errors = new HashMap<String, ValidationError>();

    /**
     * Check if any validation errors were found.
     * 
     * @return true if any validation errors were found.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Register new validation error. Adds error to existing validation errors.
     * 
     * @param constraintName
     *            failed constraint.
     * @param error
     *            error code.
     */
    public void fail(String constraintName, ValidationError error) {
        errors.put(constraintName, error);
    }

    /**
     * Get validation errors. Client code should not attempt to modify returned map, as it has no effects and
     * may throw exceptions.
     * 
     * @return validation errors.
     */
    public Map<String, ValidationError> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    /**
     * Add some prefix followed by dot to existing errors.
     * 
     * @param prefix
     *            prefix value.
     */
    public void prefixErrors(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("Prefix should not be null or empty");
        }
        HashMap<String, ValidationError> newErrors = new HashMap<String, ValidationError>(errors.size());
        for (Map.Entry<String, ValidationError> e : errors.entrySet()) {
            newErrors.put(prefix + "." + e.getKey(), e.getValue());
        }
        errors = newErrors;
    }

    /**
     * Add all errors from another context.
     * 
     * @param context
     *            another context.
     */
    public void addAllErrors(ValidationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("COntext may not by null");
        }

        if (context == this) {
            return;
        }

        errors.putAll(context.getErrors());
    }

    /**
     * Remove all errors from context.
     */
    public void clearContext() {
        errors.clear();
    }
}
