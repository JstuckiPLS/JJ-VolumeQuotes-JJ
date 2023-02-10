package com.pls.core.exception.fileimport;

/**
 * Exception should be thrown when actual records in file exceeds expected maximum limit.
 *
 * @author Gleb Zgonikov
 *
 */
public class ImportRecordsNumberExceededException extends ImportException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message
     *            the detail message.
     */
    public ImportRecordsNumberExceededException(String message) {
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
    public ImportRecordsNumberExceededException(String message, Throwable cause) {
        super(message, cause);
    }

}
