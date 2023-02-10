package com.pls.ltlrating.integration.ltllifecycle.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.pls.ltlrating.integration.ltllifecycle.LtlLifecycleClientException;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountGroupDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountGroupMappingDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.QuoteRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ShipmentCancelRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ShipmentDispatchRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ShipmentTrackRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.QuoteResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.ShipmentCancelResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.ShipmentDispatchResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.ShipmentTrackingResultDTO;

@Service
public class LTLLifecycleRestClient {
    
    private static final Logger LOG = LoggerFactory.getLogger(LTLLifecycleRestClient.class);
    
    @Value("${ltlLifecycleRest.url}")
    private String ltlLifecycleRestUrl;
    
    @Value("${ltlLifecycleRest.auth.user}")
    private String ltlLifecycleRestUsername;
    
    @Value("${ltlLifecycleRest.auth.password}")
    private String ltlLifecycleRestPassword;

    private RestTemplate restTemplate = new RestTemplate();
    private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    
    @PostConstruct
    protected void init() {
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(ltlLifecycleRestUsername, ltlLifecycleRestPassword));
        asyncRestTemplate.getInterceptors().add(new AsyncBasicAuthorizationInterceptor(ltlLifecycleRestUsername, ltlLifecycleRestPassword));
    }

    public CompletableFuture<List<QuoteResultDTO>> getQuotesAsync(QuoteRequestDTO ltlQuoteRequest) {
        final String customerId = ltlQuoteRequest.getCustomerId();
        final ShipmentType shipmentType = ltlQuoteRequest.getShipmentType();

        CompletableFuture<List<QuoteResultDTO>> futureResult = new CompletableFuture<List<QuoteResultDTO>>();
        
        String url = ltlLifecycleRestUrl + "/api/quote";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QuoteRequestDTO> req = new HttpEntity<QuoteRequestDTO>(ltlQuoteRequest, headers);

        try {
            final long requestStart = System.currentTimeMillis();
            
            ListenableFuture<ResponseEntity<List<QuoteResultDTO>>> response = asyncRestTemplate.exchange(url, HttpMethod.POST, req,
                    new ParameterizedTypeReference<List<QuoteResultDTO>>() {
                    });

            response.addCallback((result) -> {
                LOG.info("LTL-Lifecycle quotes retrieved." + (ltlQuoteRequest.getCustomerId() != null? " (CSP)": " (Blanket)") + " in " + (System.currentTimeMillis()-requestStart) + "ms");
                List<QuoteResultDTO> quotes = result.getBody();
                
                //mark results so we can identify their type later
                for(QuoteResultDTO quote: quotes) {
                    quote.setCustomerId(customerId);
                    quote.setShipmentType(shipmentType);
                }
                
                futureResult.complete(quotes);
            }, (ex) -> {
                futureResult.complete(null); // in case of exception, the value of the future will be null, and we log the problem here.
                String errorMessage = "Getting LTL-Lifecycle quotes failed. " + getReasonMessage(ex);
                LOG.error(errorMessage, ex);
            });
            
            return futureResult;
        } catch (Exception ex) {
            String errorMessage = "Getting LTL-Lifecycle quotes failed. " + getReasonMessage(ex);
            LOG.error(errorMessage, ex);
            futureResult.complete(null);
            return futureResult; 
        } 

    }
    
    public List<P44AccountGroupDTO> getAccountGroups() {
        
        String url = ltlLifecycleRestUrl + "/api/p44/accountGroups";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> req = new HttpEntity<Void>(headers);

        try {
            ResponseEntity<List<P44AccountGroupDTO>> response = restTemplate.exchange(url, HttpMethod.GET, req,
                    new ParameterizedTypeReference<List<P44AccountGroupDTO>>() {
                    });

            return response.getBody();
        } catch (Exception ex) {
            String errorMessage = "Getting account groups failed. " + getReasonMessage(ex);
            LOG.error(errorMessage, ex);
            throw new LtlLifecycleClientException(errorMessage, ex);
        } 

    }
    
    public P44AccountGroupMappingDTO getAccountGroupMapping(Long customerId) {
        
        String url = ltlLifecycleRestUrl + "/api/p44/accountGroupMapping/PLS20/"+customerId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> req = new HttpEntity<Void>(headers);

        try {
            ResponseEntity<P44AccountGroupMappingDTO> response = restTemplate.exchange(url, HttpMethod.GET, req, P44AccountGroupMappingDTO.class);

            return response.getBody();
        } catch (Exception ex) {
            String errorMessage = "Getting account group mapping failed. " + getReasonMessage(ex);
            LOG.error(errorMessage, ex);
            throw new LtlLifecycleClientException(errorMessage, ex);
        } 
    }
    
    public void saveAccountGroupMapping(Long customerId, P44AccountGroupMappingDTO accountGroupMapping) {
        
        String url = ltlLifecycleRestUrl + "/api/p44/accountGroupMapping/PLS20/"+customerId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<P44AccountGroupMappingDTO> req = new HttpEntity<P44AccountGroupMappingDTO>(accountGroupMapping, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, req, Void.class);
        } catch (Exception ex) {
            String errorMessage = "Saving account group mapping failed. " + getReasonMessage(ex);
            LOG.error(errorMessage, ex);
            throw new LtlLifecycleClientException(errorMessage, ex);
        } 
    }

    public ShipmentDispatchResultDTO dispatch(ShipmentDispatchRequestDTO dispatchRequest) {
        String url = ltlLifecycleRestUrl + "/api/shipment/dispatch/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ShipmentDispatchRequestDTO> req = new HttpEntity<ShipmentDispatchRequestDTO>(dispatchRequest, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, req, ShipmentDispatchResultDTO.class).getBody();
        } catch (Exception ex) {
            String errorMessage = "Dispatch failed. " + getReasonMessage(ex);
            LOG.error(errorMessage, ex);
            throw new LtlLifecycleClientException(errorMessage, ex);
        } 
    }
    
    public ShipmentCancelResultDTO cancel(ShipmentCancelRequestDTO cancelRequest) {
        String url = ltlLifecycleRestUrl + "/api/shipment/cancel/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ShipmentCancelRequestDTO> req = new HttpEntity<ShipmentCancelRequestDTO>(cancelRequest, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, req, ShipmentCancelResultDTO.class).getBody();
        } catch (Exception ex) {
            String errorMessage = "Cancel failed. " + getReasonMessage(ex);
            LOG.error(errorMessage, ex);
            throw new LtlLifecycleClientException(errorMessage, ex);
        } 
    }

    public ShipmentTrackingResultDTO initTrack(ShipmentTrackRequestDTO trackRequest) {
        String url = ltlLifecycleRestUrl + "/api/shipment/track/init";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ShipmentTrackRequestDTO> req = new HttpEntity<ShipmentTrackRequestDTO>(trackRequest, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, req, ShipmentTrackingResultDTO.class).getBody();
        } catch (Exception ex) {
            String errorMessage = "Tracking initialization failed. " + getReasonMessage(ex);
            LOG.error(errorMessage, ex);
            throw new LtlLifecycleClientException(errorMessage, ex);
        }
    }

    public String getReasonMessage(Throwable ex) {
        if(ex instanceof RestClientResponseException) {
            RestClientResponseException ex2 = (RestClientResponseException) ex;
            return "Endpoint returned status: " + ex2.getRawStatusCode() + "; message: " + ex2.getResponseBodyAsString();
        } else {
            return ex.getMessage();
        }
    }

}
