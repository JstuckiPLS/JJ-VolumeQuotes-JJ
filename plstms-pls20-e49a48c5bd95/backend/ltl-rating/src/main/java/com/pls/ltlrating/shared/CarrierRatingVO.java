package com.pls.ltlrating.shared;

import com.pls.ltlrating.domain.enums.PricingType;

/**
 * VO for pricing details of one pricing profile.
 *
 * @author Aleksandr Leshchenko
 */
public class CarrierRatingVO {
    private Long profileId;
    private PricingType pricingType;

    private CarrierPricingProfilesVO rate;
    private CarrierPricingProfilesVO shipperRate;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public CarrierPricingProfilesVO getRate() {
        return rate;
    }

    public void setRate(CarrierPricingProfilesVO rate) {
        this.rate = rate;
    }

    public CarrierPricingProfilesVO getShipperRate() {
        return shipperRate;
    }

    public void setShipperRate(CarrierPricingProfilesVO shipperRate) {
        this.shipperRate = shipperRate;
    }
}
