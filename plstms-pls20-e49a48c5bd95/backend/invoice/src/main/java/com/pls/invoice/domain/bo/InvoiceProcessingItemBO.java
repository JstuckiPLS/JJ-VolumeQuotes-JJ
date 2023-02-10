package com.pls.invoice.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.invoice.domain.bo.enums.InvoiceErrorCode;

/**
 * BO for loads and adjustment details necessary for invoicing.
 * 
 * @author Aleksandr Leshchenko
 */
public class InvoiceProcessingItemBO {
    private Long loadId;
    private Long adjustmentId;
    private Long costDetailId;
    private Long billToId;
    private String bolNumber;
    private String glNumber;
    private String poNumber;
    private Date pickupDate;
    private Date deliveryDate;
    private Date freightBillDate;
    private InvoiceType invoiceType;
    private CbiInvoiceType cbiInvoiceType;
    private String consolidatedInvoiceNumber;
    private InvoiceSortType sortType;
    private Currency billToCurrency;
    private Currency carrierCurrency;
    private BigDecimal totalRevenue;
    private BigDecimal totalItemsRevenue;
    private BigDecimal totalCost;
    private BigDecimal totalItemsCost;
    private Long invalidCostItemsCount;
    private Boolean doNotInvoice;
    private Boolean rebill;

    private InvoiceErrorCode errorCode;

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

    public Long getCostDetailId() {
        return costDetailId;
    }

    public void setCostDetailId(Long costDetailId) {
        this.costDetailId = costDetailId;
    }

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getFreightBillDate() {
        return freightBillDate;
    }

    public void setFreightBillDate(Date freightBillDate) {
        this.freightBillDate = freightBillDate;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public CbiInvoiceType getCbiInvoiceType() {
        return cbiInvoiceType;
    }

    public void setCbiInvoiceType(CbiInvoiceType cbiInvoiceType) {
        this.cbiInvoiceType = cbiInvoiceType;
    }

    public String getConsolidatedInvoiceNumber() {
        return consolidatedInvoiceNumber;
    }

    public void setConsolidatedInvoiceNumber(String consolidatedInvoiceNumber) {
        this.consolidatedInvoiceNumber = consolidatedInvoiceNumber;
    }

    public InvoiceSortType getSortType() {
        return sortType;
    }

    public void setSortType(InvoiceSortType sortType) {
        this.sortType = sortType;
    }

    public Currency getBillToCurrency() {
        return billToCurrency;
    }

    public void setBillToCurrency(Currency billToCurrency) {
        this.billToCurrency = billToCurrency;
    }

    public Currency getCarrierCurrency() {
        return carrierCurrency;
    }

    public void setCarrierCurrency(Currency carrierCurrency) {
        this.carrierCurrency = carrierCurrency;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalItemsRevenue() {
        return totalItemsRevenue;
    }

    public void setTotalItemsRevenue(BigDecimal totalItemsRevenue) {
        this.totalItemsRevenue = totalItemsRevenue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalItemsCost() {
        return totalItemsCost;
    }

    public void setTotalItemsCost(BigDecimal totalItemsCost) {
        this.totalItemsCost = totalItemsCost;
    }

    public Long getInvalidCostItemsCount() {
        return invalidCostItemsCount;
    }

    public void setInvalidCostItemsCount(Long invalidCostItemsCount) {
        this.invalidCostItemsCount = invalidCostItemsCount;
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

    public InvoiceErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(InvoiceErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
