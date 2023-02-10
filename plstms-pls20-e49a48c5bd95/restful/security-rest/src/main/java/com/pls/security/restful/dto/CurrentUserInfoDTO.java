package com.pls.security.restful.dto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.bo.user.UserInfoBO;

/**
 * Information about current user.
 * 
 * @author Maxim Medvedev
 */
public class CurrentUserInfoDTO {

    private final Long personId;
    private final boolean plsUser;

    private final String firstName;
    private final String fullName;

    private final CurrentOrganizationInfoDTO organization;
    private final CurrentOrganizationInfoDTO assignedOrganization;

    private Set<String> privilegies = new HashSet<String>();

    /**
     * Constructor.
     *
     * @param userInfo
     *            {@link UserInfoBO} for current user.
     * @param isPlsUser
     *            <code>true</code> if it is PLS employee.
     * @param capabilities
     *            Not <code>null</code> collection of user's capabilities.
     */
    public CurrentUserInfoDTO(UserInfoBO userInfo, boolean isPlsUser, Collection<String> capabilities) {
        if (userInfo != null) {
            organization = new CurrentOrganizationInfoDTO(userInfo.getParentOrgId(), userInfo.getParentOrgName(),
                    userInfo.getParentOrgStatusReason());
            plsUser = isPlsUser;
            personId = userInfo.getPersonId();
            fullName = UserNameBuilder.buildFullName(userInfo.getFirstName(), userInfo.getLastName());
            firstName = userInfo.getFirstName();
            privilegies.addAll(capabilities);
            if (userInfo.getAssignedOrgId() != null && userInfo.getAssignedOrgName() != null) {
                assignedOrganization = new CurrentOrganizationInfoDTO(userInfo.getAssignedOrgId(), userInfo.getAssignedOrgName(), null);
            } else {
                assignedOrganization = null;
            }
        } else {
            organization = null;
            plsUser = false;
            personId = null;
            fullName = null;
            firstName = null;
            assignedOrganization = null;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public CurrentOrganizationInfoDTO getOrganization() {
        return organization;
    }

    public CurrentOrganizationInfoDTO getAssignedOrganization() {
        return assignedOrganization;
    }

    public Set<String> getPrivilegies() {
        return privilegies;
    }
    public Long getPersonId() {
        return personId;
    }

    public boolean isPlsUser() {
        return plsUser;
    }
}
