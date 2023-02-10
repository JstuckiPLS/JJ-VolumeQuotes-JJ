package com.pls.core.dao;

import java.util.Set;

/**
 * DAO to obtain information about user privileges.
 * 
 * @author Maxim Medvedev
 */
public interface SecurityDao {

    /**
     * Is it PLS user/employer?
     * 
     * @param parentOrgId
     *            Value of {@link com.pls.core.domain.user.UserEntity#getParentOrgId()}.
     * @return <code>true</code> if this is PLS User. Otherwise returns <code>false</code>.
     */
    boolean isPlsUser(Long parentOrgId);

    /**
     * Find all capabilities for specified user. This method loads both directly assigned and assigned by
     * groups capabilities.
     * 
     * @param personId
     *            Not <code>null</code> PERSON_ID value.
     * 
     * @return Not <code>null</code> {@link Set} with capability names.
     */
    Set<String> loadCapabilities(Long personId);

    /**
     * Find all organizations directly assigned for specified user.<br>
     * For PLS User all assigned organizations are returned.<br>
     * For Customer User Inactive organizations are not returned.
     * 
     * @param personId
     *            Not <code>null</code> PERSON_ID value.
     * @return Not <code>null</code> {@link Set} with organization ID values.
     */
    Set<Long> loadOrganizations(Long personId);

    /**
     * Check if customer is assigned to specified user via network.
     * 
     * @param personId
     *            Not <code>null</code> PERSON_ID value.
     * @param customerId
     *            Not <code>null</code> ORG_ID value.
     * @return <code>true</code> if customer is assigned to specified user via network. <code>false</code>
     *         otherwise.
     */
    boolean isCustomerAssignedThroughNetwork(Long personId, Long customerId);

    /**
     * Save Last Login Date by specified <code>personId</code>.
     * 
     * @param currentPersonId
     *            User ID to be updated.
     */
    void saveLastLoginDateByPersonId(Long currentPersonId);
}
