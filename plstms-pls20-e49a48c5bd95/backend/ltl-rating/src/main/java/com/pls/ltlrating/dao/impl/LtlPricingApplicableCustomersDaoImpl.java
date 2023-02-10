package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlPricingApplicableCustomersDao;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;

/**
 * {@link LtlPricingApplicableCustomersDao} implementation.
 *
 * @author Mikhail Boldinov, 22/02/13
 */
@Repository
@Transactional
public class LtlPricingApplicableCustomersDaoImpl extends AbstractDaoImpl<LtlPricingApplicableCustomersEntity, Long>
        implements LtlPricingApplicableCustomersDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlPricingApplicableCustomersEntity> getApplicableCustomersByProfileId(Long profileId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("ltlPricingProfileId", profileId));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findApplicableCustomersBySMC3TariffName(String tariffName) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingApplicableCustomersEntity.Q_FIND_CUSTOMERS_BY_SMC3_TARIFF_NAME);
        query.setParameter("tariffName", tariffName);
        return query.list();
    }
}