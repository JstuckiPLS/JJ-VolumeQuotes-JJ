package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;

/**
 * customer_summary_outbound_proc been.
 * 
 * @author Alexander Nalapko
 */
public class CustomerSummaryReportBO {
    private Long orgID;
    private String customer;
    private String destState;
    private BigDecimal percentLCount;
    private BigDecimal weight;
    private BigDecimal fuelRev;
    private Long lCount;
    private BigDecimal lhRev;
    private BigDecimal accRev;
    private BigDecimal sumTotal;
    private BigDecimal shipperBench;
    private BigDecimal bmSavingsPercent;
    private BigDecimal savings;
    private BigDecimal sumTotalShipm;

    public Long getOrgID() {
        return orgID;
    }

    public void setOrgID(Long orgID) {
        this.orgID = orgID;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLhRev() {
        return lhRev;
    }

    public void setLhRev(BigDecimal lhRev) {
        this.lhRev = lhRev;
    }

    public BigDecimal getAccRev() {
        return accRev;
    }

    public void setAccRev(BigDecimal accRev) {
        this.accRev = accRev;
    }

    public BigDecimal getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(BigDecimal sumTotal) {
        this.sumTotal = sumTotal;
    }

    public BigDecimal getFuelRev() {
        return fuelRev;
    }

    public void setFuelRev(BigDecimal fuelRev) {
        this.fuelRev = fuelRev;
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

    public BigDecimal getSumTotalShipm() {
        return sumTotalShipm;
    }

    public void setSumTotalShipm(BigDecimal sumTotalShipm) {
        this.sumTotalShipm = sumTotalShipm;
    }

    public BigDecimal getBmSavingsPercent() {
        return bmSavingsPercent;
    }

    public void setBmSavingsPercent(BigDecimal bmSavingsPercent) {
        this.bmSavingsPercent = bmSavingsPercent;
    }

    /**
     * l_count.
     * 
     * @return {@link Long}
     */
    public Long getlCount() {
        return lCount;
    }

    /**
     * l_count.
     * 
     * @param lCount
     *            {@link Long}
     */
    public void setlCount(Long lCount) {
        this.lCount = lCount;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public BigDecimal getPercentLCount() {
        return percentLCount;
    }

    public void setPercentLCount(BigDecimal percentLCount) {
        this.percentLCount = percentLCount;
    }
}
