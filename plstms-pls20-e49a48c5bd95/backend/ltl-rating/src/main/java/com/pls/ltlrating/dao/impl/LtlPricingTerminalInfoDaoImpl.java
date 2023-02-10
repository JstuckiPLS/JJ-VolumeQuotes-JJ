package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingTerminalInfoDao;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;

/**
 * Implementation of {@link LtlPricingTerminalInfoDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlPricingTerminalInfoDaoImpl extends AbstractDaoImpl<LtlPricingTerminalInfoEntity, Long> implements
        LtlPricingTerminalInfoDao {

    @Override
    public LtlPricingTerminalInfoEntity findActiveByProfileDetailId(Long profileId) {
        return (LtlPricingTerminalInfoEntity) getCurrentSession().createCriteria(LtlPricingTerminalInfoEntity.class)
                .add(Restrictions.and(Restrictions.eq("priceProfileId", profileId),
                        Restrictions.eq("status", Status.ACTIVE))).uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlPricingTerminalInfoEntity> findByProfileDetailId(Long profileDetailId) {
        return getCurrentSession().createCriteria(LtlPricingTerminalInfoEntity.class)
                .add(Restrictions.eq("priceProfileId", profileDetailId)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlPricingTerminalInfoEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingTerminalInfoEntity.FIND_CSP_ENTITY_BY_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void updateStatus(Long id, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingTerminalInfoEntity.UPDATE_STATUS_STATEMENT);
        query.setParameter("id", id);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingTerminalInfoEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }
}
