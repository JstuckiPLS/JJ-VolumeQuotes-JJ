package com.pls.core.service.impl.security.util;

import java.util.Collections;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.pls.core.service.impl.security.PlsUserDetails;

/**
 * Helper class for managing security context. <br />
 * <b>WARNING</b> It should be used only in tests! Normally this class should be removed from production code
 * but in our case we need it here to avoid cyclic dependencies in maven modules ('test-utils' requires 'core'
 * and 'core' requires 'test-utils').
 * 
 * @author Viacheslav Krot
 */
public final class SecurityTestUtils {

    public static final Long DEFAULT_ORGANIZATION_ID = 1L;
    public static final Long DEFAULT_PERSON_ID = 1L;

    /**
     * Populate security context with provided user name and roles.
     * 
     * @param username
     *            user name.
     * @param personId
     *            user's personId value.
     * @param orgId
     *            Organization ID value
     * @param isPlsUser
     *            Is PLS User.
     * @param capabilities
     *            Granted capabilities/permissions
     */
    public static void login(String username, Long personId, Long orgId, boolean isPlsUser, String... capabilities) {
        PlsUserDetails details = new PlsUserDetailsBuilder(username).withPassword(username)
                .withPersonId(personId).withParentOrgId(orgId).withEnabled(true).withIsPlsUser(true)
                .withCapabilities(capabilities).withOrganizations(Collections.singleton(orgId)).build();

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(details, null));
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
    }

    /**
     * Populate security context with provided user name and roles.
     * 
     * @param username
     *            user name.
     * @param personId
     *            user's personId value.
     * @param orgId
     *            Organization ID value
     * @param capabilities
     *            Granted capabilities/permissions
     */
    public static void login(String username, Long personId, Long orgId, String... capabilities) {
        PlsUserDetails details = new PlsUserDetailsBuilder(username).withPassword(username)
                .withPersonId(personId).withParentOrgId(orgId).withEnabled(true)
                .withCapabilities(capabilities).withOrganizations(Collections.singleton(orgId)).build();

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(details, null));
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
    }

    /**
     * Populate security context with provided user name and roles.
     * 
     * @param username
     *            user name.
     * @param personId
     *            user's personId value.
     * @param capabilities
     *            Granted capabilities/permissions
     */
    public static void login(String username, Long personId, String... capabilities) {
        PlsUserDetails details = new PlsUserDetailsBuilder(username).withPassword(username)
                .withPersonId(personId).withParentOrgId(SecurityTestUtils.DEFAULT_ORGANIZATION_ID)
                .withEnabled(true).withCapabilities(capabilities).build();

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(details, null));
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
    }

    /**
     * Populate security context with provided user name and roles.
     * 
     * @param username
     *            user name.
     * @param personId
     *            user's personId value.
     * @param plsUser
     *            <code>true</code> for PLS user. <code>false</code> for customer user.
     * @param capabilities
     *            Granted capabilities/permissions
     */
    public static void login(String username, Long personId, boolean plsUser, String... capabilities) {
        PlsUserDetails details = new PlsUserDetailsBuilder(username).withPassword(username)
                .withPersonId(personId).withParentOrgId(SecurityTestUtils.DEFAULT_ORGANIZATION_ID)
                .withEnabled(true).withCapabilities(capabilities).withIsPlsUser(plsUser).build();

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(details, null));
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
    }

    /**
     * Populate security context with provided user name and roles.
     * 
     * @param username
     *            user name.
     * @param capabilities
     *            Granted capabilities/permissions
     */
    public static void login(String username, String... capabilities) {
        PlsUserDetails details = new PlsUserDetailsBuilder(username).withPassword(username)
                .withPersonId(SecurityTestUtils.DEFAULT_PERSON_ID)
                .withParentOrgId(SecurityTestUtils.DEFAULT_ORGANIZATION_ID).withEnabled(true)
                .withCapabilities(capabilities).build();

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(details, null));
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
    }

    /**
     * Clear security context.
     */
    public static void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * Prevent instantiation.
     */
    private SecurityTestUtils() {
    }
}
