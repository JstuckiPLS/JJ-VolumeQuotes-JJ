package com.pls.user.service;

import java.util.List;

import com.pls.user.domain.GroupEntity;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Service that handle business logic and transactions for Groups.
 * 
 * @author Pavani Challa
 */
public interface GroupService {

    /**
     * Get the group details. Includes the capabilities assigned to the group.
     * 
     * @param groupId
     *            group to be loaded
     * @return the group details
     */
    GroupEntity getGroupById(Long groupId);

    /**
     * Get all the groups that are assigned to the current user. if the current user is 'ROOT' then all groups
     * are returned.
     * 
     * @param personId id of the person
     * 
     * @return the groups assigned to current user
     */
    List<GroupEntity> getAllGroups(Long personId);

    /**
     * Deletes the group. This is logical delete only. The status is set to INACTIVE.
     * 
     * @param groupId
     *            group to be deleted
     */
    void deleteGroup(Long groupId);

    /**
     * Removes the group from the users. This is a logical delete and the status is set to Inactive.
     * 
     * @param groupId
     *            Group to be removed from the users
     * @param users
     *            Users for whom the group has to be removed
     */
    void unassignGroup(Long groupId, List<Long> users);

    /**
     * Get the list of all users for whom the group is assigned.
     * 
     * @param groupId
     *            group for whom the users have to be returned.
     * @param organizationId
     *            id of organization user belongs. If null show from all organizations
     * @return List of all users having the group
     */
    List<UserCapabilitiesResultVO> getUsersWithGroup(Long groupId, Long organizationId);

    /**
     * Saves the group. If one doesn't exist, a new group is created and the current user is assigned to that group.
     * 
     * @param group
     *            group to be saved
     */
    void saveGroup(GroupEntity group);

    /**
     * Checks if there is any other active groups with the same name.
     * 
     * @param name
     *            name be checked against the existing groups
     * @param excludeGroup
     *            group to be excluded
     * @return true if the name is unique else false
     */
    Boolean isNameUnique(String name, Long excludeGroup);
}
