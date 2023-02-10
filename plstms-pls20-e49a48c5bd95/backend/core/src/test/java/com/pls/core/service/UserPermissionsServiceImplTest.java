package com.pls.core.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import com.pls.core.dao.SecurityDao;
import com.pls.core.service.impl.security.UserPermissionsServiceImpl;
import com.pls.core.service.impl.security.util.SecurityTestUtils;

/**
 * Test cases for {@link UserPermissionsServiceImpl} class.
 * 
 * @author Maxim Medvedev
 */
@RunWith(MockitoJUnitRunner.class)
public class UserPermissionsServiceImplTest {

    private static final Long ORG_ID = -2L;

    private static final Long PERSON_ID = -1L;

    private static final String TEST_CAP = "TestCap";

    @Mock
    private SecurityDao securityDao;

    @InjectMocks
    private UserPermissionsServiceImpl sut;

    @Before
    public void setUp() {
        SecurityTestUtils.logout();

        Set<String> emptySet = Collections.emptySet();
        Mockito.when(securityDao.loadCapabilities((Long) Matchers.anyObject())).thenReturn(emptySet);
    }

    @Test
    public void testCanHandleWithCurrentUser() {
        SecurityTestUtils.login("Test", PERSON_ID);

        Mockito.when(securityDao.loadOrganizations(PERSON_ID)).thenReturn(new HashSet<Long>(Arrays.asList(ORG_ID)));
        boolean result = sut.canHandle(ORG_ID);

        Assert.assertTrue(result);
        Mockito.verify(securityDao).loadOrganizations(PERSON_ID);
    }

    @Test
    public void testCanHandleWithNormalCase() {
        SecurityTestUtils.login("Test", PERSON_ID, ORG_ID);

        boolean result = sut.canHandle(ORG_ID);

        Assert.assertTrue(result);
        Mockito.verifyNoMoreInteractions(securityDao);
    }

    @Test
    public void testCanHandleWithoutOrganizations() {
        SecurityTestUtils.login("Test", PERSON_ID);

        boolean result = sut.canHandle(ORG_ID);

        Assert.assertFalse(result);
    }

    @Test
    public void testCanHandleWithOrganizationsAssignedThroughNetworkForCustomerUser() {
        SecurityTestUtils.login("Test", PERSON_ID);

        Mockito.when(securityDao.isCustomerAssignedThroughNetwork(PERSON_ID, ORG_ID)).thenReturn(true);
        boolean result = sut.canHandle(ORG_ID);

        Assert.assertFalse(result);
    }

    @Test
    public void testCanHandleWithOrganizationsAssignedThroughNetwork() {
        SecurityTestUtils.login("Test", PERSON_ID, true);

        Mockito.when(securityDao.isCustomerAssignedThroughNetwork(PERSON_ID, ORG_ID)).thenReturn(true);
        boolean result = sut.canHandle(ORG_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testCheckCapabilityAndOrganizationWithNormalCase() {
        SecurityTestUtils.login("Test", PERSON_ID, ORG_ID, TEST_CAP);

        sut.checkCapabilityAndOrganization(ORG_ID, TEST_CAP);
    }

    @Test
    public void testCheckCapabilityWithNormalCase() {
        SecurityTestUtils.login("Test", PERSON_ID, TEST_CAP);

        sut.checkCapability(TEST_CAP);
    }

    @Test(expected = AccessDeniedException.class)
    public void testCheckCapabilityWithoutCapabilities() {
        SecurityTestUtils.login("Test", PERSON_ID);

        sut.checkCapability(TEST_CAP);
    }

    @Test
    public void testCheckOrganizationWithNormalCase() {
        SecurityTestUtils.login("Test", PERSON_ID, ORG_ID);

        sut.checkOrganization(ORG_ID);
    }

    @Test(expected = AccessDeniedException.class)
    public void testCheckOrganizationWithoutOrganizations() {
        SecurityTestUtils.login("Test", PERSON_ID);

        sut.checkOrganization(ORG_ID);
    }

    @Test
    public void testHasCapabilityAndOrgWithNormalCase() {
        SecurityTestUtils.login("Test", PERSON_ID, ORG_ID, TEST_CAP);

        boolean result = sut.hasCapabilityAndOrganization(PERSON_ID, ORG_ID, TEST_CAP);

        Assert.assertTrue(result);
    }

    @Test
    public void testHasCapabilityWithCurrentUser() {
        SecurityTestUtils.login("Test", PERSON_ID);

        Mockito.when(securityDao.loadCapabilities(PERSON_ID)).thenReturn(new HashSet<String>(Arrays.asList(TEST_CAP)));
        boolean result = sut.hasCapability(PERSON_ID, TEST_CAP);

        Assert.assertTrue(result);
        Mockito.verify(securityDao).loadCapabilities(PERSON_ID);
    }

    @Test
    public void testHasCapabilityWithNonCurrentUser() {
        SecurityTestUtils.login("Test", PERSON_ID + 1L);

        boolean result = sut.hasCapability(PERSON_ID, TEST_CAP);

        Assert.assertFalse(result);
        Mockito.verify(securityDao).loadCapabilities(PERSON_ID);
    }

    @Test
    public void testHasCapabilityWithNormalCase() {
        SecurityTestUtils.login("Test", PERSON_ID, TEST_CAP);

        boolean result = sut.hasCapability(PERSON_ID, TEST_CAP);

        Assert.assertTrue(result);
        Mockito.verifyNoMoreInteractions(securityDao);
    }

    @Test
    public void testHasCapabilityWithNullPersonId() {
        SecurityTestUtils.login("Test", PERSON_ID);

        boolean result = sut.hasCapability((Long) null, TEST_CAP);

        Assert.assertFalse(result);
        Mockito.verifyZeroInteractions(securityDao);
    }

    @Test
    public void testHasCapabilityWithoutCapabilities() {
        SecurityTestUtils.login("Test", PERSON_ID);

        boolean result = sut.hasCapability(PERSON_ID, TEST_CAP);

        Assert.assertFalse(result);
    }

    @Test
    public void testHasCapabilityWithoutSecurityContext() {
        SecurityTestUtils.logout();

        sut.hasCapability(PERSON_ID, TEST_CAP);

        Mockito.verify(securityDao).loadCapabilities(PERSON_ID);
    }
}
