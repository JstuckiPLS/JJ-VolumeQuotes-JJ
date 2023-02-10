package com.pls.smc3.validation;

/**
 * Class handles the validations. 1. Throws validation exceptions while validating a web service request. 2.
 * Handles the response error codes from web service.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class WebServiceValidationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     */
    public WebServiceValidationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param   message the detail message.
     */
    public WebServiceValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     * @param  cause the cause.
     */
    public WebServiceValidationException(Throwable cause) {
        super(cause);
    }
}
