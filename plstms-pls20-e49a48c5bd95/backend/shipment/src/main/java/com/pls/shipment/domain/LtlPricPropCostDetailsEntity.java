package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.bo.proposal.CostDetailOwner;

/**
 * Entity for Ltl pricing proposal details.
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LTL_PRIC_PROP_COST_DETAILS")
public class LtlPricPropCostDetailsEntity implements Identifiable<Long>, Cloneable {

    private static final long serialVersionUID = -3811582680225640028L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pric_prop_dtl_seq")
    @SequenceGenerator(name = "ltl_pric_prop_dtl_seq", sequenceName = "LTL_PRIC_PROPOSAL_DTLS_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRIC_PROP_COST_DET_ID")
    private Long ltlPricPropDetailId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LTL_PRIC_PROPOSAL_ID")
    private LtlPricingProposalsEntity ltlPricProposalEntity;

    @Column(name = "LTL_PRICING_ID")
    private Long ltlPricingId;

    @Column(name = "REF_TYPE", nullable = false)
    private String refType;

    @Column(name = "SHIP_CARR", nullable = false)
    @Enumerated(EnumType.STRING)
    private CostDetailOwner shipCarr;

    @Column(name = "SUBTOTAL", nullable = false)
    private BigDecimal subTotal;

    @Column(name = "BILLABLE", nullable = false)
    @Type(type = "yes_no")
    private Boolean billable;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @Override
    public Long getId() {
        return ltlPricPropDetailId;
    }

    @Override
    public void setId(Long ltlPricPropDetailId) {
        this.ltlPricPropDetailId = ltlPricPropDetailId;
    }

    public LtlPricingProposalsEntity getLtlPricProposalEntity() {
        return ltlPricProposalEntity;
    }

    public void setLtlPricProposalEntity(LtlPricingProposalsEntity ltlPricProposalEntity) {
        this.ltlPricProposalEntity = ltlPricProposalEntity;
    }

    public Long getLtlPricingId() {
        return ltlPricingId;
    }

    public void setLtlPricingId(Long ltlPricingId) {
        this.ltlPricingId = ltlPricingId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public CostDetailOwner getShipCarr() {
        return shipCarr;
    }

    public void setShipCarr(CostDetailOwner shipCarr) {
        this.shipCarr = shipCarr;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public Boolean getBillable() {
        return billable;
    }

    public void setBillable(Boolean billable) {
        this.billable = billable;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    protected LtlPricPropCostDetailsEntity clone() throws CloneNotSupportedException {
        LtlPricPropCostDetailsEntity pricPropCostDtlEntity = (LtlPricPropCostDetailsEntity) super.clone();
        pricPropCostDtlEntity.setId(null);
        pricPropCostDtlEntity.setLtlPricProposalEntity(null);
        return pricPropCostDtlEntity;
    }
}
