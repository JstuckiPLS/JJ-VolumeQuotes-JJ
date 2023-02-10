package com.pls.invoice.domain.bo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.shipment.domain.bo.ZipBO;

/**
 * Business Object to represent CBI history data.
 *
 * @author Alexander Nalapko
 */
public class CBIHistoryBO {

    private Long loadId;
    private Long adjustmentId;
    private String bol;
    private String pro;
    private String carrierName;
    private BigDecimal totalRevenue;
    private BigDecimal paidAmount;
    private ZipBO origin;
    private ZipBO destination;
    private String po;
    private String glNumber;
    private Long billToId;
    private BigDecimal totalCost;
    private BigDecimal fs;
    private BigDecimal acc;
    private String invoiceNumber;
    private Boolean doNotInvoice;

    @JsonIgnore
    private String destinationCity;
    @JsonIgnore
    private String destinationState;
    @JsonIgnore
    private String destinationZip;
    @JsonIgnore
    private String originCity;
    @JsonIgnore
    private String originState;
    @JsonIgnore
    private String originZip;

    /**
     * Init calculated fields.
     */
    public void init() {
        origin = new ZipBO(originZip, originState, originCity);
        destination = new ZipBO(destinationZip, destinationState, destinationCity);
    }

    public Boolean getDoNotInvoice() {
        return doNotInvoice;
    }

    public void setDoNotInvoice(Boolean doNotInvoice) {
        this.doNotInvoice = doNotInvoice;
    }

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
    public String getPro() {
        return pro;
    }
    public void setPro(String pro) {
        this.pro = pro;
    }
    public String getCarrierName() {
        return carrierName;
    }
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    public String getPo() {
        return po;
    }
    public void setPo(String po) {
        this.po = po;
    }
    public String getGlNumber() {
        return glNumber;
    }
    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }
    public Long getBillToId() {
        return billToId;
    }
    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
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
    public ZipBO getDestination() {
        return destination;
    }
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
