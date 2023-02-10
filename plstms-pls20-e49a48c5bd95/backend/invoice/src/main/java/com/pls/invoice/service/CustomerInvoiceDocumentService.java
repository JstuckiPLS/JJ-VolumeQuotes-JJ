package com.pls.invoice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Customer Invoice Document Service.
 * 
 * @author Alexander Nalapko
 *
 */
public interface CustomerInvoiceDocumentService {

    /**
     * Convert documents for loads at tiff and sand to cloud.
     *
     * @param invoiceId            invoice id
     * @param billTo            Bill To
     * @param invoices the invoices
     * @param invoiceDocuments the invoice documents
     * @throws ApplicationException             exception
     * @throws IOException - exception
     */
    void convertAndSendDocument(Long invoiceId, BillToEntity billTo, List<LoadAdjustmentBO> invoices,
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments) throws ApplicationException, IOException;

}
