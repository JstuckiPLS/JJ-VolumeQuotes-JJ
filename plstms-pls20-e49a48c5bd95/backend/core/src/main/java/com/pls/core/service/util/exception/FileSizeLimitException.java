package com.pls.core.service.util.exception;

import com.pls.core.exception.ApplicationException;

/**
 * This exception is about file size limit exceeded.
 * 
 * @author Andrey Kachur
 * 
 */
public class FileSizeLimitException extends ApplicationException {
    private static final long serialVersionUID = -8880384008928026367L;

    /**
     * Default contructor.
     * 
     * @param maxFileSizeInMegabytes
     *            maximum file size in MB.
     * @param cause
     *            {@link Throwable} to keep the stack trace
     */
    public FileSizeLimitException(double maxFileSizeInMegabytes, Throwable cause) {
        super("File size should be no more than " + maxFileSizeInMegabytes + " MB", cause);
    }
}
