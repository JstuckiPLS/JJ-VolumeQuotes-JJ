package com.pls.dto.invoice;

import com.pls.dto.invoice.enums.HistoryInvoiceStatusDTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for Financial Board History Invoice.
 *
 * @author Sergey Kirichenko
 */
public class HistoryInvoiceDTO {

    private String customerName;

    private Date invoiceDate;

    private String invoiceNumber;

    private String userName;

    private Long loadId;

    private String bol;

    private String pro;

    private String carrierName;

    private BigDecimal invoiceAmount;

    private BigDecimal cbiLoadAmount;

    private BigDecimal paidAmount;

    private HistoryInvoiceStatusDTO status;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getCbiLoadAmount() {
        return cbiLoadAmount;
    }

    public void setCbiLoadAmount(BigDecimal cbiLoadAmount) {
        this.cbiLoadAmount = cbiLoadAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public HistoryInvoiceStatusDTO getStatus() {
        return status;
    }

    public void setStatus(HistoryInvoiceStatusDTO status) {
        this.status = status;
    }
}
