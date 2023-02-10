package com.pls.core.exception;

/**
 * EDI validation exception.
 *
 * @author Mikhail Boldinov, 06/03/14
 */
public class EDIValidationException extends ApplicationException {

    private static final long serialVersionUID = 7551795687389158148L;

    /**
     * Default constructor.
     */
    public EDIValidationException() {
    }

    /**
     * Constructor.
     *
     * @param message error message
     */
    public EDIValidationException(String message) {
        super(message);
    }
}
