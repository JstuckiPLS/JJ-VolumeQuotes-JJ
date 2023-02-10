package com.pls.invoice.service.processing;

import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;

/**
 * Service to send invoices to customer via EDI/JMS.
 * 
 * @author Jasmin Dhamelia
 */
public interface EDIInvoiceProcessingService {

    /**
     * Send invoices to customer via EDI/JMS.
     * 
     * @param invoiceId
     *            invoice ID
     * @param billTo
     *            Bill to number
     * @throws ApplicationException
     *             if sending to customer failed
     */
    void sendInvoiceViaEDI(Long invoiceId, BillToEntity billTo) throws ApplicationException;

}
