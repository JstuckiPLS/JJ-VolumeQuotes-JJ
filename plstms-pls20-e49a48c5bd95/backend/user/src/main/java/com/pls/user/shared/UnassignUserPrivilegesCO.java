package com.pls.user.shared;

import java.io.Serializable;
import java.util.List;

/**
 * Criteria object to hold the users list for whom the capability/group should be removed.
 * 
 * @author Pavani Challa
 * 
 */
public class UnassignUserPrivilegesCO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private List<Long> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }
}
