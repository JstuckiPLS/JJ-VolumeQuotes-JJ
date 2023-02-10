package com.pls.quote.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.SavedQuotePricDtlsEntity;

/**
 * DAO for {@link SavedQuotePricDtlsEntity}.
 *
 * @author Ashwini Neelgund
 */
public interface SavedQuotePricDtlsDao extends AbstractDao<SavedQuotePricDtlsEntity, Long> {

    /**
     * Gets saved quote pricing details.
     *
     * @param quoteId {@link com.pls.shipment.domain.SavedQuoteEntity#getId()}
     * @return {@link com.pls.shipment.domain.SavedQuotePricDtlsEntity}
     */
    SavedQuotePricDtlsEntity getSavedQuotePricDtls(Long quoteId);

    /**
     * delete existing {@link SavedQuotePricDtlsEntity}.
     *
     * @param savedQuotePricDetail
     *            saved quote pricing detail
     */
    void delete(SavedQuotePricDtlsEntity savedQuotePricDetail);

}
