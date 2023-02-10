package com.pls.core.exception.fileimport;

import com.pls.core.exception.ApplicationException;

/**
 * Root for exceptions happening during importing addresses from file.
 * 
 * @author Viacheslav Krot
 * 
 */
public class ImportException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public ImportException(String message) {
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
    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
