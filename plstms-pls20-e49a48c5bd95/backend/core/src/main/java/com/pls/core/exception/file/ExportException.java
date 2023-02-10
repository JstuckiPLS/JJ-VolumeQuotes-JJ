package com.pls.core.exception.file;

import com.pls.core.exception.ApplicationException;

/**
 * Exception that can be thrown while exporting to file.
 * 
 * @author Stas Norochevskiy
 *
 */
public class ExportException  extends ApplicationException {

    private static final long serialVersionUID = 1075624384288562837L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public ExportException(String message) {
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
    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
