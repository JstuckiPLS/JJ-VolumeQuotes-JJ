package com.pls.invoice.service.processing;

import java.util.List;

import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Helper service for invoice processing.
 * 
 * @author Aleksandr Leshchenko
 */
public interface InvoiceService {
    /**
     * Get list of loads and adjustments by invoice numbers sorted by specified field.
     * 
     * @param invoiceId
     *            invoice ID
     * @return list of sorted loads and adjustments
     */
    List<LoadAdjustmentBO> getSortedInvoices(Long invoiceId);
}
