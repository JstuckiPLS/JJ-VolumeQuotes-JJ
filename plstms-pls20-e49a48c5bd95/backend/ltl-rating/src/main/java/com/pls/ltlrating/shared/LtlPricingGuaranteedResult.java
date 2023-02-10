package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * VO that contains all final guaranteed information to display the same on Select Carrier screen.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingGuaranteedResult implements Serializable {

    private static final long serialVersionUID = 2964735491276329347L;

    private Integer guaranteedTime;
    private BigDecimal carrierGuaranteedCost;
    private BigDecimal shipperGuaranteedCost;
    private BigDecimal benchmarkGuaranteedCost;

    public Integer getGuaranteedTime() {
        return guaranteedTime;
    }
    public void setGuaranteedTime(Integer guaranteedTime) {
        this.guaranteedTime = guaranteedTime;
    }
    public BigDecimal getCarrierGuaranteedCost() {
        return carrierGuaranteedCost;
    }
    public void setCarrierGuaranteedCost(BigDecimal carrierGuaranteedCost) {
        this.carrierGuaranteedCost = carrierGuaranteedCost;
    }
    public BigDecimal getShipperGuaranteedCost() {
        return shipperGuaranteedCost;
    }
    public void setShipperGuaranteedCost(BigDecimal shipperGuaranteedCost) {
        this.shipperGuaranteedCost = shipperGuaranteedCost;
    }
    public BigDecimal getBenchmarkGuaranteedCost() {
        return benchmarkGuaranteedCost;
    }
    public void setBenchmarkGuaranteedCost(BigDecimal benchmarkGuaranteedCost) {
        this.benchmarkGuaranteedCost = benchmarkGuaranteedCost;
    }
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("guaranteedTime", guaranteedTime)
                .append("carrierGuaranteedCost", carrierGuaranteedCost)
                .append("shipperGuaranteedCost", shipperGuaranteedCost)
                .append("benchmarkGuaranteedCost", benchmarkGuaranteedCost);

        return builder.toString();
    }
}
