package com.pls.core.service.impl.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * Custom {@link AuthenticationFailureHandler} to return HTTP 402 code for invalid credentials.
 * 
 * @author Maxim Medvedev
 */
public class Http403FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Authentication Failed: " + exception.getMessage());
    }
}
