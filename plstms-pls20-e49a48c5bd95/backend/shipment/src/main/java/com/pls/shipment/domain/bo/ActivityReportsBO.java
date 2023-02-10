package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

import org.springframework.util.ReflectionUtils;
/**
 * Reports BO.
 * 
 * @author Sergii Belodon
 * 
 */
public class ActivityReportsBO extends FreightAnalysisReportBO {
    private BigDecimal totalWeight;
    private BigDecimal customerAccCost;
    private BigDecimal customerTotalCost;
    private BigDecimal customerCostPerPound;

    /**
     * Instantiates a new activity reports bo.
     *
     * @param freightAnalysisReportBO the freight analysis report bo
     */
    public ActivityReportsBO(FreightAnalysisReportBO freightAnalysisReportBO) {
        super();
        ReflectionUtils.shallowCopyFieldState(freightAnalysisReportBO, this);
    }
    public BigDecimal getTotalWeight() {
        return totalWeight;
    }
    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }
    public BigDecimal getCustomerAccCost() {
        return customerAccCost;
    }
    public void setCustomerAccCost(BigDecimal customerAccCost) {
        this.customerAccCost = customerAccCost;
    }
    public BigDecimal getCustomerTotalCost() {
        return customerTotalCost;
    }
    public void setCustomerTotalCost(BigDecimal customerTotalCost) {
        this.customerTotalCost = customerTotalCost;
    }
    public BigDecimal getCustomerCostPerPound() {
        return customerCostPerPound;
    }
    public void setCustomerCostPerPound(BigDecimal customerCostPerPound) {
        this.customerCostPerPound = customerCostPerPound;
    }
}
