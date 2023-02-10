package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlPricingNotesDao;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;

/**
 * Implementation of {@link LtlPricingNotesDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlPricingNotesDaoImpl extends AbstractDaoImpl<LtlPricingNotesEntity, Long> implements
        LtlPricingNotesDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlPricingNotesEntity> findByProfileId(Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingNotesEntity.FIND_BY_PROFILE_ID);
        query.setParameter("priceProfileId", profileId);

        return query.list();
    }

}
