package com.pls.core.exception;

/**
 * Exception that happens during xml serialization/deserialization.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class XmlSerializationException extends ApplicationException {
    private static final long serialVersionUID = 9136117017598789768L;

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause cause of exception
     */
    public XmlSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
