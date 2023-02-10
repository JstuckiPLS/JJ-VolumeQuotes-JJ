package com.pls.user.service.exc;

import com.pls.core.exception.ApplicationException;

/**
 * Signals that password prompted during password change do not match.
 * 
 * @author Viacheslav Krot
 * 
 */
public class PasswordsDoNotMatchException extends ApplicationException {
    private static final long serialVersionUID = 1L;

}
