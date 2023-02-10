package com.pls.user.restful;

import java.util.List;

import com.pls.core.service.AuthService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.user.domain.CapabilityEntity;
import com.pls.user.service.CapabilityService;
import com.pls.user.shared.UnassignUserPrivilegesCO;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * RESTful resource for Capabilities.
 * 
 * @author Pavani Challa
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/capabilities")
public class CapabilityResource {

    @Autowired
    private CapabilityService service;

    @Autowired
    private AuthService authService;

    /**
     * Get all the capabilities that are assigned to the current user either directly or through groups. If
     * the current user is 'ROOT', then all the capabilities are returned.
     * 
     * @return All capabilities that are assigned to current user
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<CapabilityEntity> getAllCapabilities() {
        return service.getAllCapabilities(null);
    }

    /**
     * Get all the capabilities that are assigned to the current user either directly or through groups
     * excluding the capabilities that are associated with the group. If the current user is 'ROOT', then all
     * the capabilities excluding the group capabilities are returned.
     * 
     * @param groupId
     *            group to be excluded
     * @return All capabilities that are assigned to current user excluding those of group
     */
    @RequestMapping(value = "/all/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<CapabilityEntity> getAllCapabilities(@PathVariable("id") Long groupId) {
        return service.getAllCapabilities(groupId);
    }

    /**
     * Get the details of the Capability.
     * 
     * @param id
     *            capability for whom the details are to be loaded
     * @return the details of a capability
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CapabilityEntity getCapabilityById(@PathVariable("id") Long id) {
        return service.getCapabilityById(id);
    }

    /**
     * Get the list of all users for whom the capability is assigned either directly or through a group/role.
     * 
     * @param id
     *            capability for whom the users have to be returned.
     * @return List of all users having the capability
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<UserCapabilitiesResultVO> getUsersWithCapability(@PathVariable("id") Long id) {
        Long orgId = null;
        if (!SecurityUtils.isPlsUser()) {
            orgId = SecurityUtils.getParentOrganizationId();
        }
        return service.getUsersWithCapability(id, orgId);
    }

    /**
     * Removes the capability from the users that was assigned directly. If the user has the capability
     * assigned through the group, then the user will still have the capability access. This is a logical
     * delete and the status is set to Inactive.
     * 
     * @param criteria
     *            criteria object containing the capability and the users list for whom the capability has to
     *            be removed
     */
    @RequestMapping(value = "/unassign/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void unassignCapability(@RequestBody UnassignUserPrivilegesCO criteria) {
        service.unassignCapability(criteria.getId(), criteria.getUsers());
        authService.reSetUserAuthentication();
    }
}
