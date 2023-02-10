package com.pls.ltlrating.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * VO for pricing details of one pricing profile detail.
 *
 * @author Aleksandr Leshchenko
 */
public class CarrierPricingProfilesVO {
    private LtlRatingProfileVO pricingDetails;
    private LtlRatingFSTriggerVO fuelSurcharge;
    private Map<Integer, LtlRatingGuaranteedVO> guaranteedPricing = new TreeMap<Integer, LtlRatingGuaranteedVO>();
    private Map<String, AccessorialPricingVO> ltlAccessorials = new HashMap<String, AccessorialPricingVO>();
    private Map<String, AccessorialPricingVO> addlAccessorials = new HashMap<String, AccessorialPricingVO>();

    public LtlRatingProfileVO getPricingDetails() {
        return pricingDetails;
    }

    public void setPricingDetails(LtlRatingProfileVO pricingDetails) {
        this.pricingDetails = pricingDetails;
    }

    public LtlRatingFSTriggerVO getFuelSurcharge() {
        return fuelSurcharge;
    }

    public void setFuelSurcharge(LtlRatingFSTriggerVO fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }

    public Map<Integer, LtlRatingGuaranteedVO> getGuaranteedPricing() {
        return guaranteedPricing;
    }

    public void setGuaranteedPricing(Map<Integer, LtlRatingGuaranteedVO> guaranteedPricing) {
        this.guaranteedPricing = guaranteedPricing;
    }

    public Map<String, AccessorialPricingVO> getLtlAccessorials() {
        return ltlAccessorials;
    }

    public void setLtlAccessorials(Map<String, AccessorialPricingVO> ltlAccessorials) {
        this.ltlAccessorials = ltlAccessorials;
    }

    public Map<String, AccessorialPricingVO> getAddlAccessorials() {
        return addlAccessorials;
    }

    public void setAddlAccessorials(Map<String, AccessorialPricingVO> addlAccessorials) {
        this.addlAccessorials = addlAccessorials;
    }
}
