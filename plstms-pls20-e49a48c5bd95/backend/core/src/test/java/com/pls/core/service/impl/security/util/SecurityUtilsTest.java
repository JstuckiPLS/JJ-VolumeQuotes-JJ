package com.pls.core.service.impl.security.util;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * Test for {@link SecurityUtils}.
 * 
 * @author Aleksandr Leshchenko
 */
public class SecurityUtilsTest {

    @Test
    public void testGetCapabilitiesWithInvalidUserDetails() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(null, null);
        authentication.setAuthenticated(true);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Set<String> result = SecurityUtils.getCapabilities();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetCapabilitiesWithNormalCase() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withCapabilities("T");
        filllSecurityContext(builder);

        Set<String> result = SecurityUtils.getCapabilities();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetCapabilitiesWithoutAuthentication() {
        SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);

        Set<String> result = SecurityUtils.getCapabilities();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetCurrentPersondIdWithoutAuthentication() {
        SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);

        Assert.assertNull(SecurityUtils.getCurrentPersonId());
    }

    @Test
    public void testGetCurrentPersonIdWithInvalidUserDetails() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(null, null);
        authentication.setAuthenticated(true);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Assert.assertNull(SecurityUtils.getCurrentPersonId());
    }

    @Test
    public void testGetCurrentPersonIdWithNormalCase() {
        Long personId = 100500L;
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withPersonId(personId);
        filllSecurityContext(builder);

        Assert.assertEquals(personId, SecurityUtils.getCurrentPersonId());
    }

    @Test
    public void testGetCurrentPersonIdWithNullUserId() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withPersonId(null);
        filllSecurityContext(builder);

        Assert.assertNull(SecurityUtils.getCurrentPersonId());
    }

    @Test
    public void testGetgetCurrentUserLoginWithInvalidUserDetails() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(null, null);
        authentication.setAuthenticated(true);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Assert.assertNull(SecurityUtils.getCurrentUserLogin());
    }

    @Test
    public void testGetgetCurrentUserLoginWithNormalCase() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test");
        filllSecurityContext(builder);

        Assert.assertEquals("Test", SecurityUtils.getCurrentUserLogin());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetgetCurrentUserLoginWithNullUserName() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder(null);
        // We cannot build UserDetails with empty or null username
        builder.build();
    }

    @Test
    public void testGetgetCurrentUserLoginWithoutAuthentication() {
        SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);

        Assert.assertNull(SecurityUtils.getCurrentUserLogin());
    }

    @Test
    public void testGetOrganizationsWithInvalidUserDetails() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(null, null);
        authentication.setAuthenticated(true);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Set<Long> result = SecurityUtils.getOrganizations();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetOrganizationsWithNormalCase() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withOrganizations(Collections
                .singleton(-1L));
        filllSecurityContext(builder);

        Set<Long> result = SecurityUtils.getOrganizations();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetOrganizationsWithoutAuthentication() {
        SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);

        Set<Long> result = SecurityUtils.getOrganizations();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGetParentOrgIdWithInvalidUserDetails() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(null, null);
        authentication.setAuthenticated(true);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Assert.assertNull(SecurityUtils.getParentOrganizationId());
    }

    @Test
    public void testGetParentOrgIdWithNormalCase() {
        Long orgId = 100500L;
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withParentOrgId(orgId);
        filllSecurityContext(builder);

        Assert.assertEquals(orgId, SecurityUtils.getParentOrganizationId());
    }

    @Test
    public void testGetParentOrgIdWithNullOrgId() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withParentOrgId(null);
        filllSecurityContext(builder);

        Assert.assertNull(SecurityUtils.getParentOrganizationId());
    }

    @Test
    public void testGetParentOrgIdWithoutAuthentication() {
        SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);

        Assert.assertNull(SecurityUtils.getParentOrganizationId());
    }

    @Test
    public void testIsPlsUserWithCustomeruser() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withIsPlsUser(false);
        filllSecurityContext(builder);

        Assert.assertFalse(SecurityUtils.isPlsUser());
    }

    @Test
    public void testIsPlsUserWithoutAuthentication() {
        SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);

        Assert.assertFalse(SecurityUtils.isPlsUser());
    }

    @Test
    public void testIsPlsUserWithPlsUser() {
        PlsUserDetailsBuilder builder = new PlsUserDetailsBuilder("Test").withIsPlsUser(true);
        filllSecurityContext(builder);

        Assert.assertTrue(SecurityUtils.isPlsUser());
    }

    private void filllSecurityContext(PlsUserDetailsBuilder builder) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(builder.build(), null);
        authentication.setAuthenticated(true);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
