package com.pls.user.dao;

import java.util.List;

import com.pls.user.domain.UserGroupEntity;

/**
 * Data Access Object for {@link UserGroupEntity} data.
 * 
 * @author Pavani Challa
 * 
 */
public interface UserGroupDao {
    /**
     * 
     * get groups.
     * @param personId persn id
     * @return list.
     */
    List<UserGroupEntity> getGroups(Long personId);

    /**
     * save.
     * @param all all.
     */
    void saveAll(List<UserGroupEntity> all);
}
