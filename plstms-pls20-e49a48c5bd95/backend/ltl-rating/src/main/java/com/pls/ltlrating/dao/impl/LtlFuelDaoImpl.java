package com.pls.ltlrating.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlFuelDao;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelEntity;
import com.pls.ltlrating.domain.bo.FuelListItemVO;

/**
 * Implementation for {@link LtlFuelDao}.
 *
 * @author Stas Norochevskiy
 *
 */
@Transactional
@Repository
public class LtlFuelDaoImpl extends AbstractDaoImpl<LtlFuelEntity, Long> implements LtlFuelDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlFuelEntity> getAllFuelTriggersByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.ALL_FUEL_TRIGGERS_BY_PROFILE_DETAILS);
        query.setParameter("profileDetailId", profileDetailId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FuelListItemVO> getAllFuelTriggersByProfileDetailIdAndStatus(Long profileDetailId, Status status) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.ALL_FUEL_TRIGGERS);
        query.setParameter("profileDetailId", profileDetailId);
        query.setParameter("status", status.getCode());
        query.setResultTransformer(Transformers.aliasToBean(FuelListItemVO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlFuelEntity> getActiveAndEffectiveForProfile(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.FUEL_TRIGGERS_FOR_PROFILE);
        query.setParameter("profileDetailId", profileDetailId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FuelListItemVO> getActiveAndEffectiveByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.FUEL_TRIGGERS_BY_PROFILE_DETAIL);
        query.setParameter("profileDetailId", profileDetailId);
        query.setResultTransformer(Transformers.aliasToBean(FuelListItemVO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FuelListItemVO> getFuelTriggersByProfileDetailWithExpireDateLessThan(Long profileDetailId, Date date) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.EXPIRED_FUEL_TRIGGERS);
        query.setParameter("profileDetailId", profileDetailId);
        query.setResultTransformer(Transformers.aliasToBean(FuelListItemVO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DotRegionFuelEntity> getDOTRegionsAndFuelRates() {
        return getCurrentSession().createCriteria(DotRegionFuelEntity.class).list();
    }

    @Override
    public void updateFuelStatus(List<Long> fuelTriggerIds, Status newStatus) {
        Query query = getCurrentSession().getNamedQuery(
                LtlFuelEntity.UPDATE_STATUS);
        query.setParameterList("ids", fuelTriggerIds);
        query.setParameter("newStatus", newStatus);
        query.executeUpdate();
    }

    @Override
    public DotRegionFuelEntity saveDotRegionFuelEntity(DotRegionFuelEntity dotRegionFuelEntity) {
        Session session = getCurrentSession();
        session.saveOrUpdate(dotRegionFuelEntity);
        return dotRegionFuelEntity;
    }

    @Override
    public void updateStatusToInactiveByProfileId(Long profileId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.INACTIVATE_BY_PROFILE_STATEMENT);
        query.setParameter("id", profileId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void expireByListOfIds(List<Long> ids, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.EXPIRE_BY_IDS);
        query.setParameterList("ids", ids);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlFuelEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.FIND_CSP_FUEL_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void expirateCSPByCopiedFrom(List<Long> copiedFromIds, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.EXPIRATE_CSP_BY_COPIED_FROM_STATEMENT);
        query.setParameterList("ownersIds", copiedFromIds);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.UPDATE_CSP_STATUS_STATEMENT);
        query.setParameterList("ownersIds", copiedFromIds);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameter("status", status.getCode());
        query.executeUpdate();
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }
}
