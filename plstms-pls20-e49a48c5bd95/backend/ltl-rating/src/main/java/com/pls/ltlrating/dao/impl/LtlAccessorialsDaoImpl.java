package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlAccessorialsDao;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.bo.AccessorialListItemVO;

/**
 * Implementation of {@link LtlAccessorialsDao}.
 *
 * @author Hima Bindu Challa
 */
@Transactional
@Repository
public class LtlAccessorialsDaoImpl extends AbstractDaoImpl<LtlAccessorialsEntity, Long> implements LtlAccessorialsDao {

    /**
     * To get All Accessorials irrespective of status and expiration date for the given profile Id.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<LtlAccessorialsEntity> - List of all LtlAccessorialsEntities for selected profile
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LtlAccessorialsEntity> findAllByProfileDetailId(Long profileDetailId) {
        return getCriteria().add(Restrictions.eq("ltlPricProfDetailId", profileDetailId)).list();
    }

    /**
     * To get All Accessorials irrespective expiration date for the given status and profile Id. Will return
     * all active profiles or all inactive profiles.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @param status
     *            - Status of the accessorial - Active/Inactive = A/I
     * @return List<AccessorialListItemVO> - List of all LtlAccessorialsEntities for selected profile
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AccessorialListItemVO> findAllByStatusAndProfileDetailId(Long profileDetailId, Status status) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.Q_FIND_BY_STATUS_AND_PROFILE_ID);
        query.setParameter("profileDetailId", profileDetailId);
        query.setParameter("status", status.getCode());
        query.setResultTransformer(Transformers.aliasToBean(AccessorialListItemVO.class));
        return query.list();
    }

    /**
     * To get All active and effective Accessorials for the given profile Id.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<LtlAccessorialsEntity> - List of all LtlAccessorialsEntities for selected profile
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LtlAccessorialsEntity> findActiveAndEffectiveForProfile(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.Q_ALL_ACTIVE_AND_EFFECTIVE_FOR_PROFILE);
        query.setParameter("profileDetailId", profileDetailId);
        return query.list();
    }

    /**
     * To get All active and effective Accessorials for the given profile Id.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<AccessorialListItemVO> - List of all LtlAccessorialsEntities for selected profile
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AccessorialListItemVO> findActiveAndEffectiveByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.Q_ALL_ACTIVE_AND_EFFECTIVE_BY_PROFILE_ID);
        query.setParameter("profileDetailId", profileDetailId);
        query.setResultTransformer(Transformers.aliasToBean(AccessorialListItemVO.class));
        return query.list();
    }

    /**
     * To get All active and expired Accessorials for the given profile Id.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<AccessorialListItemVO> - List of all LtlAccessorialsEntities for selected profile
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AccessorialListItemVO> findExpiredByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.Q_ALL_ACTIVE_BY_PROFILE_ID);
        query.setParameter("profileDetailId", profileDetailId);
        query.setResultTransformer(Transformers.aliasToBean(AccessorialListItemVO.class));
        return query.list();
    }

    @Override
    public void expireByListOfIds(List<Long> ids, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.EXPIRE_BY_IDS);
        query.setParameterList("ids", ids);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    /**
     * To archive/inactivate multiple active accessorials by Profile Detail ID.
     *
     * This method inactivates all active accessorials and is used when cloning the accessorials from another
     * Profile.
     *
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be inactivated
     */
    @Override
    public void inactivateActiveAndEffAccByProfDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.INACTIVATE_ACTIVE_EFF_BY_PROFILE_ID);
        query.setParameter("id", profileDetailId);
        query.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId());
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlAccessorialsEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.FIND_CSP_ACC_BY_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void expirateCSPByCopiedFrom(List<Long> copiedFromIds, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.EXPIRATE_CPS_BY_COPIED_FROM_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.UPDATE_CSP_STATUS_STATEMENT);
        query.setParameterList("ownerIds", copiedFromIds);
        query.setParameter("status", status.getCode());
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void updateStatuses(List<Long> accessorialIds, Status status, Long modifiedBy) {
        getCurrentSession().getNamedQuery(LtlAccessorialsEntity.UPDATE_STATUSES_STATEMENT).setParameterList("ids", accessorialIds).
                setParameter("status", status).setParameter("modifiedBy", modifiedBy).executeUpdate();
    }

    @Override
    public void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsEntity.INACTIVATE_CSP_BY_DETAIL_ID);
        query.setParameter("ownerId", profileDetailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }
}
