package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlGuaranteedPriceDao;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.bo.GuaranteedPriceListItemVO;

/**
 * Implementation of {@link LtlGuaranteedPriceDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlGuaranteedPriceDaoImpl extends AbstractDaoImpl<LtlGuaranteedPriceEntity, Long> implements
        LtlGuaranteedPriceDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlGuaranteedPriceEntity> findByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.FIND_BY_PROFILE_ID);
        query.setParameter("priceProfileId", profileDetailId);

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlGuaranteedPriceEntity> findActiveAndEffectiveEntitiesForProfile(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.FIND_ACTIVE_AND_EFFECTIVE_FOR_PROFILE);
        query.setParameter("priceProfileId", profileDetailId);

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GuaranteedPriceListItemVO> findByStatusAndProfileDetailId(Status status, Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.FIND_BY_STATUS_AND_PROFILE_ID);
        query.setParameter("status", status);
        query.setParameter("priceProfileId", profileDetailId);
        query.setResultTransformer(new AliasToBeanResultTransformer(GuaranteedPriceListItemVO.class));

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GuaranteedPriceListItemVO> findActiveAndEffectiveByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.FIND_ACTIVE_AND_EFFECTIVE);
        query.setParameter("priceProfileId", profileDetailId);
        query.setResultTransformer(new AliasToBeanResultTransformer(GuaranteedPriceListItemVO.class));

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GuaranteedPriceListItemVO> findExpiredByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.FIND_EXPIRED);
        query.setParameter("priceProfileId", profileDetailId);
        query.setResultTransformer(new AliasToBeanResultTransformer(GuaranteedPriceListItemVO.class));

        return query.list();
    }

    @Override
    public void updateStatusOfGuaranteedPriceList(List<Long> guaranteedIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.UPDATE_STATUS);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameterList("ids", guaranteedIds);
        query.executeUpdate();
    }

    @Override
    public void expireByListOfIds(List<Long> ids, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.EXPIRE_BY_IDS);
        query.setParameterList("ids", ids);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatusToInactiveByProfileId(Long profileId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.INACTIVATE_BY_PROFILE_STATEMENT);
        query.setParameter("id", profileId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlGuaranteedPriceEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.FIND_CSP_ENTITY_BY_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void expirateCSPByCopiedFrom(List<Long> copiedFromIds, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.EXPIRATE_CSP_BY_COPIED_FROM_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.UPDATE_CSP_STATUS_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("status", status.getCode());
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlGuaranteedPriceEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }
}
