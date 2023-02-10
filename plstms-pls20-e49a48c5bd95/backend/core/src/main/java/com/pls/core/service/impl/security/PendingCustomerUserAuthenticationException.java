package com.pls.core.service.impl.security;

/**
 *  If customer is inactive customer users should not be able to login. This
 * class utilizes for certain kind of exception.
 * 
 * @author Dmitriy Nefedchenko
 */
public class PendingCustomerUserAuthenticationException extends RuntimeException {
    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -8369503213353931621L;

    /**
     * Raises exception in response of inactive customer user login attempt.
     * 
     * @param message - exception message
     */
    public PendingCustomerUserAuthenticationException(String message) {
        super(message);
    }
}
