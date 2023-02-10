package com.pls.ltlrating.domain.bo.proposal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import com.pls.core.domain.bo.proposal.Smc3CostDetailsDTO;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Contains carrier specific pricing details for given proposition.
 *
 * @author Ashwini Neelgund
 */
public class PricingDetailsBO implements Serializable {

    private static final long serialVersionUID = -8227757993733119906L;

    private Set<Smc3CostDetailsDTO> smc3CostDetails;
    private BigDecimal smc3MinimumCharge;
    private BigDecimal totalChargeFromSmc3;
    private BigDecimal deficitChargeFromSmc3;
    private BigDecimal costAfterDiscount;
    private BigDecimal minimumCost;
    private BigDecimal costDiscount;
    private Long carrierFSId;
    private BigDecimal carrierFuelDiscount;
    private PricingType pricingType;
    private MoveType movementType;
    private Date effectiveDate;
    private Long buyProfileDetailId;
    private Long sellProfileDetailId;

    public Set<Smc3CostDetailsDTO> getSmc3CostDetails() {
        return smc3CostDetails;
    }

    public void setSmc3CostDetails(Set<Smc3CostDetailsDTO> smc3CostDetails) {
        this.smc3CostDetails = smc3CostDetails;
    }

    public BigDecimal getSmc3MinimumCharge() {
        return smc3MinimumCharge;
    }

    public void setSmc3MinimumCharge(BigDecimal smc3MinimumCharge) {
        this.smc3MinimumCharge = smc3MinimumCharge;
    }

    public BigDecimal getTotalChargeFromSmc3() {
        return totalChargeFromSmc3;
    }

    public void setTotalChargeFromSmc3(BigDecimal totalChargeFromSmc3) {
        this.totalChargeFromSmc3 = totalChargeFromSmc3;
    }

    public BigDecimal getDeficitChargeFromSmc3() {
        return deficitChargeFromSmc3;
    }

    public void setDeficitChargeFromSmc3(BigDecimal deficitChargeFromSmc3) {
        this.deficitChargeFromSmc3 = deficitChargeFromSmc3;
    }

    public BigDecimal getCostAfterDiscount() {
        return costAfterDiscount;
    }

    public void setCostAfterDiscount(BigDecimal costAfterDiscount) {
        this.costAfterDiscount = costAfterDiscount;
    }

    public BigDecimal getMinimumCost() {
        return minimumCost;
    }

    public void setMinimumCost(BigDecimal minimumCost) {
        this.minimumCost = minimumCost;
    }

    public BigDecimal getCostDiscount() {
        return costDiscount;
    }

    public void setCostDiscount(BigDecimal costDiscount) {
        this.costDiscount = costDiscount;
    }

    public Long getCarrierFSId() {
        return carrierFSId;
    }

    public void setCarrierFSId(Long carrierFSId) {
        this.carrierFSId = carrierFSId;
    }

    public BigDecimal getCarrierFuelDiscount() {
        return carrierFuelDiscount;
    }

    public void setCarrierFuelDiscount(BigDecimal carrierFuelDiscount) {
        this.carrierFuelDiscount = carrierFuelDiscount;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Long getBuyProfileDetailId() {
        return buyProfileDetailId;
    }

    public void setBuyProfileDetailId(Long buyProfileDetailId) {
        this.buyProfileDetailId = buyProfileDetailId;
    }

    public Long getSellProfileDetailId() {
        return sellProfileDetailId;
    }

    public void setSellProfileDetailId(Long sellProfileDetailId) {
        this.sellProfileDetailId = sellProfileDetailId;
    }
}
