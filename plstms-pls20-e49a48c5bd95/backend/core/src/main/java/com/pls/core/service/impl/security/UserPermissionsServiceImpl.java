package com.pls.core.service.impl.security;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.SecurityDao;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;

/**
 * Service class for validation of user permissions.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class UserPermissionsServiceImpl implements UserPermissionsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPermissionsServiceImpl.class);

    @Autowired
    private SecurityDao securityDao;

    @Override
    public boolean canHandle(Long organizationId) {
        boolean result = false;
        Long personId = SecurityUtils.getCurrentPersonId();
        if (personId != null && organizationId != null) {
            Set<Long> organizations = extractOrganizations(personId, false);
            result = organizations.contains(organizationId);
            if (!result) { // read organizations from database to check if some QA has created new organization in another browser
                LOGGER.warn("Reading list of organizations for user {} to check if he can handle organization {}. Current user is {}", personId,
                        organizationId, SecurityUtils.getCurrentPersonId());
                organizations = extractOrganizations(personId, true);
                result = organizations.contains(organizationId);
            }
            if (!result && SecurityUtils.isPlsUser()) {
                result = securityDao.isCustomerAssignedThroughNetwork(personId, organizationId);
            }
        }
        return result;
    }

    @Override
    public void checkCapability(String... capabilities) throws AccessDeniedException {
        if (!hasCapability(SecurityUtils.getCurrentPersonId(), capabilities)) {
            throw new AccessDeniedException("User ID='" + SecurityUtils.getCurrentPersonId()
                    + "' does not have specified privilegy(ies) (" + StringUtils.join(capabilities) + ")");
        }
    }

    @Override
    public void checkCapabilityAndOrganization(Long organizationId, String... capabilities) throws AccessDeniedException {
        checkCapability(capabilities);
        checkOrganization(organizationId);
    }

    @Override
    public void checkOrganization(Long organizationId) throws AccessDeniedException {
        if (!canHandle(organizationId)) {
            throw new AccessDeniedException("User ID='" + SecurityUtils.getCurrentPersonId()
                    + "' is not permitted to handle organizatuion ID='" + organizationId + "'");
        }
    }

    @Override
    public boolean hasCapability(Long personId, String... capabilities) {
        boolean result = false;
        if (personId != null && capabilities != null && capabilities.length > 0) {
            result = isAnyCapabilityPresent(extractCapabillities(personId, false), capabilities);
            if (!result) { // read capabilities from database to check if someone has updated them for current user
                LOGGER.warn("Reading list of capabilities for user {} to check if he has {} capability. Current user is {}", personId,
                        StringUtils.join(capabilities, " or "), SecurityUtils.getCurrentPersonId());
                result = isAnyCapabilityPresent(extractCapabillities(personId, true), capabilities);
            }
        }
        return result;
    }

    @Override
    public boolean hasCapability(String... capabilities) {
        return hasCapability(SecurityUtils.getCurrentPersonId(), capabilities);
    }

    @Override
    public boolean hasCapabilityAndOrganization(Long personId, Long organizationId, String... capabilities) {
        return hasCapability(personId, capabilities) && canHandle(organizationId);
    }

    @Override
    public boolean hasCapabilityAndOrganization(Long organizationId, String... capabilities) {
        return hasCapability(capabilities) && canHandle(organizationId);
    }

    private boolean isAnyCapabilityPresent(Set<String> realCapabilities, String... desiredCapabilities) {
        for (String cap : desiredCapabilities) {
            if (realCapabilities.contains(cap)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> extractCapabillities(Long personId, boolean forceReload) {
        boolean currentUser = personId.equals(SecurityUtils.getCurrentPersonId());
        if (!forceReload && currentUser) {
            return SecurityUtils.getCapabilities();
        } else if (forceReload == currentUser) { // no need to reload for another user once more
            return securityDao.loadCapabilities(personId);
        }
        return Collections.emptySet();
    }

    private Set<Long> extractOrganizations(Long personId, boolean forceReload) {
        boolean currentUser = personId.equals(SecurityUtils.getCurrentPersonId());
        if (!forceReload && currentUser) {
            return SecurityUtils.getOrganizations();
        } else if (forceReload == currentUser) { // no need to reload for another user once more
            return securityDao.loadOrganizations(personId);
        }
        return Collections.emptySet();
    }
}
