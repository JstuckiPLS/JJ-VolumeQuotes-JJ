package com.pls.invoice.service.processing;

import com.pls.core.exception.ApplicationException;

/**
 * Service for automatic invoice processing.
 *
 * @author Sergey Kirichenko
 */
public interface AutomaticInvoiceProcessingService {

    /**
     * Process invoices on a weekly basis. Creates reports sends them to the customers and process all
     * loads.
     * 
     * @throws ApplicationException
     *             if specified invoice id is already used for existing invoices
     */
    void processInvoicesWeekly() throws ApplicationException;

    /**
     * Process invoices on a daily basis. Creates reports sends them to the customers and process all
     * loads.
     * 
     * @throws ApplicationException
     *             if specified invoice id is already used for existing invoices
     */
    void processInvoicesDaily() throws ApplicationException;
}
