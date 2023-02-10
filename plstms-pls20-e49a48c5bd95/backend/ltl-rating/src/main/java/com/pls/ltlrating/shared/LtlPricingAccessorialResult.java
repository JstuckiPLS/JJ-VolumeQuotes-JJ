package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * VO that contains all final accessorial information to display the same on Select Carrier screen.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingAccessorialResult implements Serializable {
    private static final long serialVersionUID = -1257932522390644855L;

    private String accessorialType;
    private String accessorialDescription;
    private String accessorialGroup;
    private BigDecimal carrierAccessorialCost;
    private boolean applyCarrierCostBeforeFuel;
    private BigDecimal shipperAccessorialCost;
    private boolean applyShipperCostBeforeFuel;
    private BigDecimal benchmarkAccessorialCost;
    private boolean applyBenchmarkCostBeforeFuel;

    public String getAccessorialType() {
        return accessorialType;
    }
    public void setAccessorialType(String accessorialType) {
        this.accessorialType = accessorialType;
    }
    public String getAccessorialDescription() {
        return accessorialDescription;
    }
    public void setAccessorialDescription(String accessorialDescription) {
        this.accessorialDescription = accessorialDescription;
    }
    public String getAccessorialGroup() {
        return accessorialGroup;
    }
    public void setAccessorialGroup(String accessorialGroup) {
        this.accessorialGroup = accessorialGroup;
    }
    public BigDecimal getCarrierAccessorialCost() {
        return carrierAccessorialCost;
    }
    public void setCarrierAccessorialCost(BigDecimal carrierAccessorialCost) {
        this.carrierAccessorialCost = carrierAccessorialCost;
    }
    public boolean isApplyCarrierCostBeforeFuel() {
        return applyCarrierCostBeforeFuel;
    }
    public void setApplyCarrierCostBeforeFuel(boolean applyCarrierCostBeforeFuel) {
        this.applyCarrierCostBeforeFuel = applyCarrierCostBeforeFuel;
    }
    public BigDecimal getShipperAccessorialCost() {
        return shipperAccessorialCost;
    }
    public void setShipperAccessorialCost(BigDecimal shipperAccessorialCost) {
        this.shipperAccessorialCost = shipperAccessorialCost;
    }
    public boolean isApplyShipperCostBeforeFuel() {
        return applyShipperCostBeforeFuel;
    }
    public void setApplyShipperCostBeforeFuel(boolean applyShipperCostBeforeFuel) {
        this.applyShipperCostBeforeFuel = applyShipperCostBeforeFuel;
    }
    public BigDecimal getBenchmarkAccessorialCost() {
        return benchmarkAccessorialCost;
    }
    public void setBenchmarkAccessorialCost(BigDecimal benchmarkAccessorialCost) {
        this.benchmarkAccessorialCost = benchmarkAccessorialCost;
    }
    public boolean isApplyBenchmarkCostBeforeFuel() {
        return applyBenchmarkCostBeforeFuel;
    }
    public void setApplyBenchmarkCostBeforeFuel(boolean applyBenchmarkCostBeforeFuel) {
        this.applyBenchmarkCostBeforeFuel = applyBenchmarkCostBeforeFuel;
    }
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("accessorialType", accessorialType)
                .append("accessorialDescription", accessorialDescription)
                .append("accessorialGroup", accessorialGroup)
                .append("carrierAccessorialCost", carrierAccessorialCost)
                .append("applyCarrierCostBeforeFuel", applyCarrierCostBeforeFuel)
                .append("shipperAccessorialCost", shipperAccessorialCost)
                .append("applyShipperCostBeforeFuel", applyShipperCostBeforeFuel)
                .append("benchmarkAccessorialCost", benchmarkAccessorialCost)
                .append("applyBenchmarkCostBeforeFuel", applyBenchmarkCostBeforeFuel);

        return builder.toString();
    }
}
