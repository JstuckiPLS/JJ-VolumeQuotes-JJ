package com.pls.core.exception;

/**
 * Thrown if any I/O exceptions occur while working with FTP.
 *
 * @author Mikhail Boldinov, 04/09/13
 */
public class FTPClientException extends Exception {

    private static final long serialVersionUID = 5816045228886419983L;

    private String ftpClientReplyString;

    /**
     * Constructor.
     *
     * @param message error message
     * @param ftpClientReplyString FTP client reply string
     */
    public FTPClientException(String message, String ftpClientReplyString) {
        super(message);
        this.ftpClientReplyString = ftpClientReplyString;
    }

    /**
     * Constructor.
     *
     * @param message error message
     * @param ftpClientReplyString FTP client reply string
     * @param cause   exception cause
     */
    public FTPClientException(String message, String ftpClientReplyString, Throwable cause) {
        super(message, cause);
        this.ftpClientReplyString = ftpClientReplyString;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ". error='" + this.ftpClientReplyString + "'";
    }
}
