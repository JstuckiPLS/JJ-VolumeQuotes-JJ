package com.pls.core.service;

import com.pls.core.domain.organization.InvoiceSettingsEntity;

/**
 * Service that processes billTo's invoices.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface BillToInvoiceService {
    /**
     * Save invoice settings.
     *
     * @param invoiceSettings settings to save
     * @param billToId id of billTo entity
     */
    void saveInvoiceSettings(InvoiceSettingsEntity invoiceSettings, Long billToId);

    /**
     * Get invoice settings by id.
     *
     * @param id id of invoice settings to get
     * @return {@link InvoiceSettingsEntity}
     */
    InvoiceSettingsEntity getInvoiceSettings(Long id);

    /**
     * Get invoice settings by billTo id.
     *
     * @param billToId id of billTo to whom invoice settings need to be received
     * @return {@link InvoiceSettingsEntity}
     */
    InvoiceSettingsEntity getInvoiceSettingsForBillTo(Long billToId);
}
