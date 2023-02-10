package com.pls.invoice.domain.bo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.InvoiceProcessingType;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.core.domain.enums.WeekDays;
import com.pls.invoice.domain.bo.enums.ApprovedStatus;

/**
 * Object that contains Consolidated Invoice data.
 *
 * @author Sergey Kirichenko
 */
public class ConsolidatedInvoiceBO {
    private Long billToId;
    private String billToName;
    private Currency currency;
    private Long customerId;
    private String customerName;
    private String networkName;
    private String invoiceDateInfo;
    private boolean includeCarrierRate;
    private String sendBy;
    private BigDecimal totalCost;
    private BigDecimal totalRevenue;
    private BigDecimal totalMargin;
    private ApprovedStatus approved;

    @JsonIgnore
    private Long approvedCount;
    @JsonIgnore
    private Long allCount;
    @JsonIgnore
    private boolean edi;
    @JsonIgnore
    private boolean email;
    @JsonIgnore
    private String timeZone;
    @JsonIgnore
    private WeekDays processingDayOfWeek;
    @JsonIgnore
    private InvoiceProcessingType processingType;
    @JsonIgnore
    private ProcessingPeriod processingPeriod;
    @JsonIgnore
    private Integer processingTime;
    @JsonIgnore
    private boolean invoiceInFinancials;
    @JsonIgnore
    private boolean noInvoiceDocument;

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public String getBillToName() {
        return billToName;
    }

    public void setBillToName(String billToName) {
        this.billToName = billToName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public String getInvoiceDateInfo() {
        return invoiceDateInfo;
    }

    public void setInvoiceDateInfo(String invoiceDateInfo) {
        this.invoiceDateInfo = invoiceDateInfo;
    }

    public boolean isIncludeCarrierRate() {
        return includeCarrierRate;
    }

    public void setIncludeCarrierRate(boolean includeCarrierRate) {
        this.includeCarrierRate = includeCarrierRate;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalMargin() {
        return totalMargin;
    }

    public void setTotalMargin(BigDecimal totalMargin) {
        this.totalMargin = totalMargin;
    }

    public ApprovedStatus getApproved() {
        return approved;
    }

    public void setApproved(ApprovedStatus approved) {
        this.approved = approved;
    }

    public Long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(Long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public Long getAllCount() {
        return allCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public boolean isEdi() {
        return edi;
    }

    public void setEdi(boolean edi) {
        this.edi = edi;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public WeekDays getProcessingDayOfWeek() {
        return processingDayOfWeek;
    }

    public void setProcessingDayOfWeek(WeekDays processingDayOfWeek) {
        this.processingDayOfWeek = processingDayOfWeek;
    }

    public InvoiceProcessingType getProcessingType() {
        return processingType;
    }

    public void setProcessingType(InvoiceProcessingType processingType) {
        this.processingType = processingType;
    }

    public ProcessingPeriod getProcessingPeriod() {
        return processingPeriod;
    }

    public void setProcessingPeriod(ProcessingPeriod processingPeriod) {
        this.processingPeriod = processingPeriod;
    }

    public Integer getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Integer processingTime) {
        this.processingTime = processingTime;
    }

    public boolean isInvoiceInFinancials() {
        return invoiceInFinancials;
    }

    public void setInvoiceInFinancials(boolean invoiceInFinancials) {
        this.invoiceInFinancials = invoiceInFinancials;
    }

    public boolean isNoInvoiceDocument() {
        return noInvoiceDocument;
    }

    public void setNoInvoiceDocument(boolean noInvoiceDocument) {
        this.noInvoiceDocument = noInvoiceDocument;
    }
}
