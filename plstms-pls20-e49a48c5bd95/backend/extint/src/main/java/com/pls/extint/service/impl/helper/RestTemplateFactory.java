package com.pls.extint.service.impl.helper;

import java.net.URI;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.pls.extint.shared.ApiRequestVO;

/**
 * Factory for creating the templates for calling REST web services.
 * 
 * @author Pavani Challa
 * 
 */
@Component
public class RestTemplateFactory {

    /**
     * Creates a rest template with authorization header set.
     * 
     * @param uri
     *            uri to which request should be sent
     * @param requestVO
     *            data containing the credentials for the URI
     * @return the rest template
     */
    public RestTemplate getTemplate(URI uri, ApiRequestVO requestVO) {
        CredentialsProvider credentialsProvider = null;
        if (!StringUtils.isEmpty(requestVO.getApiType().getUsername())) {
            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort(), AuthScope.ANY_REALM),
                    new UsernamePasswordCredentials(requestVO.getApiType().getUsername(), requestVO.getApiType().getPassword()));
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new CustomHttpRequestFactory(httpClient, uri, requestVO.getApiType().getAuthPolicy());

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        return restTemplate;
    }
}
