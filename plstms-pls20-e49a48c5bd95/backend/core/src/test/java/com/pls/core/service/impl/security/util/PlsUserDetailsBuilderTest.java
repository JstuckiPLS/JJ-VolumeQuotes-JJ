package com.pls.core.service.impl.security.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.service.impl.security.PlsUserDetails;

/**
 * Test cases for {@link PlsUserDetailsBuilder} class.
 * 
 * @author Maxim Medvedev
 */
public class PlsUserDetailsBuilderTest {

    private static final String USER_NAME = "Test";

    @Test
    public void testBuildWithUsernameOnly() {
        PlsUserDetailsBuilder sut = new PlsUserDetailsBuilder(USER_NAME);

        PlsUserDetails result = sut.build();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEnabled());
        Assert.assertFalse(result.isPlsUser());
        Assert.assertEquals(USER_NAME, result.getUsername());
        Assert.assertEquals(StringUtils.EMPTY, result.getPassword());
        Assert.assertEquals(0, result.getAuthorities().size());
    }

    @Test
    public void testWithCapabilitiesWithDuplications() {
        List<String> capabilities = new ArrayList<String>();
        capabilities.add("Test");
        capabilities.add("Test");

        PlsUserDetailsBuilder sut = new PlsUserDetailsBuilder(USER_NAME);
        sut.withCapabilities(capabilities);

        PlsUserDetails result = sut.build();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getCapabilities().size());
        Assert.assertTrue(result.getCapabilities().contains("Test"));
    }

    @Test
    public void testWithCapabilitiesArrayWithDuplications() {
        PlsUserDetailsBuilder sut = new PlsUserDetailsBuilder(USER_NAME);
        sut.withCapabilities("Test", "Test");

        PlsUserDetails result = sut.build();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getCapabilities().size());
        Assert.assertEquals(1, result.getCapabilities().size());
    }

}
