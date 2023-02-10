package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

/**
 * BO to retrieve Saved Quotes Information from DB.
 * 
 * @author Ashwini Neelgund
 */
import com.pls.core.shared.StatusYesNo;

/**
 * Saved Quote BO.
 *
 * @author Ashwini Neelgund
 */
public class SavedQuoteBO {

    private Long id;

    private String quoteId;

    private String originZip;

    private String originState;

    private String originCity;

    private String destZip;

    private String destState;

    private String destCity;

    private String carrierName;

    private String customerName;

    private BigDecimal weight;

    private String commodityClass;

    private Long estimatedTransitTime;

    private BigDecimal shipperBaseRate;

    private BigDecimal customerRevenue;

    private BigDecimal carrierCost;

    private Long pricingProfileId;

    private String volumeQuoteId;

    private StatusYesNo costOverride;

    private StatusYesNo revenueOverride;

    private String loadId;

    public Long getPricingProfileId() {
        return pricingProfileId;
    }

    public void setPricingProfileId(Long pricingProfileId) {
        this.pricingProfileId = pricingProfileId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getEstimatedTransitTime() {
        return estimatedTransitTime;
    }

    public void setEstimatedTransitTime(Long estimatedTransitTime) {
        this.estimatedTransitTime = estimatedTransitTime;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(String commodityClass) {
        this.commodityClass = commodityClass;
    }

    public BigDecimal getShipperBaseRate() {
        return shipperBaseRate;
    }

    public void setShipperBaseRate(BigDecimal shipperBaseRate) {
        this.shipperBaseRate = shipperBaseRate;
    }

    public BigDecimal getCustomerRevenue() {
        return customerRevenue;
    }

    public void setCustomerRevenue(BigDecimal customerRevenue) {
        this.customerRevenue = customerRevenue;
    }

    public BigDecimal getCarrierCost() {
        return carrierCost;
    }

    public void setCarrierCost(BigDecimal carrierCost) {
        this.carrierCost = carrierCost;
    }

    public String getVolumeQuoteId() {
        return volumeQuoteId;
    }

    public void setVolumeQuoteId(String volumeQuoteId) {
        this.volumeQuoteId = volumeQuoteId;
    }

    public StatusYesNo getCostOverride() {
        return costOverride;
    }

    public void setCostOverride(StatusYesNo costOverride) {
        this.costOverride = costOverride;
    }

    public StatusYesNo getRevenueOverride() {
        return revenueOverride;
    }

    public void setRevenueOverride(StatusYesNo revenueOverride) {
        this.revenueOverride = revenueOverride;
    }

    public String getLoadId() {
        return loadId;
    }

    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestZip() {
        return destZip;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

}
