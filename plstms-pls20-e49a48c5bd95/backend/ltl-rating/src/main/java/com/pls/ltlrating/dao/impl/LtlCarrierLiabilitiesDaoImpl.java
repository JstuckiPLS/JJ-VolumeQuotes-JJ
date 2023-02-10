package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlCarrierLiabilitiesDao;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;

/**
 * Implementation of {@link LtlCarrierLiabilitiesDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlCarrierLiabilitiesDaoImpl extends AbstractDaoImpl<LtlCarrierLiabilitiesEntity, Long>
                                            implements LtlCarrierLiabilitiesDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlCarrierLiabilitiesEntity> findCarrierLiabilitiesByProfileId(Long profileId) {
        return getCurrentSession().createCriteria(LtlCarrierLiabilitiesEntity.class)
                .add(Restrictions.eq("pricingProfileId", profileId)).list();
    }

    @Override
    public List<LtlCarrierLiabilitiesEntity> saveCarrierLiabilitiesList(List<LtlCarrierLiabilitiesEntity> list) {
        if (list == null) {
            throw new IllegalArgumentException("Argument 'list' can't be null");
        }

        for (LtlCarrierLiabilitiesEntity item : list) {
            getCurrentSession().saveOrUpdate(item);
        }

        return list;
    }

    @Override
    public void deleteLiabilities(Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlCarrierLiabilitiesEntity.DELETE_FOR_PROFILE);
        query.setParameter("profileId", profileId);
        query.executeUpdate();
    }

}
