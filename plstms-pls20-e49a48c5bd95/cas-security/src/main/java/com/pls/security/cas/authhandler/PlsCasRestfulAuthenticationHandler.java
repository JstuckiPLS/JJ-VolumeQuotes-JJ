package com.pls.security.cas.authhandler;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom CAS authentication handler for PLS project needs. Works with authentication REST service.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class PlsCasRestfulAuthenticationHandler implements AuthenticationHandler {

    protected final Logger log = LoggerFactory.getLogger(PlsCasRestfulAuthenticationHandler.class);

    private static final String LOGIN_PARAM = "login";

    private static final String PASSWORD_PARAM = "password";

    private RestTemplate restTemplate;
    private String authUrl;

    @Override
    public boolean authenticate(Credentials credentials) throws AuthenticationException {
        Map<String, String> params = new HashMap<String, String>();
        UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) credentials;
        params.put(LOGIN_PARAM, usernamePasswordCredentials.getUsername());
        params.put(PASSWORD_PARAM, usernamePasswordCredentials.getPassword());
        try {
            ResponseEntity<Void> entity = restTemplate.getForEntity(authUrl, Void.class, params);
            return entity.getStatusCode() == HttpStatus.OK;
        } catch (RestClientException e) {
            log.error("Can not authenticate through REST service", e);
            return false;
        }
    }

    @Override
    public boolean supports(Credentials credentials) {
        return true;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
}
