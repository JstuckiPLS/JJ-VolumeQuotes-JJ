package com.pls.invoice.domain.bo;

import java.math.BigDecimal;

import com.pls.core.domain.enums.ShipmentFinancialStatus;

/**
 * Business object for handling loads/adjustments that were sent or failed sending to Oracle Financials.
 * 
 * @author Aleksandr Leshchenko
 */
public class InvoiceResultBO {
    private String invoiceNumber;
    private ShipmentFinancialStatus finalizationStatus;
    private Long loadId;
    private Long adjustmentId;
    private Boolean doNotInvoice;
    private Boolean rebill;
    private String bol;
    private String errorMessage;
    private BigDecimal cost;
    private BigDecimal revenue;
    private Long billToId;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public ShipmentFinancialStatus getFinalizationStatus() {
        return finalizationStatus;
    }

    public void setFinalizationStatus(ShipmentFinancialStatus finalizationStatus) {
        this.finalizationStatus = finalizationStatus;
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

    public Boolean getDoNotInvoice() {
        return doNotInvoice;
    }

    public void setDoNotInvoice(Boolean doNotInvoice) {
        this.doNotInvoice = doNotInvoice;
    }

    public Boolean isRebill() {
        return rebill;
    }

    public void setRebill(Boolean rebill) {
        this.rebill = rebill;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }
}
