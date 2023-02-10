package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.enums.LtlServiceType;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingDetailType;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Class to capture Guaranteed information for calculating carrier rates.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlRatingGuaranteedVO implements Serializable, Comparable<LtlRatingGuaranteedVO> {

    private static final long serialVersionUID = 8231643876236763231L;

    private Long profileId;
    private PricingDetailType profileDetailType;
    private PricingType pricingType;
    private Long carrierOrgId;
    private String applyBeforeFuel;
    private String bolCarrierName;
    private String chargeRuleType;
    private BigDecimal unitCost;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    private BigDecimal unitMargin;
    private BigDecimal minMarginFlat;
    private Integer time;
    private String origin;
    private String destination;
    private MoveType movementType;
    private LtlServiceType serviceType;

    public Long getProfileId() {
        return profileId;
    }
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
    public String getApplyBeforeFuel() {
        return applyBeforeFuel;
    }
    public void setApplyBeforeFuel(String applyBeforeFuel) {
        this.applyBeforeFuel = applyBeforeFuel;
    }
    public String getBolCarrierName() {
        return bolCarrierName;
    }
    public void setBolCarrierName(String bolCarrierName) {
        this.bolCarrierName = bolCarrierName;
    }
    public String getChargeRuleType() {
        return chargeRuleType;
    }
    public void setChargeRuleType(String chargeRuleType) {
        this.chargeRuleType = chargeRuleType;
    }
    public BigDecimal getUnitCost() {
        return unitCost;
    }
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
    public BigDecimal getMinCost() {
        return minCost;
    }
    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
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
    public Integer getTime() {
        return time;
    }
    public void setTime(Integer time) {
        this.time = time;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
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
    public Long getCarrierOrgId() {
        return carrierOrgId;
    }
    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
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
    public void setServiceType(String serviceType) {
        this.serviceType = StringUtils.isBlank(serviceType) ? null : LtlServiceType.valueOf(serviceType);
    }
    public LtlServiceType getServiceType() {
        return serviceType;
    }
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("profileId", profileId)
                .append("profileDetailType", profileDetailType)
                .append("pricingType", pricingType)
                .append("carrierOrgId", carrierOrgId)
                .append("applyBeforeFuel", applyBeforeFuel)
                .append("bolCarrierName", bolCarrierName)
                .append("chargeRuleType", chargeRuleType)
                .append("unitCost", unitCost)
                .append("minCost", minCost)
                .append("maxCost", maxCost)
                .append("unitMargin", unitMargin)
                .append("minMarginFlat", minMarginFlat)
                .append("time", time)
                .append("origin", origin)
                .append("destination", destination)
                .append("movementType", movementType)
                .append("serviceType", serviceType);

        return builder.toString();
    }

    /**
     * Method to compare objects for getting the ordered list.
     * @param comparableObj - Object to compare.
     * @return the compared int value.
     */
    public int compareTo(LtlRatingGuaranteedVO comparableObj) {

        int compareTime = comparableObj.getTime();

        //ascending order
        return this.time - compareTime;
    }
}
