package com.pls.invoice.service.pdf;

import java.util.List;

import com.pls.core.service.pdf.PdfDocumentParameter;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Implementation of {@link PdfDocumentParameter} for invoice pdf.
 *
 * @author Aleksandr Leshchenko
 */
public class InvoicePdfDocumentParameter implements PdfDocumentParameter {
    private List<LoadAdjustmentBO> invoices;

    /**
     * Constructor.
     *
     * @param invoices
     *            list of invoices
     */
    public InvoicePdfDocumentParameter(List<LoadAdjustmentBO> invoices) {
        this.invoices = invoices;
    }

    public List<LoadAdjustmentBO> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<LoadAdjustmentBO> invoices) {
        this.invoices = invoices;
    }

}
