package com.pls.invoice.service.pdf;

import java.math.BigDecimal;
import java.util.Date;

/**
 * BO for general invoice information.
 *
 * @author Denis Zhupinsky (Team International)
 */
class InvoicePdfDetails {
    private Date invoiceDate;
    private Date dueDate;
    private BigDecimal amountDue;

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }
}
