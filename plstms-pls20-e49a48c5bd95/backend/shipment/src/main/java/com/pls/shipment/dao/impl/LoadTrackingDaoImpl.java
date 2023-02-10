package com.pls.shipment.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.shipment.dao.LoadReasonTrackingStatusDao;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LoadTrackingStatusDao;
import com.pls.shipment.domain.LoadReasonTrackingStatusEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.LoadTrackingStatusEntity;
import com.pls.shipment.domain.bo.LoadAuditBO;
import com.pls.shipment.domain.bo.LoadTrackingBO;

/**
 * {@link LoadTrackingDao} implementation.
 *
 * @author Mikhail Boldinov, 05/03/14
 */
@Repository
@Transactional
public class LoadTrackingDaoImpl extends AbstractDaoImpl<LoadTrackingEntity, Long> implements LoadTrackingDao {

    @Autowired
    private LoadTrackingStatusDao loadTrackingStatusDao;

    @Autowired
    private LoadReasonTrackingStatusDao loadReasonTrackingStatusDao;

    @Override
    public LoadTrackingEntity saveOrUpdate(LoadTrackingEntity entity) {
        // Add the created by user and created date when creating a new record.
        if (entity.getId() == null && entity.getCreatedBy() == null) {
            entity.setCreatedBy(SecurityUtils.getCurrentPersonId());
            entity.setCreatedDate(new Date());
        }

        if (entity.getSource() != null) {
            if (entity.getStatusCode() != null) {
                LoadTrackingStatusEntity status = loadTrackingStatusDao.find(entity.getStatusCode(), entity.getSource());
                entity.setStatus(status);
            }
            if (entity.getStatusReasonCode() != null) {
                LoadReasonTrackingStatusEntity status = loadReasonTrackingStatusDao.find(entity.getStatusReasonCode(), entity.getSource());
                entity.setStatusReason(status);
            }
        }
        return super.saveOrUpdate(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoadTrackingBO> findShipmentTracking(Long shipmentId) {
        Query query = getCurrentSession().getNamedQuery(LoadTrackingEntity.Q_GET_BY_LOAD_ID);
        query.setParameter("loadId", shipmentId);
        query.setResultTransformer(new AliasToBeanResultTransformer(LoadTrackingBO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoadAuditBO> findShipmentAudit(Long loadId) {
        return getCurrentSession().getNamedQuery(LoadTrackingEntity.Q_GET_AUDIT_BY_LOAD_ID).setParameter("loadId", loadId)
                .setResultTransformer(new AliasToBeanResultTransformer(LoadAuditBO.class)).list();
    }
}
