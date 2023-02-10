package com.pls.user.service.exc;

import com.pls.core.exception.ApplicationException;

/**
 * Exception class for errors when resetting password.
 * 
 * @author Aleksandr Leshchenko
 */
public class UserPasswordResetException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public UserPasswordResetException(String message) {
        super(message);
    }
}
