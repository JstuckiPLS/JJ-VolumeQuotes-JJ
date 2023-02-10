package com.pls.core.service.impl.security;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pls.core.dao.SecurityDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.user.UserLoginBO;
import com.pls.core.domain.enums.UserStatus;

/**
 * Test for {@link PlsUserDetailsService}.
 * 
 * @author Maxim Medvedev
 */
@RunWith(MockitoJUnitRunner.class)
public class PlsUserDetailsServiceTest {
    private static final String LOGIN = "TestUser";

    private static final String PRIVILEGE1 = "Test1";
    private static final String PRIVILEGE2 = "Test2";

    @Mock
    private SecurityDao securityDao;

    @InjectMocks
    private PlsUserDetailsService sut;

    @Mock
    private UserInfoDao usersDao;

    @Test
    public void testLoadUserByUsernameWithEnabledUser() throws Exception {
        UserLoginBO user = prepareMinUser();
        user.setStatus(UserStatus.ACTIVE);
        user.setParentOrgId(-1L);
        when(usersDao.findByLogin(PlsUserDetailsServiceTest.LOGIN)).thenReturn(user);
        when(securityDao.loadCapabilities(-1L)).thenReturn(new HashSet<String>());
        HashSet<Long> organizations = new HashSet<Long>();
        organizations.add(10L);
        when(securityDao.loadOrganizations(-1L)).thenReturn(organizations);

        UserDetails result = sut.loadUserByUsername(PlsUserDetailsServiceTest.LOGIN);

        assertTrue(result instanceof PlsUserDetails);
        assertTrue(((PlsUserDetails) result).isEnabled());
    }

    @Test
    public void testLoadUserByUsernameWithInactiveUser() throws Exception {
        UserLoginBO user = prepareMinUser();
        user.setStatus(UserStatus.INACTIVE);
        user.setParentOrgId(-1L);
        when(usersDao.findByLogin(PlsUserDetailsServiceTest.LOGIN)).thenReturn(user);
        when(securityDao.loadCapabilities(-1L)).thenReturn(new HashSet<String>());
        when(securityDao.loadOrganizations(-1L)).thenReturn(new HashSet<Long>());
        when(securityDao.isPlsUser(user.getParentOrgId())).thenReturn(true);

        UserDetails result = sut.loadUserByUsername(PlsUserDetailsServiceTest.LOGIN);

        assertTrue(result instanceof PlsUserDetails);
        assertFalse(((PlsUserDetails) result).isEnabled());
    }

    @Test
    public void testLoadUserByUsernameWithNonAssociatedUser() throws Exception {
        UserLoginBO user = prepareMinUser();
        user.setStatus(UserStatus.ACTIVE);
        user.setParentOrgId(null);
        when(usersDao.findByLogin(PlsUserDetailsServiceTest.LOGIN)).thenReturn(user);
        when(securityDao.isPlsUser(user.getParentOrgId())).thenReturn(true);
        when(securityDao.loadCapabilities(-1L)).thenReturn(new HashSet<String>());

        UserDetails result = sut.loadUserByUsername(PlsUserDetailsServiceTest.LOGIN);

        assertTrue(result instanceof PlsUserDetails);
        assertFalse(((PlsUserDetails) result).isEnabled());
    }

    @Test
    public void testLoadUserByUsernameWithNormalCase() throws Exception {
        UserLoginBO user = prepareMinUser();
        when(usersDao.findByLogin(PlsUserDetailsServiceTest.LOGIN)).thenReturn(user);
        Set<String> privileges = new HashSet<String>();
        privileges.add(PlsUserDetailsServiceTest.PRIVILEGE1);
        privileges.add(PlsUserDetailsServiceTest.PRIVILEGE2);

        when(securityDao.loadCapabilities(-1L)).thenReturn(privileges);
        when(securityDao.isPlsUser(user.getParentOrgId())).thenReturn(true);

        UserDetails result = sut.loadUserByUsername(PlsUserDetailsServiceTest.LOGIN);

        assertTrue(result instanceof PlsUserDetails);

        assertEquals(user.getPersonId(), ((PlsUserDetails) result).getPersonId());
        assertEquals(PlsUserDetailsServiceTest.LOGIN, ((PlsUserDetails) result).getUsername());
        assertEquals("TestPwd", ((PlsUserDetails) result).getPassword());
        assertEquals(privileges.size(), ((PlsUserDetails) result).getCapabilities().size());

        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(
                new SimpleGrantedAuthority(PlsUserDetailsServiceTest.PRIVILEGE1)));
        assertTrue(result.getAuthorities().contains(
                new SimpleGrantedAuthority(PlsUserDetailsServiceTest.PRIVILEGE2)));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameWithoutUser() throws Exception {
        when(usersDao.findByLogin(PlsUserDetailsServiceTest.LOGIN)).thenReturn(null);

        sut.loadUserByUsername(PlsUserDetailsServiceTest.LOGIN);
    }

    @Test(expected = InactiveCustomerUserAuthenticationException.class)
    public void testLoadCustomerUserWithForInactiveCustomer() {
        UserLoginBO user = prepareMinUser();
        when(usersDao.findByLogin(PlsUserDetailsServiceTest.LOGIN)).thenReturn(user);

        sut.loadUserByUsername(PlsUserDetailsServiceTest.LOGIN);
    }

    private UserLoginBO prepareMinUser() {
        UserLoginBO user = new UserLoginBO();
        user.setPersonId(-1L);
        user.setUserId(PlsUserDetailsServiceTest.LOGIN);
        user.setPassword("TestPwd");
        return user;
    }
}
