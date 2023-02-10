package com.pls.core.exception;

/**
 * Exception thrown for any errors while communicating between modules through JMS queues.
 * 
 * @author Pavani Challa
 */
public class InternalJmsCommunicationException extends ApplicationException {

    private static final long serialVersionUID = 8575520443946036319L;

    /**
     * Constructor.
     */
    public InternalJmsCommunicationException() {
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause exception cause
     */
    public InternalJmsCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
