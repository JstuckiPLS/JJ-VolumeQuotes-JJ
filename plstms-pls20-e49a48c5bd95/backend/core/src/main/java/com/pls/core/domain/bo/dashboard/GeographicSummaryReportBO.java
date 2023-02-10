package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;

/**
 * Describes geographical summary report data.
 * 
 * @author Dmitriy Nefedchenko
 */
public class GeographicSummaryReportBO {
    private Long customerId;
    private String destination;
    private String origin;
    private Long loadCount;
    private BigDecimal averageWeight;
    private BigDecimal linehaulRevenue;
    private BigDecimal fuelRevenue;
    private BigDecimal accessorialRevenue;
    private BigDecimal summaryTotal;
    private BigDecimal shipperBench;
    private BigDecimal savings;
    private BigDecimal summaryTotalShipment;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Long getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(Long loadCount) {
        this.loadCount = loadCount;
    }

    public BigDecimal getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(BigDecimal avarageWeight) {
        this.averageWeight = avarageWeight;
    }

    public BigDecimal getLinehaulRevenue() {
        return linehaulRevenue;
    }

    public void setLinehaulRevenue(BigDecimal linehaulRevenue) {
        this.linehaulRevenue = linehaulRevenue;
    }

    public BigDecimal getFuelRevenue() {
        return fuelRevenue;
    }

    public void setFuelRevenue(BigDecimal fuelRevenue) {
        this.fuelRevenue = fuelRevenue;
    }

    public BigDecimal getAccessorialRevenue() {
        return accessorialRevenue;
    }

    public void setAccessorialRevenue(BigDecimal accessorialRevenue) {
        this.accessorialRevenue = accessorialRevenue;
    }

    public BigDecimal getSummaryTotal() {
        return summaryTotal;
    }

    public void setSummaryTotal(BigDecimal summaryTotal) {
        this.summaryTotal = summaryTotal;
    }

    public BigDecimal getShipperBench() {
        return shipperBench;
    }

    public void setShipperBench(BigDecimal shipperBench) {
        this.shipperBench = shipperBench;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }

    public BigDecimal getSummaryTotalShipment() {
        return summaryTotalShipment;
    }

    public void setSummaryTotalShipment(BigDecimal summaryTotalShipment) {
        this.summaryTotalShipment = summaryTotalShipment;
    }
}
