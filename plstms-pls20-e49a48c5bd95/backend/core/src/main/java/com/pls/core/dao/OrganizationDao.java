package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.exception.EntityNotFoundException;

/**
 * DAO for {@link OrganizationEntity}.
 * 
 * @author Alexander Nalapko
 * 
 */

public interface OrganizationDao extends AbstractDao<OrganizationEntity, Long> {

    /**
     * Save carrier API details.
     * 
     * @param details
     *            carrier details
     */
    void saveCarrierAPIDetails(OrganizationAPIDetailsEntity details);

    /**
     * Update logo for organization by id.
     * 
     * @param orgId
     *            organization id
     * @param logoId
     *            id of stored organization logo
     * @param user
     *            current user
     * @exception EntityNotFoundException
     *                if organization with <id> not found
     */
    void updateLogoForOrganization(Long orgId, Long logoId, Long user) throws EntityNotFoundException;

    /**
     * Get organization with ORG_TYPE==EFLATBED by name.
     * 
     * @param name
     *            Name pattern.
     * @param limit
     *            Max number of result entities.
     * 
     * @return not null list of {@link OrganizationEntity}.
     */
    List<ParentOrganizationBO> getRootOrganizationByName(String name, int limit);

    /**
     * Unsubscribe from receiving missing paperwork emails.
     * 
     * @param orgId - id of Carrier organization.
     * @return <code>true</code> if update success.
     */
    boolean unsubscribeFromPaperworkEmails(Long orgId);
}
