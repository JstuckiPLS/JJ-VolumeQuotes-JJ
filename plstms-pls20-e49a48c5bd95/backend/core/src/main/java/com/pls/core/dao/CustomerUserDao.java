package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.user.CustomerUserEntity;

/**
 * DAO for {@link CustomerUserEntity} entity.
 * 
 * @author Denis Zhupinsky (Team International)
 */
public interface CustomerUserDao extends AbstractDao<CustomerUserEntity, Long> {
    /**
     * Get active {@link CustomerUserEntity} for specified customer.
     * 
     * @param customerId
     *            - id of organization to filter by
     * @return {@link List} of {@link CustomerUserEntity} filtered by organization id
     */
    List<CustomerUserEntity> getActive(Long customerId);

    /**
     * Get list of {@link CustomerUserEntity} filtered by customer name.
     *
     * @param userId id of user
     * @param customerName   customer name filter
     * @return list of organization users filtered by customer name
     */
    List<CustomerUserEntity> getByName(Long userId, String customerName);

    /**
     * Returns {@link CustomerUserEntity} for specified user and customer.
     * 
     * @param personId - id of user
     * @param orgId - id of customer
     * @return {@link CustomerUserEntity} if it was found. Otherwise returns <code>null</code>
     */
    CustomerUserEntity findByPersonIdOrgIdLocationId(Long personId, Long orgId);
}
