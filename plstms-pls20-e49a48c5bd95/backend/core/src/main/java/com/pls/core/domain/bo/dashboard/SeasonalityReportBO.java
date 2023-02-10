package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * shipment_seasonality_proc been.
 * 
 * @author Alexander Nalapko
 */
public class SeasonalityReportBO {

    private List<String> month = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December");

    private Long orgID;
    private String shipDateMonth;
    private String destState;
    private BigDecimal percentLCount;
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

    public String getShipDateMonth() {
        return shipDateMonth;
    }

    /**
     * DB contain null value, in Month column.
     * @param shipDateMonth number of month.
     */
    public void setShipDateMonth(Integer shipDateMonth) {
        if (shipDateMonth > 1) {
            this.shipDateMonth = month.get(shipDateMonth - 1);
        }
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public BigDecimal getFuelRev() {
        return fuelRev;
    }

    public void setFuelRev(BigDecimal fuelRev) {
        this.fuelRev = fuelRev;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setLhRev(BigDecimal lhRev) {
        this.lhRev = lhRev;
    }

    public BigDecimal getAccRev() {
        return accRev;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLhRev() {
        return lhRev;
    }

    public void setAccRev(BigDecimal accRev) {
        this.accRev = accRev;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }

    public BigDecimal getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(BigDecimal sumTotal) {
        this.sumTotal = sumTotal;
    }

    public BigDecimal getBmSavingsPercent() {
        return bmSavingsPercent;
    }

    public void setBmSavingsPercent(BigDecimal bmSavingsPercent) {
        this.bmSavingsPercent = bmSavingsPercent;
    }

    public BigDecimal getShipperBench() {
        return shipperBench;
    }

    public void setShipperBench(BigDecimal shipperBench) {
        this.shipperBench = shipperBench;
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

    public BigDecimal getPercentLCount() {
        return percentLCount;
    }

    public void setPercentLCount(BigDecimal percentLCount) {
        this.percentLCount = percentLCount;
    }
}
