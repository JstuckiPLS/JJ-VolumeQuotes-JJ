package com.pls.shipment.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.LoadTrackingStatusDao;
import com.pls.shipment.domain.LoadTrackingStatusEntity;
import com.pls.shipment.domain.LoadTrackingStatusEntityId;

/**
 * {@link LoadTrackingStatusDao} implementation.
 *
 * @author Mikhail Boldinov, 26/05/14
 */
@Repository
@Transactional
public class LoadTrackingStatusDaoImpl extends AbstractDaoImpl<LoadTrackingStatusEntity, LoadTrackingStatusEntityId> implements
        LoadTrackingStatusDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LoadTrackingStatusEntity> getAll(Long source) {
        return getCurrentSession().getNamedQuery(LoadTrackingStatusEntity.GET_ALL).setParameter("source", source)
                .list();
    }

    @Override
    public LoadTrackingStatusEntity find(String code, Long source) {
        return (LoadTrackingStatusEntity) getCurrentSession().getNamedQuery(LoadTrackingStatusEntity.FIND)
                .setParameter("code", code).setParameter("source", source).uniqueResult();
    }

    @Override
    public Map<String, String> getMap(Long source) {
        Map<String, String> result = new HashMap<String, String>();
        for (LoadTrackingStatusEntity entity : getAll(source)) {
            result.put(entity.getId().getCode(), entity.getDescription());
        }
        return result;
    }
}
