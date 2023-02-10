package com.pls.core.common.utils;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.user.UserEntity;

/**
 * Test cases for {@link UserNameBuilder} class.
 * 
 * @author Maxim Medvedev
 */
public class UserNameBuilderTest {

    @Test
    public void testBuildFullNameByUserWithNormalCase() {
        UserEntity user = new UserEntity();
        user.setFirstName("First");
        user.setLastName("Last");
        String result = UserNameBuilder.buildFullName(user);

        Assert.assertEquals("First Last", result);
    }

    @Test
    public void testBuildFullNameByUserWithNull() {
        String result = UserNameBuilder.buildFullName(null);

        Assert.assertEquals("", result);
    }

    @Test
    public void testBuildFullNameWithNormalCase() {
        String result = UserNameBuilder.buildFullName("First", "Last");

        Assert.assertEquals("First Last", result);
    }

    @Test
    public void testBuildFullNameWithNulls() {
        String result = UserNameBuilder.buildFullName(null, null);

        Assert.assertEquals("", result);
    }

    @Test
    public void testBuildFullNameWithoutNullFirstName() {
        String result = UserNameBuilder.buildFullName(null, "Last");

        Assert.assertEquals("Last", result);
    }

    @Test
    public void testBuildFullNameWithoutNullLastName() {
        String result = UserNameBuilder.buildFullName("First", null);

        Assert.assertEquals("First", result);
    }
}
