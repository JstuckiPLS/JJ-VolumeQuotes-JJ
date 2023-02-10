package com.pls.core.dao.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.user.UserEmailBO;
import com.pls.core.domain.bo.user.UserInfoBO;
import com.pls.core.domain.bo.user.UserLoginBO;

/**
 * Test cases for {@link UserInfoDaoImpl} class.
 * 
 * @author Maxim Medvedev
 */
public class UserInfoDaoImplIT extends AbstractDaoTest {

    private static final String VALID_USERID = "root";

    @Autowired
    private UserInfoDao sut;

    @Test
    public void shouldFindUserInfo() {
        UserInfoBO user = sut.getByPersonId(1L);

        Assert.assertEquals(new Long(1), user.getPersonId());
        Assert.assertEquals("admin", user.getFirstName());
        Assert.assertEquals("sysadmin", user.getLastName());
        Assert.assertEquals(new Long(38941), user.getParentOrgId());
        Assert.assertEquals("PLS PRO", user.getParentOrgName());
        Assert.assertEquals("ACTOPREQ", user.getParentOrgStatusReason());
    }

    @Test
    public void testFindUserByLoginWithDiffStringCase() {
        UserLoginBO result1 = sut.findByLogin(VALID_USERID.toLowerCase(Locale.ENGLISH));
        UserLoginBO result2 = sut.findByLogin(VALID_USERID.toUpperCase(Locale.ENGLISH));

        Assert.assertNotNull(result1);
        Assert.assertNotNull(result2);
        Assert.assertEquals(result1.getUserId(), result2.getUserId());
        Assert.assertEquals(result1.getPassword(), result2.getPassword());
        Assert.assertEquals(result1.getParentOrgId(), result2.getParentOrgId());
        Assert.assertEquals(result1.getPersonId(), result2.getPersonId());
        Assert.assertEquals(result1.getStatus(), result2.getStatus());
    }

    @Test
    public void testFindUserByLoginWithInvalidCase() {
        UserLoginBO result = sut.findByLogin(VALID_USERID + "SomeInvalidPrefix");

        Assert.assertNull(result);
    }

    @Test
    public void testFindUserByLoginWithNull() {
        UserLoginBO result = sut.findByLogin(null);

        Assert.assertNull(result);
    }

    @Test
    public void testFindUserByLoginWithValidCase() {
        UserLoginBO result = sut.findByLogin(VALID_USERID);

        Assert.assertNotNull(result);
        Assert.assertTrue(StringUtils.endsWithIgnoreCase(result.getUserId(), VALID_USERID));
    }

    @Test
    public void testfindUsers() {
        Long currentPersonId = 1L;
        List<UserEmailBO> result = sut.findUsers(currentPersonId, "t");
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.size());

        result = sut.findUsers(currentPersonId, "T");
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.size());

        result = sut.findUsers(currentPersonId, "test");
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.size());

        result = sut.findUsers(currentPersonId, "TEST");
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.size());

        Assert.assertEquals("DONOTREPLY@test.com", result.get(0).getEmail());
        Assert.assertEquals("admin", result.get(0).getFirstName());
        Assert.assertEquals("sysadmin", result.get(0).getLastName());
    }
}
