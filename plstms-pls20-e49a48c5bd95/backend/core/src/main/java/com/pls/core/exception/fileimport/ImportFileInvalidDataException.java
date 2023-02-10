package com.pls.core.exception.fileimport;



/**
 * Signals that some import file contains some invalid data.
 * 
 * @author Viacheslav Krot
 * 
 */
public class ImportFileInvalidDataException extends ImportException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public ImportFileInvalidDataException(String message) {
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
    public ImportFileInvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
