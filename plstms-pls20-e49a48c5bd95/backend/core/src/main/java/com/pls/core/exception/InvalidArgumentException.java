package com.pls.core.exception;


/**
 * Signals that method argument is invalid - null or in illegal range.
 * 
 * @author Viacheslav Krot
 */
public class InvalidArgumentException extends ApplicationException {
    private static final long serialVersionUID = 8405454642647203345L;

    /**
     * Constructs a new exception.
     */
    public InvalidArgumentException() {
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param   message the detail message.
     */
    public InvalidArgumentException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     * @param  cause the cause.
     */
    public InvalidArgumentException(Throwable cause) {
        super(cause);
    }
}
