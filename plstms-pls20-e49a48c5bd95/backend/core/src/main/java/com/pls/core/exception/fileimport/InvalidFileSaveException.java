package com.pls.core.exception.fileimport;

/**
 * Signals that some unexpected failure happened during saving of invalid address import file.
 * 
 * @author Viacheslav Krot
 * 
 */
public class InvalidFileSaveException extends ImportException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public InvalidFileSaveException(String message) {
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
    public InvalidFileSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
