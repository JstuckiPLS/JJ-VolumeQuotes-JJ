package com.pls.invoice.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.domain.enums.InvoiceType;

/**
 * Business Object to represent Invoice history data.
 *
 * @author Mikhail Boldinov, 30/01/14
 */
public class InvoiceHistoryBO {
    private InvoiceType invoiceType;
    @JsonIgnore
    private String invoiceTypeStr;
    private Boolean invoiceInFinancials;
    private Boolean ediCapable;
    private Long invoiceId;
    private Date invoiceDate;
    private String invoiceNumber;
    private String userName;
    private Long loadId;
    private Long adjustmentId;
    private Long billToId;
    private String bol;
    private String pro;
    private String carrierName;
    private BigDecimal invoiceAmount;
    private BigDecimal paidAmount;
    private BigDecimal paidDue;
    private Date dueDate;
    private String networkName;
    private String customerName;
    private Boolean adjustment;

    public Boolean isAdjustment() {
        return adjustment;
    }

    public void setAdjustment(String adjustment) {
        this.adjustment = adjustment == null ? null : Boolean.valueOf(adjustment);
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTypeStr() {
        return invoiceTypeStr;
    }

    /**
     * Setter for Invoice Type.
     * 
     * @param invoiceTypeStr
     *            invoice type
     */
    public void setInvoiceTypeStr(String invoiceTypeStr) {
        this.invoiceTypeStr = invoiceTypeStr;
        this.invoiceType = InvoiceType.valueOf(invoiceTypeStr);
    }

    public Boolean getInvoiceInFinancials() {
        return invoiceInFinancials;
    }

    public void setInvoiceInFinancials(String invoiceInFinancials) {
        this.invoiceInFinancials = invoiceInFinancials == null ? null : Boolean.valueOf(invoiceInFinancials);
    }

    public Boolean getEdiCapable() {
        return ediCapable;
    }

    public void setEdiCapable(String ediCapable) {
        this.ediCapable = ediCapable == null ? null : Boolean.valueOf(ediCapable);
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
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

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Invoice and Due amounts setter.
     *
     * @param invoiceAmount
     *            invoice amount
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
        calculatePaidDue();
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    /**
     * Paid and Due amounts setter.
     *
     * @param paidAmount paid amount
     */
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        calculatePaidDue();
    }

    private void calculatePaidDue() {
        if (this.getInvoiceAmount() != null && this.getPaidAmount() != null) {
            this.paidDue = this.getInvoiceAmount().subtract(this.getPaidAmount());
        }
    }

    public BigDecimal getPaidDue() {
        return paidDue;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}
