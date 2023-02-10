package com.pls.shipment.domain;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.Currency;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Base class for financial accountable entities.
 *
 * @author Alexander Kirichenko
 */
@MappedSuperclass
public class AbstractFinancialAccountableEntity implements Serializable, HasModificationInfo {

    private static final long serialVersionUID = -639633895548127851L;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID")
    private LoadEntity load;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAA_DETAIL_ID")
    private FinancialAccessorialsEntity financialAccessorials;

    @Column(name = "FINAN_LOAD_ID")
    private String financialLoadId;

    @Column(name = "ADJ_ACC")
    private String adjustmentAccessorial;

    @Column(name = "INV_NUMBER")
    private String invoiceNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INV_DATE")
    private Date invoiceDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INV_DUE_DATE")
    private Date invoiceDueDate;

    @Column(name = "AMT_INVOICED")
    private BigDecimal amountInvoiced;

    @Column(name = "AMT_DUE")
    private BigDecimal amountDue;

    @Column(name = "AMT_APPLIED")
    private BigDecimal amountApplied;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INV_PAID_DATE")
    private Date invoicePaidDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INV_ACTUAL_DATE_CLOSED")
    private Date invoiceActualClosedDate;

    @Column(name = "CURRENCY_CODE")
    @Enumerated(EnumType.STRING)
    private Currency currencyCode = Currency.USD;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public FinancialAccessorialsEntity getFinancialAccessorials() {
        return financialAccessorials;
    }

    public void setFinancialAccessorials(FinancialAccessorialsEntity financialAccessorials) {
        this.financialAccessorials = financialAccessorials;
    }

    public String getFinancialLoadId() {
        return financialLoadId;
    }

    public void setFinancialLoadId(String financialLoadId) {
        this.financialLoadId = financialLoadId;
    }

    public String getAdjustmentAccessorial() {
        return adjustmentAccessorial;
    }

    public void setAdjustmentAccessorial(String adjustmentAccessorial) {
        this.adjustmentAccessorial = adjustmentAccessorial;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(Date invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    public BigDecimal getAmountInvoiced() {
        return amountInvoiced;
    }

    public void setAmountInvoiced(BigDecimal amountInvoiced) {
        this.amountInvoiced = amountInvoiced;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public BigDecimal getAmountApplied() {
        return amountApplied;
    }

    public void setAmountApplied(BigDecimal amountApplied) {
        this.amountApplied = amountApplied;
    }

    public Date getInvoicePaidDate() {
        return invoicePaidDate;
    }

    public void setInvoicePaidDate(Date invoicePaidDate) {
        this.invoicePaidDate = invoicePaidDate;
    }

    public Date getInvoiceActualClosedDate() {
        return invoiceActualClosedDate;
    }

    public void setInvoiceActualClosedDate(Date invoiceActualClosedDate) {
        this.invoiceActualClosedDate = invoiceActualClosedDate;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }
}
