package com.pls.smc3.service.impl;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.BaseServiceITClass;
import com.pls.smc3.dto.DensityRateResponseDTO;
import com.pls.smc3.dto.DensityRateShipmentRequestDTO;
import com.pls.smc3.dto.DensityRequestDetailDTO;
import com.pls.smc3.service.DensityRateClient;
import com.pls.smc3.validation.WebServiceValidationException;

/**
 * Test class for {@link DensityRateClient}.
 * 
 * @author Pavani Challa
 * 
 */
public class DensityRateClientImplTest extends BaseServiceITClass {

    private static final String COUNTRY_USA = "USA";
    @Autowired
    private DensityRateClient impl;

    @Ignore("Need to get credentials for Density Rates")
    @Test(expected = WebServiceValidationException.class)
    public void testGetDensityRatesError() throws WebServiceValidationException {

        impl.getDensityRates(buildRequest());

    }

    @Ignore("Need to get credentials for Density Rates")
    @Test
    public void testGetDensityRates() throws WebServiceValidationException {
        DensityRateShipmentRequestDTO request = buildRequest();
        request.setOriginCountry(COUNTRY_USA);
        DensityRateResponseDTO dto = impl.getDensityRates(request);
        assert (dto != null);
        assertEquals("comparing the destination country", COUNTRY_USA, dto.getDestinationCountry());
        assertEquals("No error", "0", dto.getErrorCode());
    }

    /**
     * Builds request to test.
     * 
     * @return DensityRateShipmentRequestDTO
     */
    private DensityRateShipmentRequestDTO buildRequest() {

        DensityRateShipmentRequestDTO dto = new DensityRateShipmentRequestDTO();
        dto.setOriginPostalCode("40201");
        dto.setOriginCountry("USA");
        dto.setDestinationPostalCode("60601");
        dto.setDestinationCountry(COUNTRY_USA);
        dto.setTariffName("LITECZ02");
        try {
            dto.setShipmentDateCCYYMMDD(DateUtility.stringToDate("10/01/2000", "mm/dd/yyyy"));
        } catch (ParseException pe) {

        }

        // dto.setShipmentDateCCYYMMDD("20110209");
        dto.setDetailType("LWH");
        List<DensityRequestDetailDTO> details = new ArrayList<DensityRequestDetailDTO>();

        DensityRequestDetailDTO det1 = new DensityRequestDetailDTO();

        det1.setDimensionUnits("F");
        det1.setHeight("5");
        det1.setLength("5");
        det1.setWidth("5");
        det1.setWeightUnits("L");
        det1.setWeight("20000");
        details.add(det1);

        DensityRequestDetailDTO det2 = new DensityRequestDetailDTO();

        det2.setDimensionUnits("F");
        det2.setHeight("6");
        det2.setLength("6");
        det2.setWidth("6");
        det2.setWeightUnits("L");
        det2.setWeight("10000");
        details.add(det2);
        dto.setDetails(details);

        return dto;

    }

    @Ignore("Need to get credentials for Density Rates")
    @Test(expected = WebServiceValidationException.class)
    public void testInValidRequest() throws WebServiceValidationException {

        DensityRateShipmentRequestDTO dto = new DensityRateShipmentRequestDTO();
        dto = buildRequest();
        DensityRateResponseDTO response = impl.getDensityRates(dto);
        assert (response == null);

    }

    @Ignore("Need to get credentials for Density Rates")
    @Test
    public void testValidateRequiredFields() throws WebServiceValidationException {

        DensityRateShipmentRequestDTO dto = new DensityRateShipmentRequestDTO();
        dto = buildRequest();
        dto.setOriginCountry(COUNTRY_USA);
        DensityRateResponseDTO response = impl.getDensityRates(dto);
        assert (response != null);
    }

}
