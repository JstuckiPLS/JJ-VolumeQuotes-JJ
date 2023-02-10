package com.pls.ltlrating.service;

import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlRatingVO;
import com.pls.smc3.dto.LTLRateShipmentDTO;

/**
 * Service for getting prices from SMC3.
 *
 * @author Aleksandr Leshchenko
 */
public interface LtlSMC3Service {
    /**
     * Method to populate rates from SMC3.
     *
     * @param ratesCO
     *            pricing details to populate criteria for SMC3 call.
     * @param ratingProfiles
     *            pricing profiles to populate criteria for SMC3 call.
     */
    void populateSMC3Rates(GetOrderRatesCO ratesCO, LtlRatingVO... ratingProfiles);

    /**
     * Method to get rates from SMC3.
     *
     * @param smc3Crit
     *            - SMC3 criteria to get rates from SMC3.
     * @return LTLRateShipmentDTO which contains SMC3 rates for given lane.
     * @throws Exception
     *             - exception from SMC3.
     */
    LTLRateShipmentDTO getRatesFromSMC3(LTLRateShipmentDTO smc3Crit) throws Exception;
}
