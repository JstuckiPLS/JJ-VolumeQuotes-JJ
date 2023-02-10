package com.pls.core.service.pdf.exception;

import com.pls.core.exception.ApplicationException;


/**
 * Signals that PDF generation failed.
 * 
 * @author Viacheslav Krot
 */
public class PDFGenerationException extends ApplicationException {
    private static final long serialVersionUID = 8405454642647203345L;

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public PDFGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     * @param  cause the cause.
     */
    public PDFGenerationException(Throwable cause) {
        super(cause);
    }
}
