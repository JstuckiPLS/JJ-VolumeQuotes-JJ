package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockLaneDao;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;
import com.pls.ltlrating.domain.bo.BlanketCarrListItemVO;
import com.pls.ltlrating.domain.bo.BlockLaneListItemVO;

/**
 * Implementation of {@link LtlBlockLaneDao}.
 *
 * @author Ashwini Neelgund
 *
 */
@Transactional
@Repository
public class LtlBlockLaneDaoImpl extends AbstractDaoImpl<LtlBlockLaneEntity, Long> implements LtlBlockLaneDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<BlockLaneListItemVO> findActiveAndEffectiveByProfileId(Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockLaneEntity.FIND_ACTIVE_AND_EFFECTIVE);
        query.setParameter("profileId", profileId);
        query.setResultTransformer(new AliasToBeanResultTransformer(BlockLaneListItemVO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BlockLaneListItemVO> findByStatusAndProfileId(String status, Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockLaneEntity.FIND_BY_STATUS_AND_PROFILE_ID);
        query.setParameter("status", status);
        query.setParameter("profileId", profileId);
        query.setResultTransformer(new AliasToBeanResultTransformer(BlockLaneListItemVO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BlockLaneListItemVO> findExpiredByProfileId(Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockLaneEntity.FIND_EXPIRED);
        query.setParameter("profileId", profileId);
        query.setResultTransformer(new AliasToBeanResultTransformer(BlockLaneListItemVO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BlanketCarrListItemVO> getUnblockedBlanketCarrListForCust(Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockLaneEntity.GET_UNBLOCKED_BLANKET_CARR_PROF_FOR_CUST);
        query.setParameter("profileId", profileId);
        query.setResultTransformer(new AliasToBeanResultTransformer(BlanketCarrListItemVO.class));
        return query.list();
    }

    @Override
    public void expireBlockedLanes(List<Long> blockedLaneIds, Long personId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockLaneEntity.EXPIRE_BY_IDS);
        query.setParameterList("ids", blockedLaneIds);
        query.setParameter("modifiedBy", personId);
        query.executeUpdate();
    }

    @Override
    public void updateStatusOfBlockedLanes(List<Long> blockedLaneIds, Status status, Long personId) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockLaneEntity.UPDATE_STATUS);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", personId);
        query.setParameterList("ids", blockedLaneIds);
        query.executeUpdate();
    }

    @Override
    public BlockLaneListItemVO findById(Long id) {
        Query query = getCurrentSession().getNamedQuery(LtlBlockLaneEntity.GET_BLOCK_LANE_BY_ID);
        query.setParameter("id", id);
        query.setResultTransformer(new AliasToBeanResultTransformer(BlockLaneListItemVO.class));
        return (BlockLaneListItemVO) query.uniqueResult();
    }

}
