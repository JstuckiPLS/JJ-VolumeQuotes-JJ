package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.LtlServiceType;

/**
 * VO that contains pricing details of WS response.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingDetailsWSResult implements Serializable {
    private static final long serialVersionUID = 6564189793036309481L;

    //The Profile Id of the profile picked for carrier.
    //In CTSI it is "Movement ID" and user wants this to be displayed in the popup of the pricing details.
    private Long profileId;
    private String scac;
    private String carrierName;
    private Currency currencyCode;
    private BigDecimal totalCost;
    private Integer totalMiles;
    private Date transitDate;
    private Integer transitDays;
    private String prohibitedCommodities;
    private LtlServiceType serviceType;
    private BigDecimal newProdLiability;
    private BigDecimal usedProdLiability;
    private LtlPricingCostDetailsWSResult costDetails;

    public Long getProfileId() {
        return profileId;
    }
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
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
    public Currency getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    public Integer getTotalMiles() {
        return totalMiles;
    }
    public void setTotalMiles(Integer totalMiles) {
        this.totalMiles = totalMiles;
    }
    public Date getTransitDate() {
        return transitDate;
    }
    public void setTransitDate(Date transitDate) {
        this.transitDate = transitDate;
    }
    public Integer getTransitDays() {
        return transitDays;
    }
    public void setTransitDays(Integer transitDays) {
        this.transitDays = transitDays;
    }
    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }
    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }
    public LtlServiceType getServiceType() {
        return serviceType;
    }
    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }
    public BigDecimal getNewProdLiability() {
        return newProdLiability;
    }
    public void setNewProdLiability(BigDecimal newProdLiability) {
        this.newProdLiability = newProdLiability;
    }
    public BigDecimal getUsedProdLiability() {
        return usedProdLiability;
    }
    public void setUsedProdLiability(BigDecimal usedProdLiability) {
        this.usedProdLiability = usedProdLiability;
    }
    public LtlPricingCostDetailsWSResult getCostDetails() {
        return costDetails;
    }
    public void setCostDetails(LtlPricingCostDetailsWSResult costDetails) {
        this.costDetails = costDetails;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("profileId", profileId)
                .append("scac", scac)
                .append("carrierName", carrierName)
                .append("currencyCode", currencyCode)
                .append("totalCost", totalCost)
                .append("totalMiles", totalMiles)
                .append("transitDate", transitDate)
                .append("transitDays", transitDays)
                .append("prohibitedCommodities", prohibitedCommodities)
                .append("serviceType", serviceType)
                .append("newProdLiability", newProdLiability)
                .append("usedProdLiability", usedProdLiability)
                .append("costDetails", costDetails);
        return builder.toString();
    }

}
