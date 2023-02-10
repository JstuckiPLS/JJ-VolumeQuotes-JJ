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
 * Credit Limit Entity.
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
@Table(name = "PLS_CREDIT_LIMIT_MVIEW")
public class CreditLimitEntity {

    @Id
    @Column(name = "BILL_TO_ID")
    private Long billToId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @Column(name = "ACCOUNTNUM")
    private String accountNumber;

    @Column(name = "CREDITMAX")
    private BigDecimal creditLimit = BigDecimal.ZERO;

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Set credit limit.
     * 
     * @param creditLimit - Credit Limit.
     */
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}
