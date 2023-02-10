package com.pls.smc3.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.shared.AddressVO;
import com.pls.smc3.dto.TerminalDetailsDTO;
import com.pls.smc3.dto.TerminalResponseDetailDTO;
import com.pls.smc3.service.TerminalDetailsClient;

/**
 * Test class for {@link TerminalDetailsClient}.
 * 
 * 
 * @author Pavani Challa
 * 
 */
public class TerminalDetailsClientImplTest extends BaseServiceITClass {

    private static final String METHOD_LTL = "LTL";
    private static final String SCAC_RDWY = "RDWY";
    private static final String SCAC_AVRT = "AVRT";
    private static final String COUNTRY_USA = "USA";
    @Autowired
    private TerminalDetailsClient impl;

    @Test
    public void testGetTerminalDetailsByPostalCode() throws Exception {

        List<TerminalResponseDetailDTO> dtos = impl.getTerminalDetailsByPostalCode(createRequest(), new Date());
        assert (dtos != null);
        assertEquals("Size of terminals", 2, dtos.size());

    }

    @Test
    public void testGetTerminalsByTerminalCode() throws Exception {

        TerminalDetailsDTO dto = new TerminalDetailsDTO();
        dto.setMethod(METHOD_LTL);

        List<String> scacs = new ArrayList<String>();
        scacs.add(SCAC_AVRT);
        scacs.add(SCAC_RDWY);

        dto.setScacList(scacs);

        List<TerminalDetailsDTO> list = new ArrayList<TerminalDetailsDTO>();
        list.add(dto);
        List<TerminalResponseDetailDTO> dtos = impl.getTerminalsByTerminalCode(list);

        assert (dtos != null);
        assertEquals("Size of terminals", 2, dtos.size());

    }

    /**
     * Creates the request to get terminal details by postal code.
     * 
     * @return List of {@link TerminalDetailsDTO}
     */
    private List<TerminalDetailsDTO> createRequest() {

        TerminalDetailsDTO dto = new TerminalDetailsDTO();
        dto.setScac(SCAC_AVRT);
        dto.setMethod(METHOD_LTL);

        AddressVO addressDto = new AddressVO();
        addressDto.setPostalCode("10024");
        addressDto.setCountryCode(COUNTRY_USA);

        AddressVO addressDto1 = new AddressVO();
        addressDto1.setPostalCode("15090");
        addressDto1.setCountryCode(COUNTRY_USA);

        List<AddressVO> addresses = new ArrayList<AddressVO>();
        addresses.add(addressDto);
        addresses.add(addressDto1);
        dto.setShipmentAddresses(addresses);

        List<TerminalDetailsDTO> details = new ArrayList<TerminalDetailsDTO>();

        details.add(dto);

        TerminalDetailsDTO dto1 = new TerminalDetailsDTO();
        dto1.setScac(SCAC_RDWY);
        dto1.setMethod(METHOD_LTL);

        AddressVO addressDto2 = new AddressVO();
        addressDto2.setPostalCode("16066");
        addressDto2.setCountryCode(COUNTRY_USA);

        List<AddressVO> addresses1 = new ArrayList<AddressVO>();
        addresses1.add(addressDto2);
        dto1.setShipmentAddresses(addresses1);

        details.add(dto1);

        return details;
    }
}
