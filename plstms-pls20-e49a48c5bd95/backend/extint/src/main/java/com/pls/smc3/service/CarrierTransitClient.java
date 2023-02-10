package com.pls.smc3.service;

import com.pls.smc3.dto.TransitRequestDTO;
import com.pls.smc3.dto.TransitResponseDTO;

/**
 * Service is used to obtain carrier transit time information from SMC3.
 * 
 * @author Pavani Challa
 * 
 */
public interface CarrierTransitClient {

    /**
     * The method gets the transit information from SMC3 carrier.
     * It takes the request, validates and returns back the DTO response.
     * 
     * @param requestDTO
     *            {@link TransitRequestDTO} request object prepared to get Transit details.
     * @return {@link TransitResponseDTO} response DTO from the SMC3 transit call.
     */
    TransitResponseDTO getTransitInformation(TransitRequestDTO requestDTO);

}
