package com.pls.dto.quote;

import java.math.BigDecimal;

import com.pls.core.shared.StatusYesNo;
import com.pls.dto.address.ZipDTO;

/**
 * DTO that is used to transfer data for saved quotes table items.
 * 
 * @author Ivan Shapovalov
 */
public class SavedQuoteListItemDTO {

    private Long id;
    /**
     * ref #
     */
    private String quoteId;

    private ZipDTO origin;

    private ZipDTO destination;

    private String carrierName;

    private String customerName;

    private BigDecimal weight;

    private String commodityClass;

    /**
     * Estimated transit time in minutes.
     */
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

    public ZipDTO getOrigin() {
        return origin;
    }

    public void setOrigin(ZipDTO origin) {
        this.origin = origin;
    }

    public ZipDTO getDestination() {
        return destination;
    }

    public void setDestination(ZipDTO destination) {
        this.destination = destination;
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

}
