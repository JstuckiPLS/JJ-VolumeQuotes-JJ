package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.enums.LtlServiceType;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingDetailType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.domain.enums.UnitType;

/**
 * Class to capture accessorials details for calculating carrier rates for given lanes.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlRatingAccessorialsVO implements Serializable {

    private static final long serialVersionUID = 4218736713236763231L;

    private Long profileId;
    private Long profileDetailId;
    private PricingType pricingType;
    private PricingDetailType profileDetailType;
    private Long accessorialId;
    private String accessorialType;
    private String description;
    private String accessorialGroup;
    private UnitType costType;
    private BigDecimal unitCost;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    private UnitType marginType;
    private BigDecimal unitMargin;
    private BigDecimal minMarginFlat;
    private Integer geoLevel;
    private Long carrierOrgId;
    private MoveType movementType;
    private LtlServiceType serviceType;
    private BigDecimal minLength;
    private BigDecimal minWidth;
    private String applyBeforeFuel;

    public BigDecimal getMinLength() {
        return minLength;
    }

    public void setMinLength(BigDecimal minLength) {
        this.minLength = minLength;
    }
    
    public BigDecimal getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(BigDecimal minWidth) {
        this.minWidth = minWidth;
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
    public Long getAccessorialId() {
        return accessorialId;
    }
    public void setAccessorialId(Long accessorialId) {
        this.accessorialId = accessorialId;
    }
    public String getAccessorialType() {
        return accessorialType;
    }
    public void setAccessorialType(String accessorialType) {
        this.accessorialType = accessorialType;
    }
    public UnitType getCostType() {
        return costType;
    }
    public void setCostType(String costType) {
        this.costType = StringUtils.isBlank(costType) ? null : UnitType.getByName(costType);
    }
    public BigDecimal getUnitCost() {
        return unitCost;
    }
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
    public UnitType getMarginType() {
        return marginType;
    }
    public void setMarginType(String marginType) {
        this.marginType = StringUtils.isBlank(marginType) ? null : UnitType.getByName(marginType);
    }
    public BigDecimal getUnitMargin() {
        return unitMargin;
    }
    public void setUnitMargin(BigDecimal unitMargin) {
        this.unitMargin = unitMargin;
    }
    public BigDecimal getMinMarginFlat() {
        return minMarginFlat;
    }
    public void setMinMarginFlat(BigDecimal minMarginFlat) {
        this.minMarginFlat = minMarginFlat;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getCarrierOrgId() {
        return carrierOrgId;
    }
    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }
    public String getAccessorialGroup() {
        return accessorialGroup;
    }
    public void setAccessorialGroup(String accessorialGroup) {
        this.accessorialGroup = accessorialGroup;
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
    public PricingDetailType getProfileDetailType() {
        return profileDetailType;
    }
    public void setProfileDetailType(String profileDetailType) {
        this.profileDetailType = StringUtils.isBlank(profileDetailType) ? null : PricingDetailType.valueOf(profileDetailType);
    }
    public void setProfileDetailTypeEnum(PricingDetailType profileDetailType) {
        this.profileDetailType = profileDetailType;
    }
    public Integer getGeoLevel() {
        return geoLevel;
    }
    public void setGeoLevel(Integer geoLevel) {
        this.geoLevel = geoLevel;
    }
    public BigDecimal getMinCost() {
        return minCost;
    }
    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }
    public BigDecimal getMaxCost() {
        return maxCost;
    }
    public void setMaxCost(BigDecimal maxCost) {
        this.maxCost = maxCost;
    }
    public MoveType getMovementType() {
        return movementType;
    }
    public void setMovementType(String movementType) {
        this.movementType = StringUtils.isBlank(movementType) ? null : MoveType.valueOf(movementType);
    }
    public LtlServiceType getServiceType() {
        return serviceType;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = StringUtils.isBlank(serviceType) ? null : LtlServiceType.valueOf(serviceType);
    }
    public String getApplyBeforeFuel() {
        return applyBeforeFuel;
    }
    public void setApplyBeforeFuel(String applyBeforeFuel) {
        this.applyBeforeFuel = applyBeforeFuel;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("profileId", profileId)
                .append("profileDetailId", profileDetailId)
                .append("pricingType", pricingType)
                .append("profileDetailType", profileDetailType)
                .append("accessorialId", accessorialId)
                .append("accessorialType", accessorialType)
                .append("description", description)
                .append("accessorialGroup", accessorialGroup)
                .append("costType", costType)
                .append("unitCost", unitCost)
                .append("minCost", unitCost)
                .append("maxCost", unitCost)
                .append("marginType", marginType)
                .append("unitMargin", unitMargin)
                .append("minMarginFlat", minMarginFlat)
                .append("carrierOrgId", carrierOrgId)
                .append("movementType", movementType)
                .append("serviceType", serviceType);

        return builder.toString();
    }
}
