package com.pls.invoice.dao;

import java.util.List;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.invoice.domain.bo.CBIHistoryBO;
import com.pls.invoice.domain.bo.InvoiceHistoryBO;

/**
 * DAO for invoices history.
 *
 * @author Sergey Kirichenko
 */
public interface HistoryInvoiceDao {

    /**
     * Get list of Invoice History items.
     *
     * @param search
     *            object holding all necessary search parameters
     * @param userId
     *            id of user
     * @return list of {@link InvoiceHistoryBO}
     */
    List<InvoiceHistoryBO> getInvoiceHistory(RegularSearchQueryBO search, Long userId);

    /**
     * Get CBI history details.
     *
     * @param invoiceId
     *            invoiceId
     * @param groupInvoiceNumber
     *            group number of the invoice
     * @return list of {@link CBIInvoiceHistoryBO}
     */
    List<CBIHistoryBO> getInvoiceHistoryCBIDetails(Long invoiceId, String groupInvoiceNumber);
}
