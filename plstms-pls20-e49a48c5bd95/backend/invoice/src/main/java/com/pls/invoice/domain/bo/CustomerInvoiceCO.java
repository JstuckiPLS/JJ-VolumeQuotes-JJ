package com.pls.invoice.domain.bo;

import com.pls.invoice.domain.bo.enums.CustomerInvoiceType;

import java.util.Date;

/**
 * Object to hold query parameters to get customer's invoices.
 *
 * @author Alexander Kirichenko
 */
public class CustomerInvoiceCO {
    private CustomerInvoiceType invoiceType;
    private Date paidFrom;
    private Date paidTo;
    private Long carrierId;
    private Long bookedBy;
    private boolean includeFirstThirtyDays;
    private boolean includeSecondThirtyDays;
    private boolean includeThirdThirtyDays;
    private boolean includeLastDays;

    public CustomerInvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(CustomerInvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Date getPaidFrom() {
        return paidFrom;
    }

    public void setPaidFrom(Date paidFrom) {
        this.paidFrom = paidFrom;
    }

    public Date getPaidTo() {
        return paidTo;
    }

    public void setPaidTo(Date paidTo) {
        this.paidTo = paidTo;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(Long bookedBy) {
        this.bookedBy = bookedBy;
    }

    public boolean isIncludeFirstThirtyDays() {
        return includeFirstThirtyDays;
    }

    public void setIncludeFirstThirtyDays(boolean includeFirstThirtyDays) {
        this.includeFirstThirtyDays = includeFirstThirtyDays;
    }

    public boolean isIncludeSecondThirtyDays() {
        return includeSecondThirtyDays;
    }

    public void setIncludeSecondThirtyDays(boolean includeSecondThirtyDays) {
        this.includeSecondThirtyDays = includeSecondThirtyDays;
    }

    public boolean isIncludeThirdThirtyDays() {
        return includeThirdThirtyDays;
    }

    public void setIncludeThirdThirtyDays(boolean includeThirdThirtyDays) {
        this.includeThirdThirtyDays = includeThirdThirtyDays;
    }

    public boolean isIncludeLastDays() {
        return includeLastDays;
    }

    public void setIncludeLastDays(boolean includeLastDays) {
        this.includeLastDays = includeLastDays;
    }
}
