package com.pls.core.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.user.UserInfoBO;
import com.pls.core.service.AuthService;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.impl.security.PlsUserDetails;
import com.pls.core.service.impl.security.util.SecurityUtils;

/**
 * Implementation of {@link AuthService} interface.
 * 
 * @author Maxim Medvedev
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private UserDetailsService pls20DetailsService;

    @Autowired
    private DBUtilityService dbUtilityService;

    @Override
    public UserInfoBO findCurrentUser() {
        UserInfoBO result = null;
        Long personId = SecurityUtils.getCurrentPersonId();
        if (personId != null) {
            result = userInfoDao.getByPersonId(personId);
        }
        return result;
    }

    @Override
    public Set<String> getCapabalitiesForCurrentUser() {
        return SecurityUtils.getCapabilities();
    }

    @Override
    public boolean isCurrentPlsUser() {
        return SecurityUtils.isPlsUser();
    }

    @Override
    public void reSetUserAuthentication() {
        //flush any changes to DB before re-read user authentication data
        dbUtilityService.flushSession();
        //Set new authentication data for currently logged-in user
        SecurityUtils.setupNewAuthentication((PlsUserDetails) pls20DetailsService.loadUserByUsername(SecurityUtils.getCurrentUserLogin()));
    }
}
