package com.pls.invoice.domain.bo;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;



/**
 * BO for Financial Board Price Audit.
 * 
 * @author Brichak Aleksandr
 */
public class FinancialPriceAuditBO {

    private Long loadId;
    private Long adjustmentId;
    private String bol;
    private BigDecimal margin;
    private Date actualDeliveryDate;
    private String scac;
    private String customerName;
    private String accountExecutiveName;
    private String businessUnit;
    private String reason;
    private Date priceAuditDate;
    private String assignee;
    private Date assignedDate;
    private String carrierOrSales;
    private String proNumber;
    private String noteComment;
    private ZonedDateTime noteCreatedDate;
    private String noteModifiedBy;
    private Long numberOfNotes;

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

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAccountExecutiveName() {
        return accountExecutiveName;
    }

    public void setAccountExecutiveName(String accountExecutiveName) {
        this.accountExecutiveName = accountExecutiveName;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getPriceAuditDate() {
        return priceAuditDate;
    }

    public void setPriceAuditDate(Date priceAuditDate) {
        this.priceAuditDate = priceAuditDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getCarrierOrSales() {
        return carrierOrSales;
    }

    public void setCarrierOrSales(String carrierOrSales) {
        this.carrierOrSales = carrierOrSales;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getNoteComment() {
        return noteComment;
    }

    public void setNoteComment(String noteComment) {
        this.noteComment = noteComment;
    }

    public ZonedDateTime getNoteCreatedDate() {
        return noteCreatedDate;
    }

    public void setNoteCreatedDate(Date noteCreatedDate) {
        this.noteCreatedDate = ZonedDateTime.ofInstant(noteCreatedDate.toInstant(), ZoneOffset.UTC);
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

}
