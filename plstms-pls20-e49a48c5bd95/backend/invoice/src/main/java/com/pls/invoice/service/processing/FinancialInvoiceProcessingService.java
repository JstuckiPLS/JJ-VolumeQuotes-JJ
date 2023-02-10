package com.pls.invoice.service.processing;

import com.pls.core.exception.ApplicationException;
import com.pls.invoice.domain.bo.SendToFinanceBO;

/**
 * Service to send invoices to finance systems.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FinancialInvoiceProcessingService {

    /**
     * Send information to AX system for specified invoice ID.
     * 
     * @param invoiceId
     *            invoice ID
     * @param personId
     *            id of user responsible for invoicing
     * @throws ApplicationException
     *             if sending to AX failed
     */
    void sendInvoicesToAX(Long invoiceId, Long personId) throws ApplicationException;

    /**
     * Get invoice number for CBI.
     * 
     * @return invoice number
     */
    String getCBIInvoiceNumber();

    /**
     * Prepare all required data for invoicing.
     * 
     * @param bo
     *            information that is necessary for invoicing
     * @param rollbackOnError
     *            boolean flag if successful items should not be invoiced in case of any errors
     * @param userId
     *            id of user responsible for invoicing
     * @return invoice ID
     */
    Long processInvoices(SendToFinanceBO bo, boolean rollbackOnError, Long userId);
}
