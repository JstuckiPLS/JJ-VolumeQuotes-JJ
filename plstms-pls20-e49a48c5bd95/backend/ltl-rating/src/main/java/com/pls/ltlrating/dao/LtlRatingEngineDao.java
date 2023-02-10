package com.pls.ltlrating.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pls.ltlrating.domain.enums.FuelWeekDays;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlRatingAccessorialsVO;
import com.pls.ltlrating.shared.LtlRatingFSTriggerVO;
import com.pls.ltlrating.shared.LtlRatingGuaranteedVO;
import com.pls.ltlrating.shared.LtlRatingProfileLTLLCSummaryVO;
import com.pls.ltlrating.shared.LtlRatingProfileVO;

/**
 * Class where we get LTL rate profiles for the given parameters.
 *
 * @author Hima Bindu Challa
 *
 */
public interface LtlRatingEngineDao {

    /**
     * Method to get Customer Margin setup values.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @return - Customer Margin
     */
    List<LtlRatingProfileVO> getCustomerPricingProfile(GetOrderRatesCO criteria);

    /**
     * Method to get Benchmark profiles for the given customer and parameters.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @param carrierOrgIds
     *            - list of applicable carrier Org Ids.
     * @return - Benchmark profiles
     */
    List<LtlRatingProfileVO> getBenchmarkRates(GetOrderRatesCO criteria, Set<Long> carrierOrgIds);

    /**
     * Method to get Carrier rate profiles for the given customer and parameters.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @param palletCarriers
     *            - pallet carriers org List.
     * @return - Carrier rates
     */
    List<LtlRatingProfileVO> getCarrierRates(GetOrderRatesCO criteria, List<Long> palletCarriers);

    /**
     * Method to get Guaranteed rate profiles for the parameters and the valid profile detail Ids.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @param profileDetailIds
     *            - Set of valid profile detail ids.
     * @return - Guaranteed rates
     */
    List<LtlRatingGuaranteedVO> getGuaranteedRates(GetOrderRatesCO criteria, Set<Long> profileDetailIds);

    /**
     * Method to get accessorial rate profiles for the given criteria, both LTL and normal accessorials and
     * valid profile detail ids.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @param isSpecificAccessorials
     *            if <code>true</code> then get only LTL accessorials specified in criteria otherwise get only
     *            Additional Accessorials.
     * @param profileDetailIds
     *            - Set of valid profile detail ids.
     * @return - Accessorial rates
     */
    List<LtlRatingAccessorialsVO> getAccessorialRates(GetOrderRatesCO criteria, boolean isSpecificAccessorials, Set<Long> profileDetailIds);

    /**
     * Method to get Fuel Surcharge triggers for the given criteria, valid set of profile detail Ids, and
     * effective days.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @param profileDetailIds
     *            - Set of valid profile detail ids.
     * @param fsEffDays
     *            - Fuel Surcharge effective days map.
     * @return - Fuel Surcharge rates
     */
    List<LtlRatingFSTriggerVO> getFSTriggers(GetOrderRatesCO criteria, Set<Long> profileDetailIds, Map<FuelWeekDays, Date> fsEffDays);

    /**
     * Method to get Pallet rate profiles for the given criteria and valid profile detail Ids.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @param pricingTypes
     *            - Pricing Types for which we need to get Pallet pricing.
     * @param bmCarrierOrgIds
     *            - list of applicable carrier Org Ids to get Benchmark pricing.
     * @return - Pallet rates
     */
    List<LtlRatingProfileVO> getPalletRates(GetOrderRatesCO criteria, List<PricingType> pricingTypes, Set<Long> bmCarrierOrgIds);

    /**
     * Method to get rate profiles for the given criteria and valid buy/sell profile detail Ids with use
     * blanket flag set to true.
     *
     * @param criteria
     *            - parameters list to get rates.
     * @param carriersWithPricing
     *            - carrier profiles whose pricing details have been already pulled.
     * @param pricProfDtlId
     *            - pricing profile detail id's of carrier profiles whose pricing details have been already
     *            pulled.
     * @return - List of carrier rates.
     */
    List<LtlRatingProfileVO> getCarrierRatesWithUseBlanket(GetOrderRatesCO criteria, List<Long> carriersWithPricing,
            List<Long> pricProfDtlId);

    /**
     * Get blanket API carrier profiles.
     * 
     * @param shipperOrgId
     *            ID of customer
     * @param shipDate
     *            estimated pickup date
     * @return list of carrier rates
     */
    List<LtlRatingProfileVO> getBlanketAPIProfiles(Long shipperOrgId, Date shipDate);

    /**
     * Get LTLLC carrier profiles. (Blanket + csp without pricings(the ones without any geolocation specific pricing))
     * 
     * @param criteria - parameter list to get rates.
     * @return list of carrier rates
     */
    List<LtlRatingProfileVO> getLTLLCProfiles(GetOrderRatesCO criteria);
    
    /**
     * Pre-select information about active profiles, required to get quotes from LTLLC
     * 
     * @param criteria - parameter list to get rates.
     * @return list of profile summaries
     */
    List<LtlRatingProfileLTLLCSummaryVO> getLTLLCProfileSummaries(GetOrderRatesCO criteria);

}
