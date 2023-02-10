package com.pls.core.domain.bo.user;

/**
 * BO with main information about user.
 * 
 * @author Aleksandr Leshchenko
 */
public class UserInfoBO {
    private Long personId;
    private String firstName;
    private String lastName;
    private Long parentOrgId;
    private String parentOrgName;
    private String parentOrgStatusReason;
    private Long assignedOrgId;
    private String assignedOrgName;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public String getParentOrgStatusReason() {
        return parentOrgStatusReason;
    }

    public void setParentOrgStatusReason(String parentOrgStatusReason) {
        this.parentOrgStatusReason = parentOrgStatusReason;
    }

    public Long getAssignedOrgId() {
        return assignedOrgId;
    }

    public void setAssignedOrgId(Long assignedOrgId) {
        this.assignedOrgId = assignedOrgId;
    }

    public String getAssignedOrgName() {
        return assignedOrgName;
    }

    public void setAssignedOrgName(String assignedOrgName) {
        this.assignedOrgName = assignedOrgName;
    }
}
