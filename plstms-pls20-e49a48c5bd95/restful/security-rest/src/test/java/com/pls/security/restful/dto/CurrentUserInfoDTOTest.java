package com.pls.security.restful.dto;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.bo.user.UserInfoBO;
import com.pls.dto.enums.CustomerStatusReason;

/**
 * Test cases for {@link CurrentUserInfoDTO} class.
 * 
 * @author Maxim Medvedev
 */
public class CurrentUserInfoDTOTest {
    @Test
    public void testConstructorWithEmptyFields() {
        UserInfoBO userInfo = new UserInfoBO();
        CurrentUserInfoDTO sut = new CurrentUserInfoDTO(userInfo, true, Collections.<String>emptyList());

        Assert.assertTrue(sut.isPlsUser());
        Assert.assertEquals(StringUtils.EMPTY, sut.getFullName());
        Assert.assertNull(sut.getPersonId());
        Assert.assertTrue(sut.getPrivilegies().isEmpty());
        Assert.assertNotNull(sut.getOrganization());
        Assert.assertNull(sut.getOrganization().getOrgId());
        Assert.assertNull(sut.getOrganization().getName());
        Assert.assertNull(sut.getOrganization().getStatusReason());
    }

    @Test
    public void testConstructorWithNormalCase() {
        final Long personId = (long) (Math.random() * 100);
        final String firstName = "firstName" + Math.random();
        final String lastName = "lastName" + Math.random();
        final Long orgId = (long) (Math.random() * 100);
        final String orgName = "name" + (Math.random() * 100);
        final CustomerStatusReason reason = CustomerStatusReason.values()[(int) ((CustomerStatusReason.values().length - 1) * Math.random())];

        UserInfoBO userInfo = new UserInfoBO();
        userInfo.setPersonId(personId);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        userInfo.setParentOrgId(orgId);
        userInfo.setParentOrgName(orgName);
        userInfo.setParentOrgStatusReason(reason.getValue());
        CurrentUserInfoDTO sut = new CurrentUserInfoDTO(userInfo, false, Arrays.asList("A", "B"));

        Assert.assertFalse(sut.isPlsUser());
        Assert.assertEquals(UserNameBuilder.buildFullName(firstName, lastName), sut.getFullName());
        Assert.assertSame(personId, sut.getPersonId());
        Assert.assertEquals(2, sut.getPrivilegies().size());
        Assert.assertTrue(sut.getPrivilegies().contains("A"));
        Assert.assertTrue(sut.getPrivilegies().contains("B"));
        Assert.assertNotNull(sut.getOrganization());
        Assert.assertSame(orgId, sut.getOrganization().getOrgId());
        Assert.assertSame(orgName, sut.getOrganization().getName());
        Assert.assertEquals(reason, sut.getOrganization().getStatusReason());
    }

    @Test
    public void testConstructorWithNullUserInfo() {
        CurrentUserInfoDTO sut = new CurrentUserInfoDTO(null, true, Arrays.asList("A", "B"));

        Assert.assertFalse(sut.isPlsUser());
        Assert.assertNull(sut.getFullName());
        Assert.assertNull(sut.getPersonId());
        Assert.assertNull(sut.getOrganization());
        Assert.assertNotNull(sut.getPrivilegies());
        Assert.assertEquals(0, sut.getPrivilegies().size());
    }
}
