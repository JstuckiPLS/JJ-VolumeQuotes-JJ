package com.pls.smc3.service;

import com.pls.smc3.dto.DensityRateResponseDTO;
import com.pls.smc3.dto.DensityRateShipmentRequestDTO;
import com.pls.smc3.validation.WebServiceValidationException;

/**
 * Density Rate shipment Client.
 * 
 * @author PAVANI CHALLA
 *
 */
public interface DensityRateClient {

    /**
     * Calls the SMC3 service to get the density rates.
     * 
     * @param requestDetail {@link DensityRateShipmentRequestDTO}
     * @return {@link DensityRateResponseDTO}
     * @throws WebServiceValidationException validation exceptions
     */
    DensityRateResponseDTO getDensityRates(DensityRateShipmentRequestDTO requestDetail)
            throws WebServiceValidationException;

}
