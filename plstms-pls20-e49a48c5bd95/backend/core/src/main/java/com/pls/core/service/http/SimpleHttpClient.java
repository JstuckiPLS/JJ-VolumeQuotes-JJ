package com.pls.core.service.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A simple http client for posting info to a URL.
 *
 * @author Thomas Clancy
 */
@Service
public class SimpleHttpClient {

    @Autowired
    private RestTemplate rest;

    /**
     * Posts the body with the given headers to a url.
     *
     * @param urlString The destination URL in the form of a String.
     * @param body The boyd in the form of a String.
     * @param headers A set of headers.
     * @return Returns the status code in an instance of HttpStatus.
     */
    public HttpStatus post(String urlString, String body, HttpHeaders headers) {
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(urlString, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getStatusCode();
    }
}
