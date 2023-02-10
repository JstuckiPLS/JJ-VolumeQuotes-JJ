package com.pls.invoice.domain.bo;

import java.util.List;
import java.util.Map;

import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Class for invoice processing.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class CustomerInvoiceProcessingBO {

    private Long billToId;
    private String email;
    private String comments;
    private String subject;

    private BillToEntity billTo;
    private List<LoadAdjustmentBO> invoices;
    private Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments;

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public List<LoadAdjustmentBO> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<LoadAdjustmentBO> invoices) {
        this.invoices = invoices;
    }

    public Map<InvoiceDocument, LoadDocumentEntity> getInvoiceDocuments() {
        return invoiceDocuments;
    }

    public void setInvoiceDocuments(Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments) {
        this.invoiceDocuments = invoiceDocuments;
    }
}
