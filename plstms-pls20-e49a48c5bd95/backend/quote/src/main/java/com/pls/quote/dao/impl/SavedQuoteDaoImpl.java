package com.pls.quote.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.quote.dao.SavedQuoteDao;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * {@link SavedQuoteDao} implementation.
 *
 * @author Mikhail Boldinov, 20/03/13
 */
@Repository
@Transactional
public class SavedQuoteDaoImpl extends AbstractDaoImpl<SavedQuoteEntity, Long> implements SavedQuoteDao {

    @Override
    public void updateStatus(Long quoteId, Status status) {
        Query query = getCurrentSession().getNamedQuery(SavedQuoteEntity.Q_UPDATE_STATUS);
        query.setParameter("new_status", status);
        query.setParameter("quote_id", quoteId);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SavedQuoteBO> findSavedQuotes(Long customerId, Long personId, Date fromDate, Date toDate) {
        Query query = getCurrentSession().getNamedQuery(SavedQuoteEntity.Q_FIND_SAVED_QUOTES);
        query.setParameter("customerId", customerId);
        query.setParameter("personId", personId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setResultTransformer(Transformers.aliasToBean(SavedQuoteBO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getListOfLoadIds(Long quoteId) {
        Query query = getCurrentSession().getNamedQuery(SavedQuoteEntity.Q_FIND_LOAD_ID_LIST_FOR_SAVED_QUOTE);
        query.setParameter("quoteId", quoteId);
        return query.list();
    }
}
