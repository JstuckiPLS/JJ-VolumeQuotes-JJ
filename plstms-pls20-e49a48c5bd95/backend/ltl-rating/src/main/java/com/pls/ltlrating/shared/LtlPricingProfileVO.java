package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * The Value Object that is returned to grid when multiple Pricing profiles are requested based on criteria.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingProfileVO implements Serializable {

    private static final long serialVersionUID = 502823579331132323L;

    private Long ltlPricingProfileId;
    private Long carrierOrgId;
    private String scac = "ALL";
    private String carrierName = "ALL";
    private String pricingType;
    private String rateName;
    private Date effDate;
    private Date expDate;
    private String smc3TariffName;

    public Long getLtlPricingProfileId() {
        return ltlPricingProfileId;
    }
    public void setLtlPricingProfileId(Long ltlPricingProfileId) {
        this.ltlPricingProfileId = ltlPricingProfileId;
    }
    public Long getCarrierOrgId() {
        return carrierOrgId;
    }
    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }
    public String getScac() {
        return scac;
    }
    public void setScac(String scac) {
        this.scac = scac;
    }
    public String getCarrierName() {
        return carrierName;
    }
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    public String getPricingType() {
        return pricingType;
    }
    public void setPricingType(String pricingType) {
        this.pricingType = pricingType;
    }
    public String getRateName() {
        return rateName;
    }
    public void setRateName(String rateName) {
        this.rateName = rateName;
    }
    public Date getEffDate() {
        return effDate;
    }
    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }
    public Date getExpDate() {
        return expDate;
    }
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
    public String getSmc3TariffName() {
        return smc3TariffName;
    }
    public void setSmc3TariffName(String smc3TariffName) {
        this.smc3TariffName = smc3TariffName;
    }

}
