package com.pls.smc3.service.impl;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.BaseServiceITClass;
import com.pls.smc3.dto.LTLDetailDTO;
import com.pls.smc3.dto.LTLRateShipmentDTO;
import com.pls.smc3.service.LtlRateShipmentClient;
import com.pls.smc3.validation.WebServiceValidationException;

/**
 * 
 * Test class for {@link LtlRateShipmentClient}.
 * 
 * @author Pavani Challa
 * 
 */
public class LtlRateShipmentClientTest extends BaseServiceITClass {

    private static final String TARIFF_DEMO = "LITECZ02";
    private static final String DEST_POSTAL_CODE = "60601";
    private static final String ORIGIN_POSTAL_CODE = "40201";
    private static final String COUNTRY_USA = "USA";
    @Autowired
    private LtlRateShipmentClient impl;

    @Test
    public void testGetLtlRateShipment() throws Exception {

        LTLRateShipmentDTO responseDTO = impl.getLtlRateShipment(validRequest());
        assert (responseDTO != null);
        assert (responseDTO.getDetails() != null);
        assertEquals("Comparing the destination postal code", DEST_POSTAL_CODE, responseDTO.getDestinationPostalCode());
        assertEquals("Comparing the destination state", "IL", responseDTO.getDestinationState());
        assertEquals("Comparing the origin state", "KY", responseDTO.getOriginState());
    }

    @Test(expected = WebServiceValidationException.class)
    public void testValidateEmptyRequest() throws Exception {
        LTLRateShipmentDTO requestDTO = null;
        impl.getLtlRateShipment(requestDTO);
    }

    @Test(expected = WebServiceValidationException.class)
    public void testValidateEmptyTariff() throws Exception {
        LTLRateShipmentDTO requestDTO = new LTLRateShipmentDTO();
        requestDTO.setOriginPostalCode(ORIGIN_POSTAL_CODE);
        requestDTO.setOriginCountry(COUNTRY_USA);
        requestDTO.setDestinationPostalCode(DEST_POSTAL_CODE);

        impl.getLtlRateShipment(requestDTO);
    }

    @Test(expected = WebServiceValidationException.class)
    public void testValidateEmptyShipmentDate() throws Exception {
        LTLRateShipmentDTO requestDTO = new LTLRateShipmentDTO();
        requestDTO.setOriginPostalCode(ORIGIN_POSTAL_CODE);
        requestDTO.setOriginCountry(COUNTRY_USA);
        requestDTO.setDestinationPostalCode(DEST_POSTAL_CODE);
        requestDTO.setDestinationCountry(COUNTRY_USA);
        requestDTO.setTariffName(TARIFF_DEMO);

        impl.getLtlRateShipment(requestDTO);
    }

    private LTLRateShipmentDTO validRequest() {

        LTLRateShipmentDTO requestDTO = new LTLRateShipmentDTO();

        requestDTO.setOriginPostalCode(ORIGIN_POSTAL_CODE);
        requestDTO.setOriginCountry(COUNTRY_USA);
        requestDTO.setOriginCity("LOUISVILLE");
        requestDTO.setDestinationPostalCode(DEST_POSTAL_CODE);
        requestDTO.setDestinationCountry(COUNTRY_USA);
        requestDTO.setDestinationCity("CHICAGO");
        requestDTO.setTariffName(TARIFF_DEMO);
        try {
            requestDTO.setShipmentDate(DateUtility.stringToDate("10/01/2000", "mm/dd/yyyy"));
        } catch (ParseException pe) {

        }

        List<LTLDetailDTO> requestDetails = new ArrayList<LTLDetailDTO>();

        LTLDetailDTO reqDetail = new LTLDetailDTO();
        reqDetail.setNmfcClass("50");
        reqDetail.setWeight("20000");

        requestDetails.add(reqDetail);

        reqDetail = new LTLDetailDTO();
        reqDetail.setNmfcClass("50");
        reqDetail.setWeight("10000");

        requestDetails.add(reqDetail);

        requestDTO.setDetails(requestDetails);

        return requestDTO;
    }
}
