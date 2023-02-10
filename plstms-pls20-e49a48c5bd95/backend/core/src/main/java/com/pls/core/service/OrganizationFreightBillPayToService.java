package com.pls.core.service;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.OrganizationFreightBillPayToEntity;
import com.pls.core.domain.organization.CustomerEntity;

/**
 * Service logic for {@link OrganizationFreightBillPayToEntity}.
 * 
 * @author Artem Arapov
 *
 */
public interface OrganizationFreightBillPayToService {

    /**
     * Returns active {@link FreightBillPayToEntity} for specified Customer.
     * 
     * @param orgId - Identifier of Customer. {@link CustomerEntity#getId()}.
     * @return existing {@link FreightBillPayToEntity} for specified Customer, otherwise returns <code>null</code>.
     */
    FreightBillPayToEntity getActiveFreightBillPayToByOrgId(Long orgId);

    /**
     * Returns {@link OrganizationFreightBillPayToEntity} by specified identifier.
     * 
     * @param id - Not <code>null</code> {@link OrganizationFreightBillPayToEntity#getId()}.
     * @return Existing entity if it found, otherwise returns <code>null</code>
     */
    OrganizationFreightBillPayToEntity getById(Long id);

    /**
     * Inactivate all existing {@link OrganizationFreightBillPayToEntity} related to specified Customer and save specified entity.
     * 
     * @param entity - Not <code>null</code> entity.
     */
    void save(OrganizationFreightBillPayToEntity entity);
}
