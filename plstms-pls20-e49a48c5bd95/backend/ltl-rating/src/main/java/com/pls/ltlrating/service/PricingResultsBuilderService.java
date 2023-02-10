package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.LtlRatingMarginVO;
import com.pls.ltlrating.shared.LtlRatingVO;

/**
 * Service to build pricing result BO.
 * 
 * @author Aleksandr Leshchenko
 */
public interface PricingResultsBuilderService {

    /**
     * Get pricing results based on provided information.
     * 
     * @param ratesCO
     *            criteria object used for pricing call
     * @param orgPricing
     *            organization pricing entity (can be <code>null</code> if customer was not specified)
     * @param carrierProfileVO
     *            carrier rates applicable for given criteria object
     * @param benchmarkProfileVO
     *            benchmark rates applicable for given criteria object (can be <code>null</code> if customer
     *            was not specified or if no benchmark profiles exist)
     * @param margin
     *            default or customer margin settings
     * @return list of pricing results for each carrier within provided carrier rates
     * @throws InterruptedException
     *             unlikely to happen
     */
    List<LtlPricingResult> getPricingResults(GetOrderRatesCO ratesCO, OrganizationPricingEntity orgPricing, LtlRatingVO carrierProfileVO,
            LtlRatingVO benchmarkProfileVO, LtlRatingMarginVO margin) throws InterruptedException;

}
