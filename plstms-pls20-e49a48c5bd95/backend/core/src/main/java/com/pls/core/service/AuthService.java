package com.pls.core.service;

import java.util.Set;

import com.pls.core.domain.bo.user.UserInfoBO;

/**
 * Service to obtain information about current user and perform some security related opeartions.
 * 
 * @author Maxim Medvedev
 */
public interface AuthService {
    /**
     * Load base information about current user.
     * 
     * @return Not <code>null</code> {@link UserInfoBO} if client was authtorized and this user exists in DB.
     *         Otherwise returns <code>null</code>.
     */
    UserInfoBO findCurrentUser();

    /**
     * Return capabilities for current user.
     * 
     * @return Not <code>null</code> {@link Set}.
     */
    Set<String> getCapabalitiesForCurrentUser();

    /**
     * Is current user is PLS emploee?
     * 
     * @return <code>true</code> for PLS users. <code>false</code> for customer and non authorized users.
     */
    boolean isCurrentPlsUser();

    /**
     * Reset authentication for current user.
     */
    void reSetUserAuthentication();

}
