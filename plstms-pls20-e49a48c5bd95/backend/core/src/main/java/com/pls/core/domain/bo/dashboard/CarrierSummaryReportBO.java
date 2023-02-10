package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;
import java.util.Date;

/**
 * carrier_summary_proc been.
 * 
 * @author Alexander Nalapko
 */
public class CarrierSummaryReportBO {
    private Long orgID;
    private String scac;
    private Date shipDate;
    private BigDecimal weight;
    private BigDecimal fuelRev;
    private BigDecimal lhRev;
    private Long lCount;
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

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getFuelRev() {
        return fuelRev;
    }

    public void setFuelRev(BigDecimal fuelRev) {
        this.fuelRev = fuelRev;
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

    public BigDecimal getShipperBench() {
        return shipperBench;
    }

    public void setShipperBench(BigDecimal shipperBench) {
        this.shipperBench = shipperBench;
    }

    public BigDecimal getBmSavingsPercent() {
        return bmSavingsPercent;
    }

    public void setBmSavingsPercent(BigDecimal bmSavingsPercent) {
        this.bmSavingsPercent = bmSavingsPercent;
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
}
