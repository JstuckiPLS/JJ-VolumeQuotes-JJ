package com.pls.ltlrating.integration.ltllifecycle.service;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountGroupDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountGroupMappingDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.AddressDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.QuoteRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ShipmentDispatchRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.QuoteResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.ShipmentDispatchResultDTO;

public class LTLLifecycleRestClientTest {
    
    @InjectMocks
    private LTLLifecycleRestClient client;
    
    MockRestServiceServer mockServer;
    MockRestServiceServer mockServerAsync;
    
    private String endpointURL = "http://128.0.0.1:8080";
    
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(client, "ltlLifecycleRestUrl", endpointURL);
        
        RestTemplate serviceRestTemplate = (RestTemplate) ReflectionTestUtils.getField(client, "restTemplate");
        AsyncRestTemplate serviceRestTemplateAsync = (AsyncRestTemplate) ReflectionTestUtils.getField(client, "asyncRestTemplate");
        
        mockServer = MockRestServiceServer.createServer(serviceRestTemplate);
        mockServerAsync = MockRestServiceServer.createServer(serviceRestTemplateAsync);
    }

    @Test
    public void testGetQuotes() throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        
        QuoteResultDTO quoteResult = new QuoteResultDTO();
        quoteResult.setShipmentType(ShipmentType.LTL);
        List<QuoteResultDTO> expResponse = Collections.singletonList(quoteResult);

        mockServerAsync.expect(requestTo(endpointURL + "/api/quote"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("$.origin.state").value("MI"))
        .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(expResponse))
                );
        
        QuoteRequestDTO ltlQuoteRequest = new QuoteRequestDTO();
        AddressDTO origin = new AddressDTO();
        origin.setState("MI");
        ltlQuoteRequest.setOrigin(origin);
        CompletableFuture<List<QuoteResultDTO>> quotesFuture = client.getQuotesAsync(ltlQuoteRequest);
        List<QuoteResultDTO> quotes = quotesFuture.get(2, TimeUnit.SECONDS);
        
        Assert.assertEquals(expResponse, quotes);
    }
    
    @Test
    public void testGetAccountGroups() throws JsonProcessingException {
        
        P44AccountGroupDTO accGrp = new P44AccountGroupDTO();
        List<P44AccountGroupDTO> expResponse = Collections.singletonList(accGrp);

        mockServer.expect(requestTo(endpointURL + "/api/p44/accountGroups"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(expResponse))
                );
        
        List<P44AccountGroupDTO> accGrps = client.getAccountGroups();
        
        Assert.assertEquals(expResponse, accGrps);
        
    }
    
    @Test
    public void testGetAccountGroupMapping() throws JsonProcessingException {
        
        P44AccountGroupMappingDTO expResponse = new P44AccountGroupMappingDTO();

        mockServer.expect(requestTo(endpointURL + "/api/p44/accountGroupMapping/PLS20/123"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(expResponse))
                );
        
        P44AccountGroupMappingDTO accGrpMapping = client.getAccountGroupMapping(123L);
        
        Assert.assertEquals(expResponse, accGrpMapping);
        
    }
    
    @Test
    public void testSaveAccountGroupMapping() throws JsonProcessingException {

        mockServer.expect(requestTo(endpointURL + "/api/p44/accountGroupMapping/PLS20/123"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("$.p44AccountGroupCode").value("test"))
        .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                );
        
       
        P44AccountGroupMappingDTO accountGroupMapping = new P44AccountGroupMappingDTO();
        accountGroupMapping.setP44AccountGroupCode("test");
        client.saveAccountGroupMapping(123L, accountGroupMapping);

    }
    
    @Test
    public void testDispatch() throws JsonProcessingException {
        
        ShipmentDispatchResultDTO expResponse = new ShipmentDispatchResultDTO();

        mockServer.expect(requestTo(endpointURL + "/api/shipment/dispatch/"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("$.origin.state").value("MI"))
        .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(expResponse))
                );
        
        ShipmentDispatchRequestDTO dispatchRequest = new ShipmentDispatchRequestDTO();
        AddressDTO origin = new AddressDTO();
        origin.setState("MI");
        dispatchRequest.setOrigin(origin);
        ShipmentDispatchResultDTO result = client.dispatch(dispatchRequest);
        
        Assert.assertEquals(expResponse, result);
        
    }

}
