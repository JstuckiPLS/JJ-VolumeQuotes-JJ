package com.pls.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.LtlMarginType;
import com.pls.ltlrating.domain.enums.MoveType;

/**
 * DTO for Pricing Details.
 * 
 * @author Aleksandr Leshchenkos
 */
public class PricingDetailDTO {
    private Long id;
    private Long profileDetailId;
    private LtlCostType costType;
    private BigDecimal costAmount;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private BigDecimal minDistance;
    private BigDecimal maxDistance;
    private BigDecimal minCost;
    private LtlMarginType marginType;
    private BigDecimal marginAmount;
    private BigDecimal minMargin;
    private Date effDate;
    private Date expDate;
    private LtlServiceType serviceType;
    private String smcTariff;
    private CommodityClass freightClass;
    private MoveType movementType;

    private Map<CommodityClass, CommodityClass> fakMapping;
    private List<PricingAddressDTO> addresses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileDetailId() {
        return profileDetailId;
    }

    public void setProfileDetailId(Long profileDetailId) {
        this.profileDetailId = profileDetailId;
    }

    public LtlCostType getCostType() {
        return costType;
    }

    public void setCostType(LtlCostType costType) {
        this.costType = costType;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public BigDecimal getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(BigDecimal minWeight) {
        this.minWeight = minWeight;
    }

    public BigDecimal getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(BigDecimal maxWeight) {
        this.maxWeight = maxWeight;
    }

    public BigDecimal getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(BigDecimal minDistance) {
        this.minDistance = minDistance;
    }

    public BigDecimal getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(BigDecimal maxDistance) {
        this.maxDistance = maxDistance;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }

    public LtlMarginType getMarginType() {
        return marginType;
    }

    public void setMarginType(LtlMarginType marginType) {
        this.marginType = marginType;
    }

    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    public BigDecimal getMinMargin() {
        return minMargin;
    }

    public void setMinMargin(BigDecimal minMargin) {
        this.minMargin = minMargin;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getSmcTariff() {
        return smcTariff;
    }

    public void setSmcTariff(String smcTariff) {
        this.smcTariff = smcTariff;
    }

    public CommodityClass getFreightClass() {
        return freightClass;
    }

    public void setFreightClass(CommodityClass freightClass) {
        this.freightClass = freightClass;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }

    public Map<CommodityClass, CommodityClass> getFakMapping() {
        return fakMapping;
    }

    public void setFakMapping(Map<CommodityClass, CommodityClass> fakMapping) {
        this.fakMapping = fakMapping;
    }

    public List<PricingAddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PricingAddressDTO> addresses) {
        this.addresses = addresses;
    }
}
