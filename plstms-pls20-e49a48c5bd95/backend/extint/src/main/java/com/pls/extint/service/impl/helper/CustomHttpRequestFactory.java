package com.pls.extint.service.impl.helper;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.KerberosScheme;
import org.apache.http.impl.auth.SPNegoScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.pls.extint.domain.enums.AuthPolicy;

/**
 * Custom implementation for creating the HttpContext with Authentication scheme set.
 * 
 * @author Pavani Challa
 * 
 */
public class CustomHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
    private HttpHost host;
    private AuthPolicy authPolicy;

    /**
     * Default constructor.
     * 
     * @param httpClient
     *            Interface for HTTP Request execution
     * @param uri
     *            uri of the end point
     * @param authPolicy
     *            determines the type of authentication scheme to be set
     */
    public CustomHttpRequestFactory(HttpClient httpClient, URI uri, AuthPolicy authPolicy) {
        super(httpClient);
        this.host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        this.authPolicy = authPolicy;
    }

    /**
     * Creates the HTTP Context for the request.
     * 
     * @param uri
     *            uri of the end point
     * @param httpMethod
     *            type of HTTP request
     * 
     * @return returns the newly created HTTP context with Authentication scheme set.
     */
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return createHttpContext();
    }

    private HttpContext createHttpContext() {
        // Default is BASIC policy.
        AuthScheme authScheme = new BasicScheme();

        if (authPolicy != null) {
            switch (authPolicy) {
            case DIGEST:
                // Generate DIGEST scheme object, initialize it and add it to the local auth cache
                authScheme = new DigestScheme();
                break;
            case KERBEROS:
                authScheme = new KerberosScheme();
                break;
            case SPNEGO:
                authScheme = new SPNegoScheme();
                break;
            case BASIC:
            default:
                // Generate BASIC scheme object and add it to the local auth cache
                authScheme = new BasicScheme();
                break;
            }
        }

        AuthCache authCache = new BasicAuthCache();
        authCache.put(host, authScheme);

        // Add AuthCache to the execution context
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
        return localContext;
    }
}
