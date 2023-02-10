package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DESTINATION_REPORT been.
 * 
 * @author Alexander Nalapko
 */
public class ShipmentOverviewReportBO {
    private Long orgID;
    private Long loadID;
    private String bound;
    private Date shipDate;
    private Long lCount;
    private BigDecimal weight;
    private BigDecimal lhRev;
    private BigDecimal fuelRev;
    private BigDecimal accRev;
    private BigDecimal sumTotal;
    private BigDecimal shipperBench;
    private BigDecimal savings;
    private BigDecimal bmSavingsPercent;
    private BigDecimal sumTotalShipm;

    public Long getOrgID() {
        return orgID;
    }

    public void setOrgID(Long orgID) {
        this.orgID = orgID;
    }

    public Long getLoadID() {
        return loadID;
    }

    public void setLoadID(Long loadID) {
        this.loadID = loadID;
    }

    public String getBound() {
        return bound;
    }

    public void setBound(String bound) {
        this.bound = bound;
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

    public BigDecimal getLhRev() {
        return lhRev;
    }

    public void setLhRev(BigDecimal lhRev) {
        this.lhRev = lhRev;
    }

    public BigDecimal getFuelRev() {
        return fuelRev;
    }

    public void setFuelRev(BigDecimal fuelRev) {
        this.fuelRev = fuelRev;
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

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }

    public BigDecimal getBmSavingsPercent() {
        return bmSavingsPercent;
    }

    public void setBmSavingsPercent(BigDecimal bmSavingsPercent) {
        this.bmSavingsPercent = bmSavingsPercent;
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
