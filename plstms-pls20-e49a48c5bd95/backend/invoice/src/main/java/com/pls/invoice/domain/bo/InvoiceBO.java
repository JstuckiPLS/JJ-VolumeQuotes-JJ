package com.pls.invoice.domain.bo;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.shipment.domain.bo.ZipBO;

/**
 * Object that contains Transactional Invoice data.
 *
 * @author Alexander Kirichenko
 */
public class InvoiceBO {

    private Long loadId;
    private Long billToId;
    private Long adjustmentId;
    private Integer adjustmentRevision;
    private String customerName;
    private String billToName;
    private InvoiceType billToProcessType;
    private Date deliveredDate;
    private String bolNumber;
    private String proNumber;
    private String poNumber;
    private String glNumber;
    private String soNumber;
    private String carrierName;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal marginAmt;
    private BigDecimal margin;
    private BigDecimal paidAmount;
    private Currency currency;
    private Boolean approved;
    private String networkName;
    private Boolean doNotInvoice;
    private Boolean rebill;
    private Boolean missingPaymentsTerms;
    private BigDecimal fs;
    private BigDecimal acc;
    private ZipBO origin;
    private ZipBO destination;
    private String noteComment;
    private ZonedDateTime noteCreatedDate;
    private String noteModifiedBy;
    private Long numberOfNotes;
    private PaymentTerms paymentTerms;
    private ShipmentDirection shipmentDirection;

    @JsonIgnore
    private String destCity;
    @JsonIgnore
    private String destState;
    @JsonIgnore
    private String destZip;
    @JsonIgnore
    private String origCity;
    @JsonIgnore
    private String origState;
    @JsonIgnore
    private String origZip;

    /**
     * Init calculated fields.
     */
    public void init() {
        origin = new ZipBO(origZip, origState, origCity);
        destination = new ZipBO(destZip, destState, destCity);
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBillToName() {
        return billToName;
    }

    public void setBillToName(String billToName) {
        this.billToName = billToName;
    }

    public InvoiceType getBillToProcessType() {
        return billToProcessType;
    }

    public void setBillToProcessType(InvoiceType billToProcessType) {
        this.billToProcessType = billToProcessType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getMarginAmt() {
        return marginAmt;
    }

    public void setMarginAmt(BigDecimal marginAmt) {
        this.marginAmt = marginAmt;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public Integer getAdjustmentRevision() {
        return adjustmentRevision;
    }

    public void setAdjustmentRevision(Integer adjustmentRevision) {
        this.adjustmentRevision = adjustmentRevision;
    }

    public Boolean getDoNotInvoice() {
        return doNotInvoice;
    }

    public void setDoNotInvoice(Boolean doNotInvoice) {
        this.doNotInvoice = doNotInvoice;
    }

    public Boolean getRebill() {
        return rebill;
    }

    public void setRebill(Boolean rebill) {
        this.rebill = rebill;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public Boolean getMissingPaymentsTerms() {
        return missingPaymentsTerms;
    }

    public void setMissingPaymentsTerms(Boolean missingPaymentsTerms) {
        this.missingPaymentsTerms = missingPaymentsTerms;
    }

    public BigDecimal getFs() {
        return fs;
    }

    public void setFs(BigDecimal fs) {
        this.fs = fs;
    }

    public BigDecimal getAcc() {
        return acc;
    }

    public void setAcc(BigDecimal acc) {
        this.acc = acc;
    }

    public ZipBO getOrigin() {
        return origin;
    }

    public void setOrigin(ZipBO origin) {
        this.origin = origin;
    }

    public ZipBO getDestination() {
        return destination;
    }

    public void setDestination(ZipBO destination) {
        this.destination = destination;
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
        this.noteCreatedDate = noteCreatedDate == null ? null : ZonedDateTime.ofInstant(noteCreatedDate.toInstant(), ZoneOffset.UTC);
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

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public String getPaymentTerms() {
        return paymentTerms != null ? paymentTerms.getPaymentTermsCode() : null;
    }

    public void setPaymentTerms(PaymentTerms paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getShipmentDirection() {
        return shipmentDirection != null ? shipmentDirection.getCode() : null;
    }

    public void setShipmentDirection(ShipmentDirection shipmentDirection) {
        this.shipmentDirection = shipmentDirection;
    }
}
