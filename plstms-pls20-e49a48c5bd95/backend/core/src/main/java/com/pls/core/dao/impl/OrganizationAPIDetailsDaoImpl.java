package com.pls.core.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationAPIDetailsDao;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;

/**
 * {@link OrganizationAPIDetailsDao} implementation.
 *
 * @author Sergey Kirichenko
 * @author Hima Bindu Challa
 */
@Transactional
@Repository
public class OrganizationAPIDetailsDaoImpl extends AbstractDaoImpl<OrganizationAPIDetailsEntity, Long>
    implements OrganizationAPIDetailsDao {

    @Override
    public OrganizationAPIDetailsEntity getCarrierAPIDetailsByOrgId(Long orgId) {
        return (OrganizationAPIDetailsEntity) this.getCurrentSession().createCriteria(
                OrganizationAPIDetailsEntity.class).add(Restrictions.eq("organizationEntity.id", orgId)).uniqueResult();
    }
}
