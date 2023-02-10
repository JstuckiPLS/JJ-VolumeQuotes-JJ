package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlPricingBlockedCustomersDao;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;

/**
 * {@link LtlPricingBlockedCustomersDao} implementation.
 *
 * @author Mikhail Boldinov, 22/02/13
 */
@Transactional
@Repository
public class LtlPricingBlockedCustomersDaoImpl extends AbstractDaoImpl<LtlPricingBlockedCustomersEntity, Long>
        implements LtlPricingBlockedCustomersDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlPricingBlockedCustomersEntity> getExplicitlyBlockedCustomersByProfileId(Long profileId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("ltlPricingProfileId", profileId));
        return criteria.list();
    }

}
