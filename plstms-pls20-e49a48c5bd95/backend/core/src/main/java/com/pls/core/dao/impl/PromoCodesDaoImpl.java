package com.pls.core.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.PromoCodesDao;
import com.pls.core.domain.user.PromoCodeEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;

/**
 * {@link PromoCodesDao} implementation.
 * 
 * @author Brichak Aleksandr
 *
 */

@Repository
@Transactional
public class PromoCodesDaoImpl extends AbstractDaoImpl<PromoCodeEntity, Long> implements PromoCodesDao {

    @Override
    public boolean isPromoCodeUnique(String code, Long personId) {
        Query query = getCurrentSession().getNamedQuery(PromoCodeEntity.Q_IS_PROMO_CODE_UNIQUE);
        query.setParameter("promoCode", code);
        query.setParameter("personId", personId);
        return query.list().isEmpty();
    }

    @Override
    public PromoCodeEntity getPromoCodeByUser() {
        Query query = getCurrentSession().getNamedQuery(PromoCodeEntity.Q_FIND_BY_USER);
        query.setParameter("personId", SecurityUtils.getCurrentPersonId());
        return (PromoCodeEntity) query.setMaxResults(1).uniqueResult();
    }
}
