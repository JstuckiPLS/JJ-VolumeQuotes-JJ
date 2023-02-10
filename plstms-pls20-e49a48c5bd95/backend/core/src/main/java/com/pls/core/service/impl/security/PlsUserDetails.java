package com.pls.core.service.impl.security;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of {@link UserDetails} additionally holding PERSON_ID and some other useful stuff.
 * 
 * @author Maxim Medvedev
 */
public class PlsUserDetails extends User {
    private static final long serialVersionUID = 1L;

    private static List<GrantedAuthority> prepareAuthorities(Set<String> capabilities) {
        return AuthorityUtils.createAuthorityList(capabilities.toArray(new String[capabilities.size()]));
    }

    private final Set<String> capabilities;
    private final Set<Long> organizations;

    private final Long parentOrgId;
    private final Long personId;

    private final boolean plsUser;

    /**
     * Constructor.
     * 
     * @param username
     *            User's DEFAULT_PERSON_ID or login.
     * @param password
     *            Password value.
     * @param enabled
     *            <code>true</code> if this user is enabled.
     * @param capabilities
     *            Not <code>null</code> {@link Set} with user capabilities.
     * @param personId
     *            User's PERSON_ID value.
     * @param parentOrgId
     *            ID of organization in which this user works.
     * @param isPlsUser
     *            <code>true</code> if this is a PLS user. <code>false</code> for customer user.
     * @param organizations
     *            Not <code>null</code> {@link Set} with organization IDs which user may view or manage.
     */
    public PlsUserDetails(String username, String password, boolean enabled, Set<String> capabilities,
            Long personId, Long parentOrgId, boolean isPlsUser, Set<Long> organizations) {
        super(username, password, enabled, true, true, true, PlsUserDetails.prepareAuthorities(capabilities));
        this.personId = personId;
        this.parentOrgId = parentOrgId;
        plsUser = isPlsUser;
        this.capabilities = capabilities;
        this.organizations = organizations;
    }

    /**
     * Constructor.
     * 
     * @param username
     *            User's DEFAULT_PERSON_ID or login.
     * @param password
     *            Password value.
     * @param authorities
     *            <@ List<GrantedAuthority>>
     * @param personId
     *            User's PERSON_ID value.
     * @param parentOrgId
     *            ID of organization in which this user works.
     */
    public PlsUserDetails(String username, String password, List<GrantedAuthority> authorities, Long personId,
            Long parentOrgId) {
        super(username, password, authorities);
        this.personId = personId;
        this.parentOrgId = parentOrgId;
        plsUser = false;
        this.capabilities = null;
        this.organizations = null;
    }

    /**
     * Get PLS capabilities. This is the same as {@link #getAuthorities()} but in {@link String}
     * representation.
     * 
     * @return Not <code>null</code> {@link Set}.
     */
    public Set<String> getCapabilities() {
        return capabilities;
    }

    /**
     * Organization which can me managed by this user.
     * 
     * @return Not <code>null</code> {@link Set}.
     */
    public Set<Long> getOrganizations() {
        return organizations;
    }

    /**
     * Organization to which the user belongs.
     * 
     * @return Normally not <code>null</code> {@link Long}.
     */
    public Long getParentOrgId() {
        return parentOrgId;
    }

    public Long getPersonId() {
        return personId;
    }

    public boolean isCustomerUser() {
        return !isPlsUser();
    }

    public boolean isPlsUser() {
        return plsUser;
    }
}
