package com.pls.quote.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.quote.dao.SavedQuotePricDtlsDao;
import com.pls.shipment.domain.SavedQuotePricDtlsEntity;

/**
 * {@link SavedQuotePricDtlsDao} implementation.
 *
 * @author Ashwini Neelgund
 */
@Repository
@Transactional
public class SavedQuotePricDtlsDaoImpl extends AbstractDaoImpl<SavedQuotePricDtlsEntity, Long>
        implements SavedQuotePricDtlsDao {

    @Override
    public SavedQuotePricDtlsEntity getSavedQuotePricDtls(Long quoteId) {
        return (SavedQuotePricDtlsEntity) getCurrentSession()
                .getNamedQuery(SavedQuotePricDtlsEntity.Q_SAVED_QUOTE_PRIC_DTLS).setParameter("quoteId", quoteId)
                .uniqueResult();
    }

    @Override
    public void delete(SavedQuotePricDtlsEntity savedQuotePricDetail) {
        getCurrentSession().delete(savedQuotePricDetail);
    }

}
