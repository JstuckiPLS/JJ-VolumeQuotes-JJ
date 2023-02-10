package com.pls.core.service.validation;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.exception.ApplicationException;

/**
 * Signals that validation checks failed. Contains map of failed constraints. Constraint name is the key of
 * the map, {@link ValidationError} - value.
 * 
 * @author Viacheslav Krot
 */
public class ValidationException extends ApplicationException {

    private static final long serialVersionUID = 590129670513005390L;

    private final Map<String, ValidationError> errors;

    private String errorMsg = StringUtils.EMPTY;

    /**
     * Get validation errors.
     * 
     * @return validation errors.
     */
    public Map<String, ValidationError> getErrors() {
        return errors;
    }

    /**
     * Constructor.
     * 
     * @param errors
     *            Not <code>null</code> {@link Map}.
     */
    public ValidationException(Map<String, ValidationError> errors) {
        super("Validation checks failed");
        this.errors = errors;
    }

    /**
     * Constructor.
     * 
     * @param errors
     *            Not <code>null</code> {@link Map}.
     * @param errorMsgs
     *            Error messages that are to be sent with the exception
     */
    public ValidationException(Map<String, ValidationError> errors, String... errorMsgs) {
        super("Validation checks failed");
        this.errors = errors;

        StringBuilder errorMessages = new StringBuilder();
        for (String msg : errorMsgs) {
            errorMessages.append(msg);
            errorMessages.append('\n');
        }
        this.errorMsg = errorMessages.toString();
    }

    @Override
    public String toString() {
        return "ValidationException: errors:" + errors;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String getLocalizedMessage() {
        return this.toString();
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
