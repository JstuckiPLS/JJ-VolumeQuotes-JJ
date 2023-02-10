package com.pls.ltlrating.service;

import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlRatingVO;

/**
 * Service for getting prices from CarrierAPI.
 *
 * @author Brichak Aleksandr
 */
public interface LtlCarrierAPIService {
    /**
     * Method to populate rates.
     *
     * @param ratesCO
     *            pricing details to populate criteria.
     * @param ratingProfile
     *            pricing profile to populate criteria.
     */
    void populateRates(GetOrderRatesCO ratesCO, LtlRatingVO ratingProfile);

}
