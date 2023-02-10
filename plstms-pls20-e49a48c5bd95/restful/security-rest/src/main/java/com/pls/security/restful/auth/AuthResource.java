package com.pls.security.restful.auth;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.bo.user.UserInfoBO;
import com.pls.core.service.AuthService;
import com.pls.security.restful.dto.CurrentUserInfoDTO;
import com.pls.user.service.UserService;

/**
 * Service to obtain information about current user and perform login related operations.<br />
 * <b>Warning</b> some of URLs for this resource are handles by Spring Security engine. For example
 * "/auth/login", "/auth/logout","/auth/switch_user" and "/auth/exit_user". Please see "restful-security.xml"
 * file and documentation for more details.
 * 
 * @author Alexander Balan
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    /**
     * Get information about currently logged in user.
     * 
     * @return Not <code>null</code> Info about current user.
     */
    @RequestMapping(value = "/current_user", method = RequestMethod.GET)
    @ResponseBody
    public CurrentUserInfoDTO getCurrentUserInfo() {
        UserInfoBO userInfo = authService.findCurrentUser();
        boolean isPlsUser = authService.isCurrentPlsUser();
        Collection<String> capabilities = authService.getCapabalitiesForCurrentUser();
        return new CurrentUserInfoDTO(userInfo, isPlsUser, capabilities);
    }

    /**
     * Reset password for user.
     * 
     * @param userId
     *            the login of user to reset password
     * @throws Exception
     *             if password can't be reset
     */
    @RequestMapping(value = "/{userId}/password/reset", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void resetPassword(@PathVariable("userId") String userId) throws Exception {
        userService.resetPasword(userId);
    }
}
