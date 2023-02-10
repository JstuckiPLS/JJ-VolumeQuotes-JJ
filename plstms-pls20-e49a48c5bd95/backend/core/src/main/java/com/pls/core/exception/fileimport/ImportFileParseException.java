package com.pls.core.exception.fileimport;



/**
 * Signals that some unexpected failure happened during parsing of import file.
 * 
 * @author Viacheslav Krot
 * 
 */
public class ImportFileParseException extends ImportException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public ImportFileParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public ImportFileParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
