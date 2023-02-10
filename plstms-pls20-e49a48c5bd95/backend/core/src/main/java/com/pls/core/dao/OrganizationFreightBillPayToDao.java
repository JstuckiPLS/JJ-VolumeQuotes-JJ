package com.pls.core.dao;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.OrganizationFreightBillPayToEntity;
import com.pls.core.domain.organization.CustomerEntity;


/**
 * Data Access Object for {@link OrganizationFreightBillPayToEntity}.
 * 
 * @author Artem Arapov
 *
 */
public interface OrganizationFreightBillPayToDao extends AbstractDao<OrganizationFreightBillPayToEntity, Long> {

    /**
     * Returns active {@link FreightBillPayToEntity} for specified Customer.
     * 
     * @param orgId - Identifier of Customer. {@link CustomerEntity#getId()}.
     * @return existing {@link FreightBillPayToEntity} for specified Customer, otherwise returns <code>null</code>.
     */
    FreightBillPayToEntity getActiveFreightBillPayToByOrgId(Long orgId);

    /**
     * Inactivate existing {@link OrganizationFreightBillPayToEntity} by specified Customer.
     * 
     * @param orgId - Identifier of Customer. {@link CustomerEntity#getId()}.
     */
    void inactivateExistingByOrgId(Long orgId);
}
