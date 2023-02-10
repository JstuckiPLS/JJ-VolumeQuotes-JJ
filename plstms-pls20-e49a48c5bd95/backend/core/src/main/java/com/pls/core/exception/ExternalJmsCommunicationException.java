package com.pls.core.exception;

/**
 * Exception thrown for any errors while communicating to external JMS server for Sterling integration.
 * 
 * @author Pavani Challa
 */
public class ExternalJmsCommunicationException extends ApplicationException {

    private static final long serialVersionUID = 4971347498721323899L;

    /**
     * Constructor.
     */
    public ExternalJmsCommunicationException() {
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause exception cause
     */
    public ExternalJmsCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
