package com.pls.core.service.impl.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Custom {@link LogoutSuccessHandler} to return HTTP 200 code instead of 301.
 * 
 * @author Maxim Medvedev
 */
public class Http200LogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache, no-store");
        response.getWriter().flush();
    }
}
