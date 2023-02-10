package com.pls.user.restful;

import java.util.List;

import com.pls.core.service.AuthService;
import com.pls.core.service.impl.security.util.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.shared.ResponseVO;
import com.pls.user.domain.GroupEntity;
import com.pls.user.service.GroupService;
import com.pls.user.shared.UnassignUserPrivilegesCO;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * RESTful resource for Groups.
 * 
 * @author Pavani Challa
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/groups")
public class GroupResource {

    @Autowired
    private GroupService service;

    @Autowired
    private AuthService authService;

    /**
     * Deletes the group. This is logical delete only. The status is set to INACTIVE.
     * 
     * @param id
     *            group to be deleted
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteGroup(@PathVariable("id") Long id) {
        service.deleteGroup(id);
        authService.reSetUserAuthentication();
    }

    /**
     * Get all the groups that are assigned to the current user. if the current user is 'ROOT' then all groups
     * are returned.
     * 
     * @return the groups assigned to current user
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<GroupEntity> getAllGroups() {
        Long personId = SecurityUtils.getCurrentPersonId();
        return service.getAllGroups(personId);
    }

    /**
     * Get the group details. Includes the capabilities assigned to the group.
     * 
     * @param id
     *            group to be loaded
     * @return the group details
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public GroupEntity getGroupById(@PathVariable("id") Long id) {
        return service.getGroupById(id);
    }

    /**
     * Get the list of all users for whom the group is assigned.
     * 
     * @param id
     *            group for whom the users have to be returned.
     * @return List of all users having the group
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<UserCapabilitiesResultVO> getUsersWithGroup(@PathVariable("id") Long id) {
        Long orgId = null;
        if (!SecurityUtils.isPlsUser()) {
            orgId = SecurityUtils.getParentOrganizationId();
        }
        return service.getUsersWithGroup(id, orgId);
    }

    /**
     * Checks if there is any other active groups with the same name.
     * 
     * @param name
     *            name be checked against the existing groups
     * @param excludeGroup
     *            group to be excluded
     * @return true if the name is unique else false
     */
    @RequestMapping(value = "/isUnique/{name}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO isNameUnique(@PathVariable("name") String name,
            @RequestParam("excludeGroup") Long excludeGroup) {
        return new ResponseVO(service.isNameUnique(name, excludeGroup).booleanValue());
    }

    /**
     * Saves the group. If it doesn't exist, a new group is created
     * 
     * @param group
     *            group to be saved
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveGroup(@RequestBody GroupEntity group) {
        service.saveGroup(group);
        authService.reSetUserAuthentication();
    }

    /**
     * Removes the group from the users. This is a logical delete and the status is set to Inactive.
     * 
     * @param criteria
     *            criteria object containing the group and the users list for whom the group has to be removed
     */
    @RequestMapping(value = "/unassign/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void unassignGroup(@RequestBody UnassignUserPrivilegesCO criteria) {
        service.unassignGroup(criteria.getId(), criteria.getUsers());
        authService.reSetUserAuthentication();
    }
}
