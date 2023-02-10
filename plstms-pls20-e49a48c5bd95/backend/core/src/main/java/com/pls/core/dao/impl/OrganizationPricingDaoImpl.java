package com.pls.core.dao.impl;

import java.math.BigDecimal;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.shared.Status;

/**
 * DAO Implementation class for {@link OrganizationPricingEntity}.
 * 
 * @author Hima Bindu Challa
 * 
 */
@Transactional
@Repository
public class OrganizationPricingDaoImpl extends AbstractDaoImpl<OrganizationPricingEntity, Long> implements OrganizationPricingDao {

    @Override
    public OrganizationPricingEntity getActivePricing(Long shipperOrgId) {
        return (OrganizationPricingEntity) getCurrentSession().createCriteria(OrganizationPricingEntity.class)
                .add(Restrictions.eq("status", Status.ACTIVE))
                .add(Restrictions.eq("id", shipperOrgId))
                .uniqueResult();
    }

    @Override
    public BigDecimal getMinAcceptMargin(Long orgId) {

        return (BigDecimal) getCurrentSession().getNamedQuery(OrganizationPricingEntity.Q_GET_MIN_ACCEPT_MARGIN)
                .setParameter("orgId", orgId).uniqueResult();

    }
}
