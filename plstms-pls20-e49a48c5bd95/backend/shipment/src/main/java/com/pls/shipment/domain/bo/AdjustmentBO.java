package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for adjustments.
 * 
 * @author Aleksandr Leshchenko
 */
public class AdjustmentBO {
    /**
     * For new negative part of pair adjustment this field should be -1.
     */
    private Long financialAccessorialsId;
    private Integer version;
    private String refType;
    private BigDecimal cost;
    private BigDecimal revenue;
    private Long reason;
    private String carrierName;
    private String billToName;
    private String invoiceNumber;
    private Date invoiceDate;
    private boolean notInvoice;
    private String revenueNote;
    private String costNote;

    public Long getFinancialAccessorialsId() {
        return financialAccessorialsId;
    }

    public void setFinancialAccessorialsId(Long financialAccessorialsId) {
        this.financialAccessorialsId = financialAccessorialsId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Long getReason() {
        return reason;
    }

    public void setReason(Long reason) {
        this.reason = reason;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getBillToName() {
        return billToName;
    }

    public void setBillToName(String billToName) {
        this.billToName = billToName;
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

    public boolean isNotInvoice() {
        return notInvoice;
    }

    public void setNotInvoice(boolean notInvoice) {
        this.notInvoice = notInvoice;
    }

    public String getRevenueNote() {
        return revenueNote;
    }

    public void setRevenueNote(String revenueNote) {
        this.revenueNote = revenueNote;
    }

    public String getCostNote() {
        return costNote;
    }

    public void setCostNote(String costNote) {
        this.costNote = costNote;
    }
}
