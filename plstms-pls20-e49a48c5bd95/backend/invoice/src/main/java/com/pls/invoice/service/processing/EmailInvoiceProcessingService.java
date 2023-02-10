package com.pls.invoice.service.processing;

import java.util.Collection;
import java.util.Map;

import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.invoice.domain.bo.CustomerInvoiceProcessingBO;

/**
 * Service to send invoices to customer via email.
 * 
 * @author Aleksandr Leshchenko
 */
public interface EmailInvoiceProcessingService {

    /**
     * Send invoices to customer via email.
     * 
     * @param invoiceId
     *            invoice ID
     * @param emails
     *            list of email addresses to send customer email
     * @param subject
     *            subject of email
     * @param comments
     *            user comments to be added to email
     * @param billTo
     *            bill to of customer
     * @param personId
     *            id of user responsible for invoicing
     * @param invoiceDocuments
     *            invoice documents
     * @throws ApplicationException
     *             if sending to customer failed
     */
    void sendInvoicesViaEmail(Long invoiceId, String emails, String subject, String comments, BillToEntity billTo, Long personId,
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments)
            throws ApplicationException;

    /**
     * Send invoices to customer via email.
     * 
     * @param invoiceId
     *            invoice ID
     * @param personId
     *            id of user responsible for invoicing
     * @param bo
     *            with processing data
     * @throws ApplicationException
     *             if sending to customer failed
     */
    void sendInvoicesViaEmail(Long invoiceId, Long personId, CustomerInvoiceProcessingBO bo) throws ApplicationException;

    /**
     * Get subject of customer email.
     * 
     * @param invoiceType
     *            invoice type
     * @param invoiceNumbers
     *            list of invoice numbers that should be sent to customer
     * @return subject
     */
    String getEmailSubject(InvoiceType invoiceType, Collection<String> invoiceNumbers);
}
