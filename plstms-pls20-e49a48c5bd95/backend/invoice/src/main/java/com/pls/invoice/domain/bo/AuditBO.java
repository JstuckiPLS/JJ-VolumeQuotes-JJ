package com.pls.invoice.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.pls.core.domain.enums.InvoiceType;

/**
 * BO for Financial Board Invoice Audit and Billing Hold.
 * 
 * @author Aleksandr Leshchenko
 */
public class AuditBO {

    private Long loadId;
    private Long adjustmentId;
    private String bol;
    private String po;
    private String pro;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal margin;
    private BigDecimal vendorBillAmount;
    private Date deliveryDate;
    private String reason;
    private String carrierName;
    private String customerName;
    private String networkName;
    private String accExecName;
    private String noteComment;
    private Date noteCreatedDate;
    private String noteModifiedBy;
    private Long numberOfNotes;
    private Long diffDays;
    private Date modifiedDate;
    private String scac;
    private Date priceAuditDate;
    private InvoiceType invoiceType;
    private Boolean rebill;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getVendorBillAmount() {
        return vendorBillAmount;
    }

    public void setVendorBillAmount(BigDecimal vendorBillAmount) {
        this.vendorBillAmount = vendorBillAmount;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getAccExecName() {
        return accExecName;
    }

    public void setAccExecName(String accExecName) {
        this.accExecName = accExecName;
    }

    public String getNoteComment() {
        return noteComment;
    }

    public void setNoteComment(String noteComment) {
        this.noteComment = noteComment;
    }

    public Date getNoteCreatedDate() {
        return noteCreatedDate;
    }

    public void setNoteCreatedDate(Date noteCreatedDate) {
        this.noteCreatedDate = noteCreatedDate;
    }

    public String getNoteModifiedBy() {
        return noteModifiedBy;
    }

    public void setNoteModifiedBy(String noteModifiedBy) {
        this.noteModifiedBy = noteModifiedBy;
    }

    public Long getNumberOfNotes() {
        return numberOfNotes;
    }

    public void setNumberOfNotes(Long numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }

    public Long getDiffDays() {
        return diffDays;
    }

    public void setDiffDays(Long diffDays) {
        this.diffDays = diffDays;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getPriceAuditDate() {
        return priceAuditDate;
    }

    public void setPriceAuditDate(Date priceAuditDate) {
        this.priceAuditDate = priceAuditDate;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Boolean getRebill() {
        return rebill;
    }

    public void setRebill(Boolean rebill) {
        this.rebill = rebill;
    }
}
