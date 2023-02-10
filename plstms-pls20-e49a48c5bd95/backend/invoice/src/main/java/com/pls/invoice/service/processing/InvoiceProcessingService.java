package com.pls.invoice.service.processing;

import java.io.IOException;
import java.util.Collection;

import com.pls.core.exception.ApplicationException;
import com.pls.invoice.domain.bo.SendToFinanceBO;

/**
 * Service for processing invoices.
 * 
 * @author Aleksandr Leshchenko
 */
public interface InvoiceProcessingService {

    /**
     * Sends invoices by invoice numbers to AX and customer.
     * 
     * @param bo
     *            information that is necessary for invoicing
     * @param invoiceId
     *            invoice ID to process
     * @param userId
     *            id of user responsible for invoicing
     */
    void processInvoices(SendToFinanceBO bo, Long invoiceId, Long userId);

    /**
     * Generate Invoice Id and populate invoice history table for invoice re-processing.
     * 
     * @param loads
     *            list of loads to re-process
     * @param adjustments
     *            list of adjustments to re-process
     * @param userId
     *            id of user responsible for re-processing
     * @return invoice ID
     */
    Long prepareLoadsAndAdjustmentsForReProcessing(Collection<Long> loads, Collection<Long> adjustments, Long userId);

    /**
     * Send Invoice Documents to shared drive.
     * 
     * @param bo
     *            information that is necessary for invoicing
     * @param invoiceId
     *            invoice ID to process
     * @throws ApplicationException
     *             business exception
     * @throws IOException
     *             document write exception
     */
    void processInvoiceDocuments(SendToFinanceBO bo, Long invoiceId) throws ApplicationException, IOException;
}
