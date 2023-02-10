package com.pls.user.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.user.dao.CapabilityDao;
import com.pls.user.domain.CapabilityEntity;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Test cases for {@link CapabilityDaoImpl} class.
 * 
 * @author Pavani Challa
 */
public class CapabilityDaoImplIT extends AbstractDaoTest {
    public static final Long SYSADMIN_USER = 1L;

    public static final Long NO_GRP_USER = 24L;

    public static final Long WITH_GRP_USER = 25L;

    private static final Long GROUP_ID = 1L;

    private static final Long CAP_DIRECTLY_ASSIGNED = 99997L;

    private static final Long GRP_CAPABILITY = 99998L;

    @Autowired
    private CapabilityDao sut;

    @Before
    public void setUp() {
        executeScript("addCapabilities.sql");
    }

    @Test
    public void testFindAllCapabilities() {
        SecurityTestUtils.login("SYSADMIN", SYSADMIN_USER, true, new String[] {"Test Capability"});

        List<CapabilityEntity> result = sut.findAllCapabilities(null);

        Assert.assertNotNull(result);
        Assert.assertTrue("Incorrect number of capabilities returned", result.size() >= 40);
    }

    @Test
    public void testFindAllCapsForRootUser() {
        SecurityTestUtils.login("ROOT", 0L, true, new String[] { "Test Capability" });

        List<CapabilityEntity> result = sut.findAllCapabilities(null);

//        2 - Assigned to user with Grp directly assigned
//        3 - Assigned to user with Grp both in Grp and directly assigned
//        4 - Assigned to no grp user directly assigned and to user with grp directly assigned.

        Assert.assertNotNull(result);
        Assert.assertTrue("Incorrect number of capabilities returned", result.size() >= 42);
    }

    @Test
    public void testFindAllCapsExcludingGrp() {
        SecurityTestUtils.login("WITH_GRP_USER", WITH_GRP_USER, true, new String[] {"Test Capability"});

        List<CapabilityEntity> result = sut.findAllCapabilities(GROUP_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of capabilities returned", 1, result.size());
    }

    @Test
    public void testFindAllCapsWhenNoGrp() {
        SecurityTestUtils.login("NO_GRP_USER", NO_GRP_USER, true, new String[] {"Test Capability"});

        List<CapabilityEntity> result = sut.findAllCapabilities(null);

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of capabilities returned", 1, result.size());
    }

    @Test
    public void testFindAllCapsAssignedAndWithGrp() {
        SecurityTestUtils.login("WITH_GRP_USER", WITH_GRP_USER, true, new String[] {"Test Capability"});

        List<CapabilityEntity> result = sut.findAllCapabilities(null);

        Assert.assertNotNull(result);
        Assert.assertTrue("Incorrect number of capabilities returned", result.size() >= 40);
    }

    @Test
    public void testFindCapsForGrp() {
        List<CapabilityEntity> result = sut.findCapabilitiesForGrp(GROUP_ID);

        Assert.assertNotNull(result);
        Assert.assertTrue("Incorrect number of capabilities returned", result.size() >= 39);
    }

    @Test
    public void testUnassignCapability() {
        SecurityTestUtils.login("SYSADMIN", SYSADMIN_USER, true, new String[] { "Test Capability" });
        List<UserCapabilitiesResultVO> result = sut.findUsersWithCapability(CAP_DIRECTLY_ASSIGNED, null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 2, result.size());

        Long[] users = {WITH_GRP_USER};
        sut.unassignCapability(CAP_DIRECTLY_ASSIGNED, Arrays.asList(users));

        result = sut.findUsersWithCapability(CAP_DIRECTLY_ASSIGNED, null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 1, result.size());
    }

    @Test
    public void testFindUsersWithCapDirectlyAssigned() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithCapability(CAP_DIRECTLY_ASSIGNED, null);

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 2, result.size());
        Assert.assertEquals("Incorrect value for direct permissions", "Y", result.get(0).getDirectPermission());
    }

    @Test
    public void testFindUsersWithCapOnlyThuGrp() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithCapability(GRP_CAPABILITY, null);

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 10, result.size());
        Assert.assertNotEquals("Incorrect value for direct permissions", "Y", result.get(0).getDirectPermission());
    }

    @Test
    public void testFindUsersBelongsToCustomerWithCapOnlyThuGrp() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithCapability(GRP_CAPABILITY, 1L);

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 6, result.size());
        Assert.assertNotEquals("Incorrect value for direct permissions", "Y", result.get(0).getDirectPermission());
    }
}
