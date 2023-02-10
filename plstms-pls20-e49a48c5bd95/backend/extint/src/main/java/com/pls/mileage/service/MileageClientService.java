package com.pls.mileage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pls.mileage.dto.MileageInfoRequest;
import com.pls.mileage.dto.MileageInfoResponse;
import com.pls.mileage.dto.MileageInfoResponseWrapper;

/**
 * Client service using Mileage-REST to calculate mileage from Origin to Destination.
 */
@Service
public class MileageClientService {
    
    private static final Logger LOG = LoggerFactory.getLogger(MileageClientService.class);
    
    @Value("${mileage.api.url}")
    private String mileageApiUrl;
    
    private RestTemplate restTemplate = new RestTemplate();

    public MileageInfoResponse getMileageInfo(MileageInfoRequest request) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MileageInfoRequest> req = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<MileageInfoResponseWrapper> response = restTemplate.exchange(mileageApiUrl, HttpMethod.POST, req, new ParameterizedTypeReference<MileageInfoResponseWrapper>() {});
            return response.getBody().getBody();
        } catch (Exception ex) {
            LOG.error("Cannot get mileage information. " , ex);
            return null;
        }
        
    }
}
