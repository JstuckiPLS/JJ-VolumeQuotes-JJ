package com.pls.core.service.impl.security.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.service.impl.security.PlsUserDetails;

/**
 * Builder for {@link PlsUserDetails}.
 * 
 * @author Aleksandr Leshchenko
 */
public class PlsUserDetailsBuilder {
    private final Set<String> capabilities = new HashSet<String>();

    private boolean enabled = true;
    private boolean isPlsUser;
    private final Set<Long> organizations = new HashSet<Long>();
    private Long parentOrgId;
    private String password = StringUtils.EMPTY;
    private Long personId;

    private final String userName;

    /**
     * Constructor.
     * 
     * @param newUserName
     *            User name is mandatory, so this should be not <code>null</code> and not empty string.
     */
    public PlsUserDetailsBuilder(String newUserName) {
        userName = StringUtils.trimToEmpty(newUserName);
    }

    /**
     * Build {@link PlsUserDetails} class.
     * 
     * @return Not <code>null</code> {@link PlsUserDetails}.
     */
    public PlsUserDetails build() {
        return new PlsUserDetails(userName, password, enabled, capabilities, personId, parentOrgId,
                isPlsUser, organizations);
    }

    /**
     * Add capabilities.
     * 
     * @param newCapabilities
     *            Not <code>null</code> {@link String}s.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withCapabilities(Collection<String> newCapabilities) {
        capabilities.addAll(newCapabilities);
        return this;
    }

    /**
     * Add capabilities.
     * 
     * @param newCapabilities
     *            Not <code>null</code> {@link String}s.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withCapabilities(String... newCapabilities) {
        return withCapabilities(Arrays.asList(newCapabilities));
    }

    /**
     * Set enabled flag.
     * 
     * @param enabled
     *            Value for {@link PlsUserDetails#isEnabled()} field.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Set {@link PlsUserDetails#isPlsUser()} value.
     * 
     * @param isPlsUser
     *            Value for {@link PlsUserDetails#isPlsUser()} field.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withIsPlsUser(boolean isPlsUser) {
        this.isPlsUser = isPlsUser;
        return this;
    }

    /**
     * Set organizations which can be managed by this user.
     * 
     * @param newOrganizations
     *            Not <code>null</code> {@link Set}.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withOrganizations(Set<Long> newOrganizations) {
        organizations.addAll(newOrganizations);
        return this;
    }

    /**
     * Set organization ID to which user belongs.
     * 
     * @param newParentOrgId
     *            Value for {@link PlsUserDetails#getParentOrgId()} field.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withParentOrgId(Long newParentOrgId) {
        parentOrgId = newParentOrgId;
        return this;
    }

    /**
     * Set password.
     * 
     * @param newPassword
     *            Value for {@link PlsUserDetails#getPassword()} field.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withPassword(String newPassword) {
        password = StringUtils.trimToEmpty(newPassword);
        return this;
    }

    /**
     * Set PERSON_ID.
     * 
     * @param newPersonId
     *            Value for {@link PlsUserDetails#getPersonId()} field.
     * @return <code>this</code>
     */
    public PlsUserDetailsBuilder withPersonId(Long newPersonId) {
        personId = newPersonId;
        return this;
    }

}