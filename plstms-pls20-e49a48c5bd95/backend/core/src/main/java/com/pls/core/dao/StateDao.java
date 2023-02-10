package com.pls.core.dao;

import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;

/**
 * DAO for {@link StateEntity}.
 * 
 * @author Artem Arapov
 */
public interface StateDao {
    /**
     * Get {@link StateEntity} by their ID.
     * 
     * @param statePK Not <code>null</code> ID.
     * @return {@link StateEntity}.
     */
    StateEntity getState(StatePK statePK);

    /**
     * Get {@link StateEntity} by state and country codes.
     * 
     * @param stateCode Not <code>null</code> {@link String}.
     * @param countryCode Not <code>null</code> {@link String}.
     * @return  {@link StateEntity}.
     */
    StateEntity getState(String stateCode, String countryCode);
}
