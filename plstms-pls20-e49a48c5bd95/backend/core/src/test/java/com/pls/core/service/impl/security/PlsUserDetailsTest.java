package com.pls.core.service.impl.security;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link PlsUserDetails} class.
 * 
 * @author Maxim Medvedev
 */
public class PlsUserDetailsTest {

    private static final String USER_NAME = "TestUser";

    @Test
    public void testEnabledFlagWithFalse() {
        PlsUserDetails sut = new PlsUserDetails(PlsUserDetailsTest.USER_NAME, PlsUserDetailsTest.USER_NAME,
                false, new HashSet<String>(), null, null, false, new HashSet<Long>());

        Assert.assertFalse(sut.isEnabled());
        Assert.assertTrue(sut.isAccountNonExpired());
        Assert.assertTrue(sut.isAccountNonLocked());
        Assert.assertTrue(sut.isCredentialsNonExpired());
    }

    @Test
    public void testEnabledFlagWithTrue() {
        PlsUserDetails sut = new PlsUserDetails(PlsUserDetailsTest.USER_NAME, PlsUserDetailsTest.USER_NAME,
                true, new HashSet<String>(), null, null, false, new HashSet<Long>());

        Assert.assertTrue(sut.isEnabled());
        Assert.assertTrue(sut.isAccountNonExpired());
        Assert.assertTrue(sut.isAccountNonLocked());
        Assert.assertTrue(sut.isCredentialsNonExpired());
    }

    @Test
    public void testGetCapabilities() {
        Set<String> privileges = new HashSet<String>();
        privileges.add("Test");

        PlsUserDetails sut = new PlsUserDetails(PlsUserDetailsTest.USER_NAME, PlsUserDetailsTest.USER_NAME,
                true, privileges, null, null, false, new HashSet<Long>());

        Assert.assertEquals(1, sut.getCapabilities().size());
        Assert.assertSame(privileges, sut.getCapabilities());

        Assert.assertEquals(1, sut.getAuthorities().size());
        Assert.assertEquals("Test", sut.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testIsPlsUserWithCustomerUser() {
        PlsUserDetails sut = new PlsUserDetails(PlsUserDetailsTest.USER_NAME, PlsUserDetailsTest.USER_NAME,
                true, new HashSet<String>(), null, null, false, new HashSet<Long>());

        Assert.assertFalse(sut.isPlsUser());
        Assert.assertTrue(sut.isCustomerUser());
    }

    @Test
    public void testIsPlsUserWithPlsUser() {
        PlsUserDetails sut = new PlsUserDetails(PlsUserDetailsTest.USER_NAME, PlsUserDetailsTest.USER_NAME,
                true, new HashSet<String>(), null, null, true, new HashSet<Long>());

        Assert.assertTrue(sut.isPlsUser());
        Assert.assertFalse(sut.isCustomerUser());
    }

}
