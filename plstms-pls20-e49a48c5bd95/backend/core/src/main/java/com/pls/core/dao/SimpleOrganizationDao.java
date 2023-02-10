package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.organization.SimpleOrganizationEntity;

/**
 * The dao to get minimal Organization table information.
 * 
 * @author Hima Bindu Challa
 *
 */
public interface SimpleOrganizationDao extends AbstractDao<SimpleOrganizationEntity, Long> {
    /**
     * Find Customers by orgType and name for Customer Search Filters.
     * 
     * @param orgType
     *            organization type
     * @param name
     *            customer name
     * @param limit
     *            page size
     * @param offset
     *            pages
     * @return list of organizations
     */
    List<SimpleOrganizationEntity> getOrganizationsByNameAndType(String orgType,
            String name, Integer limit, Integer offset);
}
