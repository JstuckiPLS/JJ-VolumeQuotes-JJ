package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.ltlrating.domain.enums.PricingDetailType;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Result VO that contains selected rate profile details.
 * @author Hima Bindu Challa
 *
 */
public class LtlRatingFSTriggerVO implements Serializable {

    private static final long serialVersionUID = 5341767167382136323L;

    private Long profileId;
    private Long profileDetailId;
    private PricingDetailType profileDetailType; // TODO rename to pricing detail type
    private PricingType pricingType;
    private Long carrierOrgId;
    private Long ltlFuelId;
    private Long dotRegionId;
    private BigDecimal surcharge;
    private String upchargeType;
    private BigDecimal upchargeFlat;
    private BigDecimal upchargePercent;
    private String effDay;
    private Integer geoLevel;
    private Long ltlFuelSurchargeId;
    private Boolean isExcludeFuel;

    public Long getLtlFuelSurchargeId() {
        return ltlFuelSurchargeId;
    }
    public void setLtlFuelSurchargeId(Long ltlFuelSurchargeId) {
        this.ltlFuelSurchargeId = ltlFuelSurchargeId;
    }
    public Long getProfileId() {
        return profileId;
    }
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
    public Long getProfileDetailId() {
        return profileDetailId;
    }
    public void setProfileDetailId(Long profileDetailId) {
        this.profileDetailId = profileDetailId;
    }
    public Long getDotRegionId() {
        return dotRegionId;
    }
    public void setDotRegionId(Long dotRegionId) {
        this.dotRegionId = dotRegionId;
    }
    public BigDecimal getSurcharge() {
        return surcharge;
    }
    public void setSurcharge(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }
    public String getUpchargeType() {
        return upchargeType;
    }
    public void setUpchargeType(String upchargeType) {
        this.upchargeType = upchargeType;
    }
    public BigDecimal getUpchargeFlat() {
        return upchargeFlat;
    }
    public void setUpchargeFlat(BigDecimal upchargeFlat) {
        this.upchargeFlat = upchargeFlat;
    }
    public BigDecimal getUpchargePercent() {
        return upchargePercent;
    }
    public void setUpchargePercent(BigDecimal upchargePercent) {
        this.upchargePercent = upchargePercent;
    }
    public String getEffDay() {
        return effDay;
    }
    public void setEffDay(String effDay) {
        this.effDay = effDay;
    }
    public PricingDetailType getProfileDetailType() {
        return profileDetailType;
    }
    public void setProfileDetailType(String profileDetailType) {
        this.profileDetailType = StringUtils.isBlank(profileDetailType) ? null : PricingDetailType.valueOf(profileDetailType);
    }
    public void setProfileDetailTypeEnum(PricingDetailType profileDetailType) {
        this.profileDetailType = profileDetailType;
    }
    public PricingType getPricingType() {
        return pricingType;
    }
    public void setPricingType(String pricingType) {
        this.pricingType = StringUtils.isBlank(pricingType) ? null : PricingType.valueOf(pricingType);
    }
    public void setPricingTypeEnum(PricingType pricingType) {
        this.pricingType = pricingType;
    }
    public Long getCarrierOrgId() {
        return carrierOrgId;
    }
    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public Integer getGeoLevel() {
        return geoLevel;
    }
    public void setGeoLevel(Integer geoLevel) {
        this.geoLevel = geoLevel;
    }
    public Long getLtlFuelId() {
        return ltlFuelId;
    }
    public void setLtlFuelId(Long ltlFuelId) {
        this.ltlFuelId = ltlFuelId;
    }
    public Boolean getIsExcludeFuel() {
        return isExcludeFuel;
    }
    public void setIsExcludeFuel(Boolean isExcludeFuel) {
        this.isExcludeFuel = isExcludeFuel;
    }
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("profileId", profileId)
                .append("profileDetailId", profileDetailId)
                .append("pricingType", pricingType)
                .append("profileDetailType", profileDetailType)
                .append("carrierOrgId", carrierOrgId)
                .append("dotRegionId", dotRegionId)
                .append("surcharge", surcharge)
                .append("upchargeType", upchargeType)
                .append("upchargeFlat", upchargeFlat)
                .append("upchargePercent", upchargePercent)
                .append("effDay", effDay);

        return builder.toString();
    }
}
