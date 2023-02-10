package com.pls.core.exception;


/**
 * Root class for all application exceptions. All custom exceptions should inherit from this.
 * 
 * @author Viacheslav Krot
 */
public class ApplicationException extends Exception {
    private static final long serialVersionUID = 8405454642647203345L;

    /**
     * Constructs a new exception.
     */
    public ApplicationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param   message the detail message.
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     * @param  cause the cause.
     */
    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
