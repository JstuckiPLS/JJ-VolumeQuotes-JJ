package com.pls.core.dao;

import com.pls.core.domain.organization.OrgServiceEntity;

/**
 * DAO class for {@link OrgServiceEntity}.
 * 
 * @author Pavani Challa
 * 
 */
public interface OrgServiceDao extends AbstractDao<OrgServiceEntity, Long> {

    /**
     * Get all services supported by an organization. The services support either EDI or API or none.
     * 
     * @param orgId
     *            organization for which the services are to be loaded.
     * 
     * @return the services supported by the organization.
     */
    OrgServiceEntity getServicesByOrgId(Long orgId);

    /**
     * Checks if the API is available for an organization for specific API type.
     * 
     * @param orgId
     *            organization to check for
     * @param category
     *            api type to check for
     * @return true if api is available
     */
    Boolean isApiAvailable(Long orgId, String category);
}
