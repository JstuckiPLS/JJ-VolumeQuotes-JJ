package com.pls.core.service;

import org.springframework.security.access.AccessDeniedException;

import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.user.UserEntity;

/**
 * Service interface for validation of user capabilities/permissions.
 * 
 * @author Aleksandr Leshchenko
 */
public interface UserPermissionsService {

    /**
     * Checks that current user is permitted to work with specified organization.
     * 
     * @param organizationId
     *            Normally not <code>null</code> {@link OrganizationEntity#getId()}.
     * @return <code>true</code> if current user is permitted to interact with specified organization.
     *         Otherwise returns <code>false</code>.
     */
    boolean canHandle(Long organizationId);

    /**
     * Checks that current user has at least one of specified capabilities.
     * 
     * @param capabilities
     *            Not <code>null</code> {@link String}s.
     * @throws AccessDeniedException
     *             Current user does not have at least one capability.
     */
    void checkCapability(String... capabilities) throws AccessDeniedException;

    /**
     * Checks that current user has at least one of specified capabilities and may interact with specified
     * organization. This is combination of {@link #checkCapability(String...)} and
     * {@link #checkOrganization(Long)}.
     * 
     * @param organizationId
     *            Normally not <code>null</code> {@link OrganizationEntity#getId()}.
     * @param capabilities
     *            Not <code>null</code> {@link String}s.
     * @throws AccessDeniedException
     *             Current user does not have at least one capability or current user is not permitted to work
     *             with specified organization.
     */
    void checkCapabilityAndOrganization(Long organizationId, String... capabilities)
            throws AccessDeniedException;

    /**
     * Checks that current user is permitted to work with specified organization.
     * 
     * @param organizationId
     *            Normally not <code>null</code> ID.
     * @throws AccessDeniedException
     *             If current user is not permitted to work with specified organization.
     */
    void checkOrganization(Long organizationId) throws AccessDeniedException;

    /**
     * Checks that the user has at least one of specified capabilities.
     * 
     * @param personId
     *            Not <code>null</code> {@link UserEntity#getPersonId()} value.
     * @param capabilities
     *            Not <code>null</code> {@link String}s.
     * @return <code>true</code> if the user has at least one capability. Otherwise returns <code>false</code>
     *         .
     */
    boolean hasCapability(Long personId, String... capabilities);

    /**
     * Checks that current user has at least one of specified capabilities.
     * 
     * @param capabilities
     *            Not <code>null</code> {@link String}s.
     * @return <code>true</code> if current user has at least one capability. Otherwise returns
     *         <code>false</code>.
     */
    boolean hasCapability(String... capabilities);

    /**
     * Checks that current user has at least one of specified capabilities and may interact with specified
     * organization. This is combination of {@link #hasCapability(Long, String...)} and {@link
     * #canHandle((Long, Long)}.
     * 
     * @param personId
     *            Not <code>null</code> {@link UserEntity#getPersonId()} value.
     * @param organizationId
     *            Normally not <code>null</code> {@link OrganizationEntity#getId()}.
     * @param capabilities
     *            Not <code>null</code> {@link String}s.
     * @return <code>true</code> if user has at least one of specified capabilities and is permitted to
     *         Interact with specified organization. Otherwise returns <code>false</code>.
     */
    boolean hasCapabilityAndOrganization(Long personId, Long organizationId, String... capabilities);

    /**
     * Checks that the user has at least one of specified capabilities and may interact with specified
     * organization. This is combination of {@link #hasCapability(String...)} and {@link #canHandle(Long)}.
     * 
     * @param organizationId
     *            Normally not <code>null</code> {@link OrganizationEntity#getId()}.
     * @param capabilities
     *            Not <code>null</code> {@link String}s.
     * @return <code>true</code> if user has at least one of specified capabilities and is permitted to
     *         interact with specified organization. Otherwise returns <code>false</code>.
     */
    boolean hasCapabilityAndOrganization(Long organizationId, String... capabilities);
}
