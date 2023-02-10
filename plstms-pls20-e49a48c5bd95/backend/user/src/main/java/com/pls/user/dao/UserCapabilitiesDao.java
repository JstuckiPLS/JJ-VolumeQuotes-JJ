package com.pls.user.dao;

import java.util.List;

import com.pls.user.domain.UserCapabilityEntity;

/**
 * UserCapabilitiesDao.
 * @author Maxim Medvedev
 *
 */
public interface UserCapabilitiesDao {
    /**
     * GEt groups.
     * 
     * @param personId presonId
     * @return list
     */
    List<UserCapabilityEntity> getCaps(Long personId);

    /**
     * save.
     * @param all all.
     */
    void saveAll(List<UserCapabilityEntity> all);
}
