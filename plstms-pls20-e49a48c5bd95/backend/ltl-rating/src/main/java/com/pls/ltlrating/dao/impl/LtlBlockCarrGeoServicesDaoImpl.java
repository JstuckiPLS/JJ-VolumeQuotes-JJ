package com.pls.ltlrating.dao.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockCarrGeoServicesDao;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.bo.BlockCarrierListItemVO;

/**
 * Implementation of {@link LtlBlockCarrGeoServicesDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlBlockCarrGeoServicesDaoImpl extends AbstractDaoImpl<LtlBlockCarrGeoServicesEntity, Long> implements
        LtlBlockCarrGeoServicesDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlBlockCarrGeoServicesEntity> findActiveByProfileId(Status status, Long profileId) {
        return getCurrentSession().createCriteria(LtlBlockCarrGeoServicesEntity.class)
                .add(Restrictions.eq("profileId", profileId)).add(Restrictions.eq("status", status)).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BlockCarrierListItemVO> findByStatusAndProfileId(Status status, Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockCarrGeoServicesEntity.FIND_BY_STATUS_AND_PROFILE_ID);
        query.setParameter("profileId", profileId);
        query.setParameter("status", status.getCode());
        query.setResultTransformer(Transformers.aliasToBean(BlockCarrierListItemVO.class));
        return query.list();
    }

    @Override
    public void updateStatus(List<Long> ids, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockCarrGeoServicesEntity.UPDATE_STATUS_STATEMENT);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameterList("ids", ids);
        query.executeUpdate();
    }

    @Override
    public void inactivateByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockCarrGeoServicesEntity.INACTIVATE_BY_PROFILE_ID);
        query.setParameter("id", profileDetailId);
        query.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId());
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlBlockCarrGeoServicesEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockCarrGeoServicesEntity.FIND_CSP_ENTITY_BY_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockCarrGeoServicesEntity.UPDATE_STATUS_CHILD_CSP_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("status", status.getCode());
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockCarrGeoServicesEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlBlockCarrGeoServicesEntity> findByProfileDetailId(Long profileDetailId) {
        return getCurrentSession().createCriteria(LtlBlockCarrGeoServicesEntity.class)
                .add(Restrictions.eq("profileId", profileDetailId)).list();
    }

    @Override
    public void deleteGeoServiceDetails(Set<Long> geoServiceId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockCarrGeoServicesEntity.DELETE_GEO_SERVICE_DTLS_BY_SERVICE_ID);
        query.setParameterList("geoServiceId", geoServiceId);
        query.executeUpdate();
    }
}