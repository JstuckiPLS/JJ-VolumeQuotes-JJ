package com.pls.core.service.impl.security.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import com.pls.core.service.impl.security.PlsUserDetails;

/**
 * Helper class for working with security. Allows to get currently logged in user.
 * 
 * @author Viacheslav Krot
 * 
 */
public final class SecurityUtils {

    private static final Authentication NULL_AUTHENTICATION = new Authentication() {

        private static final long serialVersionUID = 7652942951238617109L;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return SecurityUtils.NULL_AUTHORITIES;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public String getName() {
            return StringUtils.EMPTY;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public void setAuthenticated(boolean pIsAuthenticated) throws IllegalArgumentException {
            // Do nothing
        }
    };

    private static final Collection<GrantedAuthority> NULL_AUTHORITIES = Collections
            .unmodifiableCollection(new ArrayList<GrantedAuthority>());

    /**
     * Get all capabilities for current user.
     * 
     * @return Not <code>null</code> {@link Set}.
     */
    public static Set<String> getCapabilities() {
        Set<String> result = Collections.emptySet();
        Authentication authentication = SecurityUtils.getAuthentication();
        Object details = authentication.getPrincipal();
        if (details instanceof PlsUserDetails) {
            result = ((PlsUserDetails) details).getCapabilities();
        }
        return result == null ? Collections.emptySet() : result;
    }

    /**
     * Get id of user that is currently logged in. Null if user is not authenticated.
     * 
     * @return id if user that is currently logged in or null if user is not authenticated.
     */
    public static Long getCurrentPersonId() {
        Long result = null;
        Authentication authentication = SecurityUtils.getAuthentication();
        Object details = authentication.getPrincipal();
        if (details instanceof PlsUserDetails) {
            result = ((PlsUserDetails) details).getPersonId();
        }
        return result;
    }

    /**
     * Get login of user that is currently logged in. Null if user is not authenticated.
     * 
     * @return login if user that is currently logged in or null if user is not authenticated.
     */
    public static String getCurrentUserLogin() {
        String result = null;
        Authentication authentication = SecurityUtils.getAuthentication();
        Object details = authentication.getPrincipal();
        if (details instanceof PlsUserDetails) {
            result = ((PlsUserDetails) details).getUsername();
        }
        return result;
    }

    /**
     * Get organization which can be managed by this user.
     * 
     * @return Not <code>null</code> {@link Set}.
     */
    public static Set<Long> getOrganizations() {
        Set<Long> result = Collections.emptySet();
        Authentication authentication = SecurityUtils.getAuthentication();
        Object details = authentication.getPrincipal();
        if (details instanceof PlsUserDetails) {
            result = ((PlsUserDetails) details).getOrganizations();
        }
        return result;
    }

    /**
     * Get organization ID to which user belongs.
     * 
     * @return organization id ID to which user belongs or <code>null</code> if user is not authenticated.
     */
    public static Long getParentOrganizationId() {
        Authentication authentication = SecurityUtils.getAuthentication();
        Object details = authentication.getPrincipal();
        if (details instanceof PlsUserDetails) {
            return ((PlsUserDetails) details).getParentOrgId();
        } else {
            return null;
        }
    }

    /**
     * Is current user a PLS employee?
     * 
     * @return <code>true</code> if current user is PLS employee. <code>false</code> is current user is
     *         customer user or client was not logged.
     */
    public static boolean isPlsUser() {
        boolean result = false;
        Authentication authentication = SecurityUtils.getAuthentication();
        Object details = authentication.getPrincipal();
        if (details instanceof PlsUserDetails) {
            result = ((PlsUserDetails) details).isPlsUser();
        }
        return result;
    }

    /**
     * Set up new authentication into spring security context.
     *
     * @param plsUserDetails user details
     */
    public static void setupNewAuthentication(PlsUserDetails plsUserDetails) {
        Authentication authentication = getAuthentication();
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String capability : plsUserDetails.getCapabilities()) {
            authorities.add(new SimpleGrantedAuthority(capability));
        }
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(plsUserDetails, null, authorities);
        if (authentication != null) {
            newAuthentication.setDetails(authentication.getDetails());
        }
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    /**
     * Expire sessions of user with specified id.
     *
     * @param personId user id
     * @param sessionRegistry {@link SessionRegistry}
     */
    public static void expireSessions(Long personId, SessionRegistry sessionRegistry) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof PlsUserDetails && ((PlsUserDetails) principal).getPersonId().equals(personId)) {
                for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                    session.expireNow();
                }

                return;

            }
        }
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            authentication = SecurityUtils.NULL_AUTHENTICATION;
        }
        return authentication;
    }

    /**
     * Prevent instantiation.
     */
    private SecurityUtils() {
    }
}