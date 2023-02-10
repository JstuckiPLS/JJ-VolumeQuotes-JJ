package com.pls.documentmanagement.exception;

import com.pls.core.exception.ApplicationException;

/**
 * Exception that throws when document reading fails.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class DocumentReadException extends ApplicationException {
    private static final long serialVersionUID = -1622629331561974699L;

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause cause of exception
     */
    public DocumentReadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     */
    public DocumentReadException(String message) {
        super(message);
    }
}
