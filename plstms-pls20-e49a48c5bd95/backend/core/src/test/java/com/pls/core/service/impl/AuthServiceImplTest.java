package com.pls.core.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.OrganizationDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.user.UserInfoBO;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.impl.security.util.SecurityUtils;

/**
 * Test cases for {@link AuthServiceImpl} class.
 * 
 * @author Maxim Medvedev
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {
    private static final Long PERSON_ID = -1L;

    @Mock
    private OrganizationDao organizationDao;

    @InjectMocks
    private AuthServiceImpl sut;

    @Mock
    private UserInfoDao userInfoDao;

    @Before
    public void setUp() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testFindCurrentUserWithNormalCase() {
        SecurityTestUtils.login("Test", PERSON_ID);
        UserInfoBO userInfo = new UserInfoBO();
        Mockito.when(userInfoDao.getByPersonId(PERSON_ID)).thenReturn(userInfo);

        UserInfoBO result = sut.findCurrentUser();

        Assert.assertSame(userInfo, result);
        Mockito.verify(userInfoDao).getByPersonId(PERSON_ID);
    }

    @Test
    public void testFindCurrentUserWithoutLogginedUser() {
        SecurityTestUtils.logout();

        UserInfoBO result = sut.findCurrentUser();

        Assert.assertNull(result);
        Mockito.verifyZeroInteractions(userInfoDao);
    }

    @Test
    public void testGetCapabalitiesForCurrentUser() {
        SecurityTestUtils.login("Test");
        Assert.assertSame(SecurityUtils.getCapabilities(), sut.getCapabalitiesForCurrentUser());
    }

    @Test
    public void testIsCurrentPlsUserWithoutLoggindUser() {
        SecurityTestUtils.login("Test", PERSON_ID, true);

        Assert.assertTrue(sut.isCurrentPlsUser());
    }

    @Test
    public void testIsCurrentPlsUserWithPlsUser() {
        SecurityTestUtils.logout();

        Assert.assertFalse(sut.isCurrentPlsUser());
    }
}
