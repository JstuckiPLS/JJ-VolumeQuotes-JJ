package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlFakMapDao;
import com.pls.ltlrating.domain.LtlFakMapEntity;

/**
 * Implementation of {@link LtlFakMapDao}.
 *
 * @author Hima Bindu Challa
 *
 */
@Transactional
@Repository
public class LtlFakMapDaoImpl extends AbstractDaoImpl<LtlFakMapEntity, Long> implements LtlFakMapDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlFakMapEntity> findByPricingDetailId(Long pricingDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlFakMapEntity.FIND_BY_PRICING_DETAIL_ID);
        query.setParameter("ltlPricingDetailId", pricingDetailId);

        return query.list();
    }

}
