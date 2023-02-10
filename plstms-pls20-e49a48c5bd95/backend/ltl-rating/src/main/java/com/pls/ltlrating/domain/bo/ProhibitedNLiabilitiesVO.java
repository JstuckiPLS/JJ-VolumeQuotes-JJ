package com.pls.ltlrating.domain.bo;

import java.io.Serializable;
import java.util.List;

import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;

/**
 * Business object that is used to save the data for prohibited commodities and liabilities.
 *
 * @author Pavani Challa
 *
 */
public class ProhibitedNLiabilitiesVO implements Serializable {

    private static final long serialVersionUID = 4637898484100094513L;

    private LtlPricingProfileEntity profile;

    private List<LtlCarrierLiabilitiesEntity> liabilities;

    public LtlPricingProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(LtlPricingProfileEntity profile) {
        this.profile = profile;
    }

    public List<LtlCarrierLiabilitiesEntity> getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(List<LtlCarrierLiabilitiesEntity> liabilities) {
        this.liabilities = liabilities;
    }


}
