package com.pls.smc3.service;

import com.pls.smc3.dto.LTLRateShipmentDTO;

/**
 * 
 * This service is used to get rate for single shipment using NMFC class and weight. This service supports the application of discounts, minimum
 * charge floor, and other factors to produce a highly accurate pricing response.
 * 
 * @author Pavani Challa
 */
public interface LtlRateShipmentClient {
    /**
     * Get rate for a single shipment. Postal code is a required field. The service doesn't validate the city and if city is not valid as per the
     * postal code, it returns result based on the postal code.
     * 
     * @param requestDTO
     *            LTLRateShipmentDTO
     * @return {@link LTLRateShipmentDTO} response DTO.
     * @throws Exception
     *             Throws web service and data module exception.
     */
    LTLRateShipmentDTO getLtlRateShipment(LTLRateShipmentDTO requestDTO) throws Exception;

    /**
     * Method calls LTL Rate Shipment Multiple to rate multiple shipments using NMFC class and weight.
     * 
     * @param requestDTO
     *            {@link LTLRateShipmentDTO}.
     * @return {@link LTLRateShipmentDTO} response.
     * @throws Exception
     *             Throws validation, web service and data module exception.
     */
    LTLRateShipmentDTO getLtlRateShipmentMultiple(LTLRateShipmentDTO requestDTO) throws Exception;
}
