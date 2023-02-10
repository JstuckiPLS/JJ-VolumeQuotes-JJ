package com.pls.smc3.service;

import com.pls.smc3.dto.TerminalDetailsDTO;
import com.pls.smc3.dto.TerminalResponseDetailDTO;

import java.util.Date;
import java.util.List;

/**
 * Service to get TerminalDetails.
 * 
 * @author Pavani Challa
 * 
 */
public interface TerminalDetailsClient {

    /**
     * Retrieves the carrier's terminal code(s) using PostalCode/CountryCode pairs.
     * 
     * @param terminalDetails
     *            List of {@link TerminalDetailsDTO}
     * @return List of {@link TerminalResponseDetailDTO}.
     */
    List<TerminalResponseDetailDTO> getTerminalDetailsByPostalCode(List<TerminalDetailsDTO> terminalDetails, Date shipmentDate);

    /**
     * Retrieves a list of terminals for a specific carrier and service method.
     * 
     * @param terminalDetails
     *            List of {@link TerminalDetailsDTO}
     * @return List of {@link TerminalResponseDetailDTO}.
     * @throws Exception
     *             Exception
     */
    List<TerminalResponseDetailDTO> getTerminalsByTerminalCode(List<TerminalDetailsDTO> terminalDetails) throws Exception;

    /**
     * Calls the web service to get a listing of all error codes and descriptions.
     * 
     * @param codes
     *            List of Integer.
     * @return ArrayOfProductError product error list
     * @throws Exception
     *             exception
     */
    //FIXME kircicegi ArrayOfProductError lookupErrorCode(List<Integer> codes) throws Exception;

}
