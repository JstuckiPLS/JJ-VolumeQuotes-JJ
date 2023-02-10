package com.pls.core.dao;

import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;

/**
 * DAO for {@link OrganizationAPIDetailsEntity}.
 *
 * @author Sergey Kirichenko
 */
public interface OrganizationAPIDetailsDao extends AbstractDao<OrganizationAPIDetailsEntity, Long> {

    /**
     * Get Carrier API Details for the selected carrier.
     * 
     * @param orgId - selected carrier
     * @return Carrier API Details - OrganizationAPIDetailsEntity
     */
    OrganizationAPIDetailsEntity getCarrierAPIDetailsByOrgId(Long orgId);
}
