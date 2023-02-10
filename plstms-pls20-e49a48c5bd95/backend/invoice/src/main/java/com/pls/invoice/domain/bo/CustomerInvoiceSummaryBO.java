package com.pls.invoice.domain.bo;

import java.math.BigDecimal;

/**
 * BO for customer invoice summary info.
 *
 * @author Alexander Kirichenko
 */
public class CustomerInvoiceSummaryBO {
    private BigDecimal openInvoices;
    private BigDecimal pastDueTotalInvoices;
    private BigDecimal pastDue1to30Invoices;
    private BigDecimal pastDue31to60Invoices;
    private BigDecimal pastDue61to90Invoices;
    private BigDecimal pastDue91Invoices;

    public BigDecimal getOpenInvoices() {
        return openInvoices;
    }

    public void setOpenInvoices(BigDecimal openInvoices) {
        this.openInvoices = openInvoices;
    }

    public BigDecimal getPastDueTotalInvoices() {
        return pastDueTotalInvoices;
    }

    public void setPastDueTotalInvoices(BigDecimal pastDueTotalInvoices) {
        this.pastDueTotalInvoices = pastDueTotalInvoices;
    }

    public BigDecimal getPastDue1to30Invoices() {
        return pastDue1to30Invoices;
    }

    public void setPastDue1to30Invoices(BigDecimal pastDue1to30Invoices) {
        this.pastDue1to30Invoices = pastDue1to30Invoices;
    }

    public BigDecimal getPastDue31to60Invoices() {
        return pastDue31to60Invoices;
    }

    public void setPastDue31to60Invoices(BigDecimal pastDue31to60Invoices) {
        this.pastDue31to60Invoices = pastDue31to60Invoices;
    }

    public BigDecimal getPastDue61to90Invoices() {
        return pastDue61to90Invoices;
    }

    public void setPastDue61to90Invoices(BigDecimal pastDue61to90Invoices) {
        this.pastDue61to90Invoices = pastDue61to90Invoices;
    }

    public BigDecimal getPastDue91Invoices() {
        return pastDue91Invoices;
    }

    public void setPastDue91Invoices(BigDecimal pastDue91Invoices) {
        this.pastDue91Invoices = pastDue91Invoices;
    }
}
