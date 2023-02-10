package com.pls.ax.custopenbalance.client;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Authenticator class for authenticating users on windows box.
 * 
 * @author Thomas Clancy
 */
public class NtlmAuthenticator extends Authenticator {
    private final String username;
    private final char[] password;

    /**
     * Constructor.
     * @param username The username.
     * @param pass The password.
     */
    public NtlmAuthenticator(final String username, final String pass) {
        super();
        this.username = username;
        this.password = (pass != null ? pass.toCharArray() : "".toCharArray());
    }

    /**
     * Returns a PasswordAuthentication object back to the Authenticator when we attempt to
     * connect to the AX server.
     * 
     * @return a PasswordAuthentication instance.
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
