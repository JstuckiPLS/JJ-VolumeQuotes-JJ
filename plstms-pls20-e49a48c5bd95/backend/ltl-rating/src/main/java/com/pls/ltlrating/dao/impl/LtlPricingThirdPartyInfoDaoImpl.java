package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingThirdPartyInfoDao;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * Implementation of {@link LtlPricingThirdPartyInfoDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlPricingThirdPartyInfoDaoImpl extends AbstractDaoImpl<LtlPricingThirdPartyInfoEntity, Long> implements
        LtlPricingThirdPartyInfoDao {

    @Override
    public LtlPricingThirdPartyInfoEntity findActiveByProfileDetailId(Long profileId) {

        return (LtlPricingThirdPartyInfoEntity) getCurrentSession()
                .createCriteria(LtlPricingThirdPartyInfoEntity.class)
                .add(Restrictions.and(Restrictions.eq("pricProfDetailId", profileId),
                        Restrictions.eq("status", Status.ACTIVE))).uniqueResult();
    }

    @Override
    public LtlPricingThirdPartyInfoEntity findByProfileId(Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingThirdPartyInfoEntity.FIND_THIRD_PARTY_BY_PROFILE_ID);
        query.setParameter("id", profileId);

        return (LtlPricingThirdPartyInfoEntity) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlPricingThirdPartyInfoEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingThirdPartyInfoEntity.FIND_CSP_ENTITY_BY_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void updateStatus(Long id, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingThirdPartyInfoEntity.UPDATE_STATUS_STATEMENT);
        query.setParameter("id", id);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingThirdPartyInfoEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlPricingThirdPartyInfoEntity> findByProfileDetailId(Long profileDetailId) {
        return getCurrentSession().createCriteria(LtlPricingThirdPartyInfoEntity.class)
                .add(Restrictions.eq("pricProfDetailId", profileDetailId)).list();
    }
}
