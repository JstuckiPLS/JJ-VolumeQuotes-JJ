package com.pls.ltlrating.shared;

import com.pls.ltlrating.domain.enums.PricingType;

/**
 * DTO Object for freight analysis tarifs.
 *
 * @author Brichak Aleksandr
 *
 */
public class FreightAnalysisTariffBO {

    private Long id;
    private String rateName;
    private String smc3TariffName;
    private PricingType pricingType;
    private Long ltlPricingProfileId;
    private Long customerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public String getSmc3TariffName() {
        return smc3TariffName;
    }

    public void setSmc3TariffName(String smc3TariffName) {
        this.smc3TariffName = smc3TariffName;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public Long getLtlPricingProfileId() {
        return ltlPricingProfileId;
    }

    public void setLtlPricingProfileId(Long ltlPricingProfileId) {
        this.ltlPricingProfileId = ltlPricingProfileId;
    }
}
