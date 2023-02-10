package com.pls.ltlrating.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlFuelSurchargeDao;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;


/**
 *
 * {@link LtlFuelSurchargeDao} implementation.
 *
 * @author Stas Norochevskiy
 *
 */
@Repository
@Transactional
public class LtlFuelSurchargeDaoImpl extends AbstractDaoImpl<LtlFuelSurchargeEntity, Long>
    implements LtlFuelSurchargeDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlFuelSurchargeEntity> getActiveFuelSurchargeByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.ACTIVE_FUEL_SURCHARGE);
        query.setParameter("profileDetailId", profileDetailId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getFuelSurchargeByFuelCharge(BigDecimal charge) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.GET_BY_FUEL_CHARGE);
        query.setParameter("charge", charge);
        List<LtlFuelSurchargeEntity> list = query.list();
        return list.isEmpty() ? null : list.get(0).getSurcharge();
    }

    @Override
    public void updateStatusToInactiveByProfileId(Long profileId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.INACTIVATE_BY_PROFILE_STATEMENT);
        query.setParameter("id", profileId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlFuelSurchargeEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.FIND_CSP_FUEL_SURCHARGE_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void updateStatusByListOfIds(List<Long> ids, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.UPDATE_STATUS_STATEMENT);
        query.setParameterList("ids", ids);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.UPDATE_CSP_STATUS_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("status", status.getCode());
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlFuelSurchargeEntity> getAllByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlFuelSurchargeEntity.Q_FIND_ALL_BY_PROFILE_DETAIL_ID);
        query.setParameter("profileDetailId", profileDetailId);
        return query.list();
    }
}
