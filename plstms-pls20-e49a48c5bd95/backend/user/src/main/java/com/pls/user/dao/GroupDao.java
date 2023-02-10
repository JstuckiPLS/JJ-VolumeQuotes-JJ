package com.pls.user.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.user.domain.GroupEntity;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Data Access Object for {@link GroupEntity} data.
 * 
 * @author Pavani Challa
 * 
 */
public interface GroupDao extends AbstractDao<GroupEntity, Long> {

    /**
     * Get all the groups that are assigned to the current user. if the current user is 'ROOT' then all groups
     * are returned.
     * 
     * @param personId id of the person
     * 
     * @return the groups assigned to current user
     */
    List<GroupEntity> findAllGroups(Long personId);

    /**
     * Get the group details. Includes the capabilities assigned to the group.
     * 
     * @param groupId
     *            group to be loaded
     * @return the group details
     */
    GroupEntity findGroup(Long groupId);

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
    List<UserCapabilitiesResultVO> findUsersWithGroup(Long groupId, Long organizationId);

    /**
     * Deletes the group. This is logical delete only. The status is set to INACTIVE.
     * 
     * @param groupId
     *            group to be deleted
     */
    void delete(Long groupId);

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
