package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * The Value Object that is returned to grid when multiple Pricing profiles are requested based on criteria.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlCustomerPricingProfileVO implements Serializable {

    private static final long serialVersionUID = 502823579331132323L;

    private Long ltlPricingProfileId;
    private Long carrierOrgId;
    private Long shipperOrgId;
    private String scac;
    private String carrierName;
    private String pricingType;
    private String pricingTypeDesc;
    private Date effDate;
    private Date expDate;
    private Boolean blocked = false;
    private Boolean tier1 = false;
    private String carrierType;

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
    public Long getShipperOrgId() {
        return shipperOrgId;
    }
    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
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
    public String getPricingTypeDesc() {
        return pricingTypeDesc;
    }
    public void setPricingTypeDesc(String pricingTypeDesc) {
        this.pricingTypeDesc = pricingTypeDesc;
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
    public Boolean getBlocked() {
        return blocked;
    }
    public void setBlocked(String blocked) {
        this.blocked = StringUtils.isBlank(blocked) ? null : Boolean.valueOf(blocked);
    }
    public Boolean getTier1() {
        return tier1;
    }
    public void setTier1(String tier1) {
        this.tier1 = StringUtils.isBlank(tier1) ? null : Boolean.valueOf(tier1);
    }
    public String getCarrierType() {
        return carrierType;
    }
    public void setCarrierType(String carrierType) {
        this.carrierType = carrierType;
    }
}
