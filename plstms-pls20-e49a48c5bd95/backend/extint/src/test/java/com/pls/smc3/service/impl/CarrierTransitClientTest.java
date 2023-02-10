package com.pls.smc3.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.shared.AddressVO;
import com.pls.smc3.dto.ScacRequest;
import com.pls.smc3.dto.TransitRequestDTO;
import com.pls.smc3.dto.TransitResponseDTO;
import com.pls.smc3.service.CarrierTransitClient;

/**
 * Carrier Transit Client Test class.
 * 
 * @author Pavani Challa
 * 
 */
public class CarrierTransitClientTest extends BaseServiceITClass {

    private static final String COUNTRY_INDIA = "IND";
    private static final String METHOD_LTL = "LTL";
    private static final String COUNTRY_USA = "USA";

    @Autowired
    private CarrierTransitClient impl;

    @Test
    public void testGetTransitInformation() throws Exception {
        TransitResponseDTO response = impl.getTransitInformation(createRequest());

        assert (response != null);
        assert (response.getDestination() != null);
        assert (response.getScacResponses() != null);
        assertEquals("Comparing destination country code", COUNTRY_USA, response.getDestination().getCountryCode());

    }

    @Test
    public void testValidateEmptyRequest() throws Exception {

        TransitRequestDTO requestDTO = new TransitRequestDTO();
        TransitResponseDTO response = impl.getTransitInformation(requestDTO);
        assert (response == null);
    }

    @Test
    public void testValidateScacMethod() throws Exception {

        TransitRequestDTO requestDTO = new TransitRequestDTO();
        ScacRequest scac = new ScacRequest();
        Set<ScacRequest> scacRequests = new HashSet<ScacRequest>();
        scacRequests.add(scac);
        requestDTO.setScacRequests(scacRequests);

        TransitResponseDTO response = impl.getTransitInformation(requestDTO);
        assert (response == null);
    }

    @Test
    public void testValidateDestination() throws Exception {

        TransitRequestDTO requestDTO = new TransitRequestDTO();
        ScacRequest scac = new ScacRequest();
        scac.setMethod(METHOD_LTL);
        Set<ScacRequest> scacRequests = new HashSet<ScacRequest>();
        scacRequests.add(scac);
        requestDTO.setScacRequests(scacRequests);

        TransitResponseDTO response = impl.getTransitInformation(requestDTO);
        assert (response == null);
    }

    @Test
    public void testValidateDestinationCountryCode() throws Exception {

        TransitRequestDTO requestDTO = new TransitRequestDTO();
        ScacRequest scac = new ScacRequest();
        scac.setMethod(METHOD_LTL);
        Set<ScacRequest> scacRequests = new HashSet<ScacRequest>();
        scacRequests.add(scac);
        requestDTO.setScacRequests(scacRequests);

        AddressVO destination = new AddressVO();
        requestDTO.setDestination(destination);

        TransitResponseDTO response = impl.getTransitInformation(requestDTO);
        assert (response == null);
    }

    @Test
    public void testValidateSupportedCountryCode() throws Exception {

        TransitRequestDTO requestDTO = new TransitRequestDTO();
        ScacRequest scac = new ScacRequest();
        scac.setMethod(METHOD_LTL);
        Set<ScacRequest> scacRequests = new HashSet<ScacRequest>();
        scacRequests.add(scac);
        requestDTO.setScacRequests(scacRequests);

        AddressVO destination = new AddressVO();
        destination.setCountryCode(COUNTRY_INDIA);
        requestDTO.setDestination(destination);

        TransitResponseDTO response = impl.getTransitInformation(requestDTO);
        assert (response == null);
    }

    @Test
    public void testValidateDestinationPostalCode() throws Exception {

        TransitRequestDTO requestDTO = new TransitRequestDTO();
        ScacRequest scac = new ScacRequest();
        scac.setMethod(METHOD_LTL);
        Set<ScacRequest> scacRequests = new HashSet<ScacRequest>();
        scacRequests.add(scac);
        requestDTO.setScacRequests(scacRequests);

        AddressVO destination = new AddressVO();
        destination.setCountryCode(COUNTRY_USA);
        requestDTO.setDestination(destination);

        TransitResponseDTO response = impl.getTransitInformation(requestDTO);
        assert (response == null);
    }

    @Test
    public void testValidateOrigin() throws Exception {

        TransitRequestDTO requestDTO = new TransitRequestDTO();
        ScacRequest scac = new ScacRequest();
        scac.setMethod(METHOD_LTL);
        Set<ScacRequest> scacRequests = new HashSet<ScacRequest>();
        scacRequests.add(scac);
        requestDTO.setScacRequests(scacRequests);

        AddressVO destination = new AddressVO();
        destination.setCountryCode(COUNTRY_USA);
        destination.setPostalCode("10024");
        requestDTO.setDestination(destination);

        AddressVO origin = new AddressVO();
        origin.setCountryCode(COUNTRY_USA);
        requestDTO.setOrigin(origin);

        TransitResponseDTO response = impl.getTransitInformation(requestDTO);
        assert (response == null);
    }

    private TransitRequestDTO createRequest() {

        TransitRequestDTO dto = new TransitRequestDTO();

        ScacRequest scac = new ScacRequest();
        scac.setMethod(METHOD_LTL);

        Set<ScacRequest> scacRequests = new HashSet<ScacRequest>();
        scacRequests.add(scac);

        dto.setScacRequests(scacRequests);

        AddressVO destination = new AddressVO();
        destination.setPostalCode("10024-0010");
        destination.setCountryCode(COUNTRY_USA);

        dto.setDestination(destination);

        AddressVO origin = new AddressVO();
        origin.setPostalCode("15090-0012");
        origin.setCountryCode(COUNTRY_USA);

        dto.setOrigin(origin);

        return dto;
    }

}
