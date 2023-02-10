package com.pls.invoice.service;

import java.util.List;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.exception.ApplicationException;
import com.pls.invoice.domain.bo.CBIHistoryBO;
import com.pls.invoice.domain.bo.InvoiceHistoryBO;
import com.pls.invoice.domain.bo.ReprocessHistoryBO;

/**
 * Service to handle invoice history.
 *
 * @author Alexander Kirichenko
 */
public interface InvoiceHistoryService {

    /**
     * Get list of Invoice History items.
     *
     * @param search
     *            object holding all necessary search parameters
     * @param userId
     *            id of user
     * @return list of {@link com.pls.invoice.domain.bo.InvoiceHistoryBO}
     */
    List<InvoiceHistoryBO> getInvoiceHistory(RegularSearchQueryBO search, Long userId);

    /**
     * Get CBI history details.
     *
     * @param invoiceId
     *            invoice ID
     * @param groupInvoiceNumber
     *            group number of the invoice
     * @return list of {@link CBIInvoiceHistoryBO}
     */
    List<CBIHistoryBO> getInvoiceHistoryCBIDetails(Long invoiceId, String groupInvoiceNumber);

    /**
     * Re-process history invoice.
     *
     * @param reprocessBO
     *            Business Object with parameters required for invoice re-processing
     * @param personId
     *            id of user
     * @throws com.pls.core.exception.ApplicationException
     *             if invoice generation or sending of email failed
     */
    void reprocessHistory(ReprocessHistoryBO reprocessBO, Long personId) throws ApplicationException;
}
