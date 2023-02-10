package com.pls.core.dao;

import com.pls.core.domain.organization.InvoiceSettingsEntity;

/**
 * DAO for invoice settings.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface InvoiceSettingsDao extends AbstractDao<InvoiceSettingsEntity, Long> {
    /**
     * Get invoice settings by billTo id.
     *
     * @param billToId id of billTo
     * @return {@link InvoiceSettingsEntity}
     */
    InvoiceSettingsEntity getByBillToId(long billToId);
}
