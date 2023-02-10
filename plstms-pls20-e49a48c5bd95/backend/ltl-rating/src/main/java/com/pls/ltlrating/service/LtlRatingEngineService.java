package com.pls.ltlrating.service;

import java.util.List;

import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingResult;

/**
 * Class where we calculate LTL rates for the given parameters.
 *
 * @author Hima Bindu Challa
 *
 */
public interface LtlRatingEngineService {
    int MAX_TRANSIT_TIME = 69; //69 days = 99360 min, which is max value we can store as LOADS.TRAVEL_TIME.

    /**
     * Get rates.
     *
     * @param ratesCO
     *            - Order Rates Criteria.
     * @return return the final rates in ascending order or empty list in case of any error.
     */
    List<LtlPricingResult> getRatesSafe(GetOrderRatesCO ratesCO);

    /**
     * Get rates.
     *
     * @param ratesCO
     *            - Order Rates Criteria.
     * @throws Exception
     *             - application exception
     * @return return the final rates in ascending order.
     */
    List<LtlPricingResult> getRates(GetOrderRatesCO ratesCO) throws Exception;
}
