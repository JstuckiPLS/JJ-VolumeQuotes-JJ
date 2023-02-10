package com.pls.invoice.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pls.invoice.domain.bo.CustomerInvoiceBO;
import com.pls.invoice.domain.bo.CustomerInvoiceCO;
import com.pls.invoice.domain.bo.CustomerInvoiceSummaryBO;

/**
 * DAO to provide functionality for customer invoice service. This DAO isn't extend {@link com.pls.core.dao.AbstractDao} due to it works with
 * criteria API.
 *
 * @author Alexander Kirichenko
 */
public interface CustomerInvoiceDao {

    /**
     * Method finds and returns {@link List} of {@link CustomerInvoiceBO} for specif customer and specified conditions.
     * @param customerId - owner of invoices
     * @param queryParams - filtered parameters {@link CustomerInvoiceCO}
     * @return - {@link List} of {@link CustomerInvoiceBO} for specif customer and specified conditions
     */
    List<CustomerInvoiceBO> findCustomerInvoices(Long customerId, CustomerInvoiceCO queryParams);

    /**
     * Method returns {@link CustomerInvoiceSummaryBO}.
     * @param customerId - owner of invoices
     * @return {@link CustomerInvoiceSummaryBO}
     */
    CustomerInvoiceSummaryBO getCustomerInvoiceSummary(Long customerId);

    /**
     * Gets total amount which is unpaid by customer.
     *
     * @param customerId - owner of invoices
     * @return unpaid amount
     */
    BigDecimal getCustomerUnpaidAmount(Long customerId);
}
