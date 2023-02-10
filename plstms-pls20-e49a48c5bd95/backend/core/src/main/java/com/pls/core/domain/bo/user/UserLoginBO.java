package com.pls.core.domain.bo.user;

import com.pls.core.domain.enums.UserStatus;

/**
 * BO with information about user required to log in to the system.
 * 
 * @author Aleksandr Leshchenko
 */
public class UserLoginBO {
    private Long personId;
    private Long parentOrgId;
    private String userId;
    private UserStatus status;
    private String password;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
