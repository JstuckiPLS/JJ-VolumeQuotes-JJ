package com.pls.shipment.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.LoadReasonTrackingStatusDao;
import com.pls.shipment.domain.LoadReasonTrackingStatusEntity;

/**
 * {@link LoadReasonTrackingStatusDao} implementation.
 *
 * @author Alexander Nalapko
 */
@Repository
@Transactional
public class LoadReasonTrackingStatusDaoImpl extends AbstractDaoImpl<LoadReasonTrackingStatusEntity, String> implements
        LoadReasonTrackingStatusDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LoadReasonTrackingStatusEntity> getAll(Long source) {
        return getCurrentSession().getNamedQuery(LoadReasonTrackingStatusEntity.GET_ALL).setParameter("source", source)
                .list();
    }

    @Override
    public LoadReasonTrackingStatusEntity find(String id, Long source) {
        return (LoadReasonTrackingStatusEntity) getCurrentSession().getNamedQuery(LoadReasonTrackingStatusEntity.FIND)
                .setParameter("id", id).setParameter("source", source).uniqueResult();
    }

    @Override
    public Map<String, String> getMap(Long source) {
        Map<String, String> result = new HashMap<String, String>();
        for (LoadReasonTrackingStatusEntity entity : getAll(source)) {
            result.put(entity.getId(), entity.getDescription());
        }
        return result;
    }
}
