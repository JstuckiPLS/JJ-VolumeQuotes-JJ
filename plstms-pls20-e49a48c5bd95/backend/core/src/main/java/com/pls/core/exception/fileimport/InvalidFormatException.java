package com.pls.core.exception.fileimport;

/**
 * Indicates that input file has unexpected format.
 * 
 * @author Artem Arapov
 *
 */
public class InvalidFormatException extends ImportException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public InvalidFormatException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public InvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
