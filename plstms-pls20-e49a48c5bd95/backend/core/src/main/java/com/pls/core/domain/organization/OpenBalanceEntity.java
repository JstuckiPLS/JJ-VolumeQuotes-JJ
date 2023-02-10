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
 * Open Balance Entity.
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
@Table(name = "PLS_OPEN_BALANCE_MVIEW")
public class OpenBalanceEntity {

    @Id
    @Column(name = "BILL_TO_ID")
    private Long billToId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @Column(name = "AMOUNTDUE")
    private BigDecimal balance;

    public Long getBillToId() {
        return billToId;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
