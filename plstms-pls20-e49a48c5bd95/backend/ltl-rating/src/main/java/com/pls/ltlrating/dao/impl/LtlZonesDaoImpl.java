package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlZonesDao;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.domain.bo.ZonesListItemVO;

/**
 * Implementation of {@link LtlZonesDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlZonesDaoImpl extends AbstractDaoImpl<LtlZonesEntity, Long> implements LtlZonesDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlZonesEntity> findByProfileDetailId(Long profileId) {
        List<LtlZonesEntity> zones = getCurrentSession().createCriteria(LtlZonesEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq("ltlPricProfDetailId", profileId)).list();
        return zones;
    }

    @Override
    public LtlZonesEntity findZoneByProfileDetailIdAndName(Long profileDetailId, String name) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.GET_ZONE_BY_PROFILE_DETAIL_ID_AND_NAME);
        query.setParameter("profileDetailId", profileDetailId);
        query.setParameter("name", name);
        return (LtlZonesEntity) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ZonesListItemVO> findByStatusAndProfileId(Status status, Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.GET_ZONE_BY_STATUS_AND_PROFILE_ID);
        query.setParameter("profileDetailId", profileId);
        query.setParameter("status", status.getCode());
        query.setResultTransformer(Transformers.aliasToBean(ZonesListItemVO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlZonesEntity> findActiveForProfile(Long profileId) {
        List<LtlZonesEntity> zones = getCurrentSession().createCriteria(LtlZonesEntity.class).add(Restrictions.eq("status", Status.ACTIVE))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("ltlPricProfDetailId", profileId)).list();
        return zones;
    }

    @Override
    public void updateStatus(List<Long> ids, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.UPDATE_STATUS_STATEMENT);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);

        for (Long id : ids) {
            query.setParameter("id", id);
            query.executeUpdate();
        }
    }

    @Override
    public void inactivateByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.INACTIVATE_BY_PROFILE_ID);
        query.setParameter("id", profileDetailId);
        query.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId());
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlZonesEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.FIND_CSP_ENTITY_BY_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.UPDATE_CSP_STATUS_STATEMENT);
        query.setParameterList("ownersId", copiedFromIds);
        query.setParameter("status", status.getCode());
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public LtlZonesEntity getMatchingZoneByName(Long profileDetailId, Long fromZoneId) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.GET_ZONE_WITH_MATCHING_NAME);
        query.setParameter("id", profileDetailId);
        query.setParameter("zoneId", fromZoneId);
        query.setFetchSize(1);

        List<LtlZonesEntity> zones = query.list();
        if (!zones.isEmpty()) {
            return zones.get(0);
        }
        return null;
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlZonesEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }
}
