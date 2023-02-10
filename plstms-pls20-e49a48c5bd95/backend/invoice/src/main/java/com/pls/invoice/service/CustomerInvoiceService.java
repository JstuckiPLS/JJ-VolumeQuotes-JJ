package com.pls.invoice.service;

import java.util.List;

import com.pls.core.domain.bo.CustomerCreditInfoBO;
import com.pls.invoice.domain.bo.CustomerInvoiceBO;
import com.pls.invoice.domain.bo.CustomerInvoiceCO;
import com.pls.invoice.domain.bo.CustomerInvoiceSummaryBO;

/**
 * Service to provide information about invoices to customers.
 *
 * @author Alexander Kirichenko
 */
public interface CustomerInvoiceService {

    /**
     * Method finds and returns {@link List} of {@link CustomerInvoiceBO} for specif customer and specified conditions.
     * @param customerId - owner of invoices
     * @param queryParams - filtered parameters {@link CustomerInvoiceCO}
     * @return - {@link List} of {@link CustomerInvoiceBO} for specif customer and specified conditions
     */
    List<CustomerInvoiceBO> findCustomerInvoices(Long customerId, CustomerInvoiceCO queryParams);

    /**
     * Method returns {@link CustomerInvoiceSummaryBO}.
     * @param customerId - id of customer
     * @return {@link CustomerInvoiceSummaryBO}
     */
    CustomerInvoiceSummaryBO getCustomerInvoiceSummary(Long customerId);

    /**
     * Gets customer credit info.
     *
     * @param customerId customer id
     * @return {@link com.pls.core.domain.bo.CustomerCreditInfoBO}
     */
    CustomerCreditInfoBO getCustomerCreditInfo(Long customerId);
}
