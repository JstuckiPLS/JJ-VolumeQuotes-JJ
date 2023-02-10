package com.pls.core.exception;

/**
 * Exception that appears in EDI processing process.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class EdiProcessingException extends ApplicationException {
    private static final long serialVersionUID = 4790667247221388615L;

    /**
     * Constructor.
     */
    public EdiProcessingException() {
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause exception cause
     */
    public EdiProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
