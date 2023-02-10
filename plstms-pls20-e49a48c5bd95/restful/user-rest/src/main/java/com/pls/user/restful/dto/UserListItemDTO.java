package com.pls.user.restful.dto;

/**
 * Base users' attributes to display list of active and inactive users.
 * 
 * @author Maxim Medvedev
 */
public class UserListItemDTO {

    private String email;
    private String fullName;
    private Long parentOrgId;
    private String parentOrgName;

    private Long personId;

    private String userId;

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public String getParentOrgName() {
        return parentOrgName;
    }

    public Long getPersonId() {
        return personId;
    }

    public String getUserId() {
        return userId;
    }

    public void setEmail(String pEmail) {
        email = pEmail;
    }

    public void setFullName(String pFullName) {
        fullName = pFullName;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public void setPersonId(Long pPersonId) {
        personId = pPersonId;
    }

    public void setUserId(String pUserId) {
        userId = pUserId;
    }

}
