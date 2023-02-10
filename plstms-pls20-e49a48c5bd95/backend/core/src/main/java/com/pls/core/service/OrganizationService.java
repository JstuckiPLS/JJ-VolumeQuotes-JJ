
/**
 * 
 */
package com.pls.core.service;

import java.util.List;

import com.pls.core.domain.organization.CompanyCodeEntity;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.validation.ValidationException;

/**
 * Business logic for Organization.
 * 
 * @author Alexander Nalapko
 * 
 */
public interface OrganizationService {
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
    List<SimpleOrganizationEntity> getOrganizationsByNameAndType(String orgType, String name, Integer limit, Integer offset);

    /**
     * Update logo for organization by id.
     * 
     * @param orgId
     *            organization id
     * @param docId id of saved logo
     * @exception EntityNotFoundException
     *                if organization with <id> not found
     */
    void updateLogoForOrganization(Long orgId, Long docId) throws EntityNotFoundException;

    /**
     * Save carrier API details.
     * 
     * @param details
     *            carrier details
     * @throws ValidationException
     *             if validation checks fail.
     */
    void saveCarrierAPIDetails(OrganizationAPIDetailsEntity details) throws ValidationException;

    /**
     * Get Carrier API Details for the selected carrier.
     * 
     * @param orgId
     *            - selected carrier
     * @return Carrier API Details - OrganizationAPIDetailsEntity
     */
    OrganizationAPIDetailsEntity getCarrierAPIDetailsByOrgId(Long orgId);

    /**
     * Gets simple organization information by ID.
     * 
     * @param id
     *            organization ID.
     * @return {@link SimpleOrganizationEntity}
     */
    SimpleOrganizationEntity getSimpleOrganizationById(Long id);

    /**
     * Get logo id for specified organization.
     * 
     * @param orgId
     *            organization id
     * @return retrieved image entity
     */
    Long getImageByOrganizationId(Long orgId);

    /**
     * Get all services supported by an organization.
     * 
     * @param orgId
     *            organization for which the services are to be loaded.
     * 
     * @return the services supported by the organization.
     */
    OrgServiceEntity getServicesByOrgId(Long orgId);

    /**
     * Save the details for services supported by the organization.
     * 
     * @param orgServices
     *            the services supported by the organization.
     * @return the saved entity
     */
    OrgServiceEntity saveOrgServices(OrgServiceEntity orgServices);

    /**
     * Get all company codes.
     * 
     * @return the company codes
     */
    List<CompanyCodeEntity> getCompanyCodes();

    /**
     * Get organization name by org Id.
     * 
     * @param orgId - organization Id.
     * 
     * @return organization name;
     */
    String getOrganizationNameByOrgId(Long orgId);

    /**
     * Unsubscribe from receiving missing paperwork emails.
     * 
     * @param orgId - id of Carrier organization.
     * @return <code>true</code> if update success.
     */
    boolean unsubscribeFromPaperworkEmails(Long orgId);
}
