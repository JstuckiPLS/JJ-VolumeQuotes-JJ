package com.pls.ltlrating.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingDetailsDao;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;

/**
 * Implementation of {@link LtlPricingDetailsDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlPricingDetailsDaoImpl extends AbstractDaoImpl<LtlPricingDetailsEntity, Long> implements LtlPricingDetailsDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlPricingDetailsEntity> findActiveAndEffectiveForProfile(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.FIND_ACTIVE_FOR_PROFILE_QUERY);
        query.setParameter("detailId", profileDetailId);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PricingDetailListItemVO> findActiveAndEffectiveByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.FIND_BY_STATUS);
        query.setLong("detailId", profileDetailId);
        query.setString("status", Status.ACTIVE.getCode());
        query.setInteger("expired", 0);
        query.setResultTransformer(Transformers.aliasToBean(PricingDetailListItemVO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PricingDetailListItemVO> findExpiredByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.FIND_BY_STATUS);
        query.setLong("detailId", profileDetailId);
        query.setString("status", Status.ACTIVE.getCode());
        query.setInteger("expired", 1);
        query.setResultTransformer(Transformers.aliasToBean(PricingDetailListItemVO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PricingDetailListItemVO> findArchivedPrices(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.FIND_BY_STATUS);
        query.setLong("detailId", profileDetailId);
        query.setString("status", Status.INACTIVE.getCode());
        query.setInteger("expired", 0);
        query.setResultTransformer(Transformers.aliasToBean(PricingDetailListItemVO.class));
        return query.list();
    }

    @Override
    public void updateStatus(List<Long> priceDetailIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.UPDATE_STATUS_STATEMENT);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameterList("ids", priceDetailIds);
        query.executeUpdate();
    }

    @Override
    public void updateStatusToInactiveByProfileId(Long profileId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.INACTIVATE_BY_PROFILE_STATEMENT);
        query.setParameter("id", profileId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatusToExpired(List<Long> priceDetailIds, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.EXPIRATE_STATEMENT);
        query.setParameterList("ids", priceDetailIds);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public List<LtlPricingDetailsEntity> findAllByCopiedFrom(Long copiedFrom) {
        Map<String, Object> paramMap = new HashMap<String, Object>(1);
        paramMap.put("copiedFrom", copiedFrom);

        return findByNamedQuery(LtlPricingDetailsEntity.FIND_BY_COPIED_FROM_QUERY, paramMap);
    }

    @Override
    public void expirateCSPByCopiedFrom(List<Long> copiedFromIds, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.EXPIRATE_CPS_BY_COPIED_FROM_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingDetailsEntity.UPDATE_CSP_STATUS_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("status", status.getCode());
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }
}
