package com.pls.core.service.impl.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Custom {@link AuthenticationSuccessHandler} to return HTTP 200 code instead of 301.
 * 
 * @author Maxim Medvedev
 */
public class Http200SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().flush();
    }

}
