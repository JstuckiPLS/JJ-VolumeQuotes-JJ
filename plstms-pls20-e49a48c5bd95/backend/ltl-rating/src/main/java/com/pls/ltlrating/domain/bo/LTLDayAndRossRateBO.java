package com.pls.ltlrating.domain.bo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.pls.ltlrating.shared.LtlPricingAccessorialResult;

/**
 * LTL RATE DTO object.
 * 
 * @author Brichak Aleksandr
 * 
 */
public class LTLDayAndRossRateBO {

    private Date estimatedDeliveryDate;
    private BigDecimal totalCarrierCost;
    private BigDecimal initialCost;
    private Map<String, LtlPricingAccessorialResult> charges;
    private Integer transitDays;

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public BigDecimal getTotalCarrierCost() {
        return totalCarrierCost;
    }

    public void setTotalCarrierCost(BigDecimal totalCarrierCost) {
        this.totalCarrierCost = totalCarrierCost;
    }

    public Map<String, LtlPricingAccessorialResult> getCharges() {
        return charges;
    }

    public void setCharges(Map<String, LtlPricingAccessorialResult> charges) {
        this.charges = charges;
    }

    public Integer getTransitDays() {
        return transitDays;
    }

    public void setTransitDays(Integer transitDays) {
        this.transitDays = transitDays;
    }

    public BigDecimal getInitialCost() {
        return initialCost;
    }

    public void setInitialCost(BigDecimal initialCost) {
        this.initialCost = initialCost;
    }

}
