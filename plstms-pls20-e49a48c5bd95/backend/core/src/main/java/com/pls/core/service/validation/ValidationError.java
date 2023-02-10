package com.pls.core.service.validation;

/**
 * Enumeration of all application validation errors.
 * 
 * @author Viacheslav Krot
 */
public enum ValidationError {
    IS_NULL,
    IS_EMPTY,
    GREATER_THAN,
    LESS_THAN,
    UNIQUE,
    VERSION,
    FORMAT,
    IS_NOT_VALID;
}
