package com.pls.invoice.dao;

import java.util.Collection;
import java.util.List;

import com.pls.invoice.domain.xml.finance.FinanceInfoTable;

/**
 * DAO for processing invoice to AX.
 * 
 * @author Aleksandr Leshchenko
 */
public interface InvoiceToAXDao {
    /**
     * Method returns {@link Collection} of finance info that contains file name type of finance info and XML
     * message part.
     * 
     * @param invoiceId
     *            invoice ID.
     * @return {@link List} of {@link FinanceInfoTable}.
     */
    Collection<FinanceInfoTable> getDataForFinanceSystemByInvoiceID(Long invoiceId);
}
