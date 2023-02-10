package com.pls.core.service.carrier.exception;


import com.pls.core.exception.ApplicationException;

/**
 * Signals that sending of shipment inquiry failed.
 * 
 * @author Viacheslav Krot
 */
public class ShipmentInquiryMailSendException extends ApplicationException {
    private static final long serialVersionUID = 8405454642647203345L;

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public ShipmentInquiryMailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     * @param  cause the cause.
     */
    public ShipmentInquiryMailSendException(Throwable cause) {
        super(cause);
    }
}
