package com.pls.documentmanagement.exception;

import com.pls.core.exception.ApplicationException;

/**
 * Exception that throws when document saving fails.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class DocumentSaveException extends ApplicationException {
    private static final long serialVersionUID = 6134341826458223058L;

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause cause of exception
     */
    public DocumentSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     */
    public DocumentSaveException(String message) {
        super(message);
    }
}
