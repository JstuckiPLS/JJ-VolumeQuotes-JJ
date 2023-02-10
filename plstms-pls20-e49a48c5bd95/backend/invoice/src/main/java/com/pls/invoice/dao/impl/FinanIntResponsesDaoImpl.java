package com.pls.invoice.dao.impl;

import java.util.List;

import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.invoice.dao.FinanIntResponsesDao;
import com.pls.invoice.domain.FinanIntResponsesEntity;

/**
 * {@link FinanIntResponsesDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class FinanIntResponsesDaoImpl extends AbstractDaoImpl<FinanIntResponsesEntity, Long> implements FinanIntResponsesDao {

    @Override
    public void insertLoads(Long requestId, List<Long> loadsIds, Long userId) {
        getCurrentSession().getNamedQuery(FinanIntResponsesEntity.I_LOADS)
                .setLong("requestId", requestId)
                .setParameterList("loadsIds", loadsIds, LongType.INSTANCE)
                .setLong("userId", userId)
                .executeUpdate();
    }

    @Override
    public void insertAdjustments(Long requestId, List<Long> adjustmentsIds, Long userId) {
        getCurrentSession().getNamedQuery(FinanIntResponsesEntity.I_ADJUSTMENTS)
                .setLong("requestId", requestId)
                .setParameterList("adjustmentsIds", adjustmentsIds, LongType.INSTANCE)
                .setLong("userId", userId)
                .executeUpdate();
    }

}
