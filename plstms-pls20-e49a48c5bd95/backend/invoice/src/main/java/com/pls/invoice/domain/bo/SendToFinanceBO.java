package com.pls.invoice.domain.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * BO for sending invoices to finance.
 * 
 * @author Aleksandr Leshchenko
 */
public class SendToFinanceBO implements Serializable {

    private static final long serialVersionUID = 2340982803842049396L;

    private Date invoiceDate;
    private Date filterLoadsDate;
    private List<InvoiceProcessingBO> invoiceProcessingDetails;

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getFilterLoadsDate() {
        return filterLoadsDate;
    }

    public void setFilterLoadsDate(Date filterLoadsDate) {
        this.filterLoadsDate = filterLoadsDate;
    }

    public List<InvoiceProcessingBO> getInvoiceProcessingDetails() {
        return invoiceProcessingDetails;
    }

    public void setInvoiceProcessingDetails(List<InvoiceProcessingBO> invoiceProcessingDetails) {
        this.invoiceProcessingDetails = invoiceProcessingDetails;
    }

}
