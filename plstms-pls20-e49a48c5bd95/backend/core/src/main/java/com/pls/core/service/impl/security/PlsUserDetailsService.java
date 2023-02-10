package com.pls.core.service.impl.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.SecurityDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.user.UserLoginBO;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.service.impl.security.util.PlsUserDetailsBuilder;

/**
 * Implementation of {@link UserDetailsService}.
 * 
 * @author Viacheslav Krot
 * 
 */
@Qualifier("pls2.0")
@Service("pls20DetailsService")
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class PlsUserDetailsService implements UserDetailsService, ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private SecurityDao securityDao;

    @Autowired
    private UserInfoDao userDao;

    @Override
    public PlsUserDetails loadUserByUsername(String username) {
        UserLoginBO userInfo = userDao.findByLogin(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User '" + username + "' was not found");
        }
        boolean plsUser = isPlsUser(userInfo);
        Set<Long> organizations = securityDao.loadOrganizations(userInfo.getPersonId());

        /* If customer is inactive customer users should not be able to login.
         * First part of this condition ensures non pls user's login attempt - hence customer user.
         * Second part checks whether customer user has inactive customer */
        if (!plsUser && organizations.isEmpty()) {
            throw new InactiveCustomerUserAuthenticationException("Customer user authentication attempt of inactive customer failure");
        }

        PlsUserDetailsBuilder result = new PlsUserDetailsBuilder(userInfo.getUserId());
        result.withPassword(userInfo.getPassword()).withPersonId(userInfo.getPersonId());
        result.withIsPlsUser(plsUser);
        result.withParentOrgId(userInfo.getParentOrgId());

        result.withOrganizations(organizations);
        result.withEnabled(UserStatus.ACTIVE.equals(userInfo.getStatus()) && !organizations.isEmpty());
        result.withCapabilities(securityDao.loadCapabilities(userInfo.getPersonId()));
        return result.build();
    }

    private boolean isPlsUser(UserLoginBO userInfo) {
        return securityDao.isPlsUser(userInfo.getParentOrgId());
    }

    @Override
    @Transactional
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication().getPrincipal() instanceof PlsUserDetails) {
            Long personId = ((PlsUserDetails) event.getAuthentication().getPrincipal()).getPersonId();
            securityDao.saveLastLoginDateByPersonId(personId);
        }
    }

}