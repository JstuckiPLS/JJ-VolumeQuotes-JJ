package com.pls.core.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OutboundEdiQueueMappingDao;
import com.pls.core.domain.OutboundEdiQueueMapEntity;

/**
 * This Dao is created to retrieve data from outbound queue mapping database.
 * @author Yasaman Honarvar
 *
 */

@Repository
@Transactional
public class OutboundEdiQueueMappingDaoImpl extends AbstractDaoImpl<OutboundEdiQueueMapEntity, Long> implements OutboundEdiQueueMappingDao {
    @Override
    public OutboundEdiQueueMapEntity getQueueMappingsById(Long orgId) {

        Query query = getCurrentSession().getNamedQuery(OutboundEdiQueueMapEntity.Q_GET_ALL_QUEUE_MAPPINGS_BY_ID);
        query.setParameter("orgId", orgId);
        return (OutboundEdiQueueMapEntity) query.uniqueResult();
    }

    @Override
    public OutboundEdiQueueMapEntity getQueueMappingsByScac(String scac) {
        Query query = getCurrentSession().getNamedQuery(OutboundEdiQueueMapEntity.Q_GET_ALL_QUEUE_MAPPINGS_BY_SCAC);
        query.setParameter("scac", scac);
        return (OutboundEdiQueueMapEntity) query.uniqueResult();
    }
}
