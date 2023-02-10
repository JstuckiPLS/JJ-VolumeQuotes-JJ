package com.pls.invoice.service.processing;

import java.util.List;
import java.util.Map;

import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Service for generating customer invoice documents.
 * 
 * @author Aleksandr Leshchenko
 */
public interface InvoiceDocumentGeneratorService {

    /**
     * Generate invoice documents for invoices.
     * 
     * @param billTo
     *            bill to of customer
     * @param invoices
     *            invoices
     * @return generated documents with appropriate format
     * @throws ApplicationException
     *             if generation failed
     */
    Map<InvoiceDocument, LoadDocumentEntity> generateInvoiceDocuments(BillToEntity billTo, List<LoadAdjustmentBO> invoices)
            throws ApplicationException;
}
