package com.pls.ltlrating.service;

import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingWSResult;

/**
 * Class where we calculate LTL rates for the given parameters for the customer by making WS calls.
 *
 * @author Hima Bindu Challa
 *
 */
public interface LtlPricingEngineWSService {

    /**
     * Get rates for customer by customer credentials and criteria.
     * @param ratesCO - Order Rates Criteria.
     * @throws Exception - application exception
     * @return return the final rates in ascending order.
     */
    LtlPricingWSResult getRatesForCustomer(GetOrderRatesCO ratesCO) throws Exception;

}
