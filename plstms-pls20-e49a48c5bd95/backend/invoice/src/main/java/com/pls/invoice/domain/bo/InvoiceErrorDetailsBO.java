package com.pls.invoice.domain.bo;

import com.pls.core.domain.enums.InvoiceType;

/**
 * Invoice details for invoice errors re-processing.
 * 
 * @author Aleksandr Leshchenko
 */
public class InvoiceErrorDetailsBO {
    private InvoiceType invoiceType;
    private String invoiceNumber;

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
