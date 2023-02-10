package com.pls.ltlrating.domain.bo.proposal;

import java.io.Serializable;
import java.math.BigDecimal;

import com.pls.core.domain.bo.proposal.CostDetailOwner;

/**
 * BO for cost detail items (Accessorials).
 * 
 * @author Aleksandr Leshchenko
 */
public class CostDetailItemBO implements Serializable {
    private static final long serialVersionUID = 2249097440841103300L;

    private String refType;
    private BigDecimal subTotal;
    private CostDetailOwner costDetailOwner;
    private Long guaranteedBy;
    private String note;
    private Long ltlPricingId;

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public CostDetailOwner getCostDetailOwner() {
        return costDetailOwner;
    }

    public void setCostDetailOwner(CostDetailOwner costDetailOwner) {
        this.costDetailOwner = costDetailOwner;
    }

    public Long getGuaranteedBy() {
        return guaranteedBy;
    }

    public void setGuaranteedBy(Long guaranteedBy) {
        this.guaranteedBy = guaranteedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getLtlPricingId() {
        return ltlPricingId;
    }

    public void setLtlPricingId(Long ltlPricingId) {
        this.ltlPricingId = ltlPricingId;
    }
}
