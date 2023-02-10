package com.pls.user.shared;

import java.io.Serializable;

/**
 * Value Object to return the user details for whom the capability/group is assigned.
 * 
 * @author Pavani Challa
 * 
 */
public class UserCapabilitiesResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long personId;

    private String name;

    private String userid;

    private String directPermission;

    private String roles;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDirectPermission() {
        return directPermission;
    }

    public void setDirectPermission(String directPermission) {
        this.directPermission = directPermission;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

}
