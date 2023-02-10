package com.pls.invoice.domain;

import java.util.Date;

/**
 * BO for Bill To and its invoice processing time and TimeZone.
 * 
 * @author Aleksandr Leshchenko
 */
public class BillToInvoiceProcessingTimeBO {
    private Long billToId;
    private Integer processingTime;
    private String timeZoneName;
    private Date filterLoadsDate;
    private Long rebillAdjustmentsCount;

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public Integer getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Integer processingTime) {
        this.processingTime = processingTime;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    public Date getFilterLoadsDate() {
        return filterLoadsDate;
    }

    public void setFilterLoadsDate(Date filterLoadsDate) {
        this.filterLoadsDate = filterLoadsDate;
    }

    public Long getRebillAdjustmentsCount() {
        return rebillAdjustmentsCount;
    }

    public void setRebillAdjustmentsCount(Long rebillAdjustmentsCount) {
        this.rebillAdjustmentsCount = rebillAdjustmentsCount;
    }
}
