package com.pls.user.service;

import java.util.List;

import com.pls.user.domain.CapabilityEntity;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Service that handle business logic and transactions for Capabilities.
 * 
 * @author Pavani Challa
 */
public interface CapabilityService {

    /**
     * Get the details of the Capability.
     * 
     * @param capabilityId
     *            capability for whom the details are to be loaded
     * @return the details of a capability
     */
    CapabilityEntity getCapabilityById(Long capabilityId);

    /**
     * Get all the capabilities that are assigned to the current user either directly or through groups. If
     * the current user is 'ROOT', then all the capabilities are returned. If the excludeGroup parameter is not
     * null, then the capabilities associated with that group are excluded.
     * 
     * @param excludeGroup
     *            Group to be excluded.
     * @return All capabilities that are assigned to current user
     */
    List<CapabilityEntity> getAllCapabilities(Long excludeGroup);

    /**
     * Removes the capability from the users that was assigned directly. If the user has the capability
     * assigned through the group, then the user will still have the capability access. This is a logical
     * delete and the status is set to Inactive.
     * 
     * @param capabilityId
     *            Capability to be removed from the users
     * @param users
     *            Users for whom the capability has to be removed
     */
    void unassignCapability(Long capabilityId, List<Long> users);

    /**
     * Get the list of all users for whom the capability is assigned either directly or through a group/role.
     * 
     * @param capabilityId
     *            capability for whom the users have to be returned.
     * @param organizationId
     *            id of organization user belongs. If null show from all organizations
     * @return List of all users having the capability
     */
    List<UserCapabilitiesResultVO> getUsersWithCapability(Long capabilityId, Long organizationId);
}
