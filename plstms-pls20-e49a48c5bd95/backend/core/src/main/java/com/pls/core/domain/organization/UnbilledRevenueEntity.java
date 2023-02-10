package com.pls.core.domain.organization;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * Unbilled Revenue Entity.
 * 
 * <p>
 * Mapped on Materialized View .
 * </p>
 * 
 * @author Artem Arapov
 *
 */
@Entity
@Immutable
@Table(name = "PLS_UNBILLED_REV_T")
public class UnbilledRevenueEntity {

    @Id
    @Column(name = "BILL_TO_ID")
    private Long billToId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @Column(name = "UNBILLED_REV")
    private BigDecimal unbilledRevenue = BigDecimal.ZERO;

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public BigDecimal getUnbilledRevenue() {
        return unbilledRevenue;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    /**
     * Set Unbilled Revenue Amount.
     * 
     * @param unbilledRevenue - Unbilled Revenue.
     */
    public void setUnbilledRevenue(BigDecimal unbilledRevenue) {
        this.unbilledRevenue = unbilledRevenue;
    }
}
