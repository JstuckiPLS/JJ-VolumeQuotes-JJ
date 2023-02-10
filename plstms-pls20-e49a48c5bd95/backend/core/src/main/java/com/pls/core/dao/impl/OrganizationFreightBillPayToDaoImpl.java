package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationFreightBillPayToDao;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.OrganizationFreightBillPayToEntity;

/**
 * Implementation of {@link OrganizationFreightBillPayToDao}.
 * 
 * @author Artem Arapov
 *
 */
@Repository
@Transactional
public class OrganizationFreightBillPayToDaoImpl extends AbstractDaoImpl<OrganizationFreightBillPayToEntity, Long>
                                                    implements OrganizationFreightBillPayToDao {

    @Override
    public FreightBillPayToEntity getActiveFreightBillPayToByOrgId(Long orgId) {
        return (FreightBillPayToEntity) getCurrentSession().getNamedQuery(OrganizationFreightBillPayToEntity.Q_GET_ACTIVE_BY_ORG_ID)
                    .setLong("orgId", orgId).uniqueResult();
    }

    @Override
    public void inactivateExistingByOrgId(Long orgId) {
        getCurrentSession().getNamedQuery(OrganizationFreightBillPayToEntity.Q_INACTIVATE_EXISTING_BY_ORG_ID)
                    .setLong("orgId", orgId)
                    .executeUpdate();
    }

}
