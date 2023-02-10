package com.pls.user.dao.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.user.dao.GroupDao;
import com.pls.user.domain.GroupCapabilitiesEntity;
import com.pls.user.domain.GroupEntity;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Test cases for {@link GroupDaoImpl} class.
 * 
 * @author Pavani Challa
 */
public class GroupDaoImplIT extends AbstractDaoTest {

    public static final Long SYSADMIN_USER = 1L;

    private static final String NEW_GROUP = "New Group";

    private static final String EXISTING_GROUP = "TestGroupWithAllPrivilegies";

    private static final Long GROUP_ID = 1L;

    @Autowired
    private GroupDao sut;

    @Test
    public void testFindAllGroups() {
        List<GroupEntity> result = sut.findAllGroups(SYSADMIN_USER);

        for (GroupEntity group : result) {
            Assert.assertEquals("A", group.getStatus());
            Assert.assertNotNull(group.getGrpCapabilities());
            Assert.assertFalse(group.getGrpCapabilities().isEmpty());
        }
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of groups returned", 1, result.size());
    }

    @Test
    public void testFindAllGroupsForRootUser() {
        SecurityTestUtils.login("ROOT", 0L, true, "Test Capability");

        List<GroupEntity> result = sut.findAllGroups(0L);
        for (GroupEntity group : result) {
            Assert.assertEquals("A", group.getStatus());
            Assert.assertNotNull(group.getGrpCapabilities());
            Assert.assertFalse(group.getGrpCapabilities().isEmpty());
        }

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of groups returned", 1, result.size());
    }

    @Test
    public void testFindGroup() {
        GroupEntity result = sut.findGroup(GROUP_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals("GroupId is not matching", GROUP_ID, result.getId());
        Assert.assertEquals("Group name is not matching", EXISTING_GROUP, result.getName());
        Assert.assertNotNull(result.getCapabilities());
        Assert.assertFalse(result.getCapabilities().isEmpty());
    }

    @Test
    public void testFindGroupThatDoesntExist() {
        GroupEntity result = sut.findGroup(999L);

        Assert.assertNull(result);
    }

    @Test
    public void testDeleteGroup() {
        SecurityTestUtils.login("ROOT", 0L, true, "Test Capability");

        sut.delete(GROUP_ID);

        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(GROUP_ID, null);
        Assert.assertNotNull(result);
        Assert.assertTrue("Incorrect number of results returned", result.isEmpty());

    }

    @Test
    public void testUnassignGroup() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(GROUP_ID, null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 10, result.size());

        Long[] users = {1L, 2L};
        sut.unassignGroup(GROUP_ID, Arrays.asList(users));

        result = sut.findUsersWithGroup(GROUP_ID, null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 9, result.size());
    }

    @Test
    public void testFindUsersWithGroup() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(GROUP_ID, null);

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 10, result.size());
    }

    @Test
    public void testFindUsersBelongsToCustomerWithGroup() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(GROUP_ID, 1L);

        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 6, result.size());
    }

    @Test
    public void testCreateAsSysadmin() {
        SecurityTestUtils.login("SYSADMIN", SYSADMIN_USER, true, "Test Capability");

        GroupEntity entity = sut.saveOrUpdate(getNewGroup());

        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(entity.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 1, result.size());
    }

    @Test
    public void testCreateAsRoot() {
        SecurityTestUtils.login("ROOT", 0L, true, "Test Capability");

        GroupEntity entity = sut.saveOrUpdate(getNewGroup());

        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(entity.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 1, result.size());
    }

    @Test
    public void testUpdateAsSysadmin() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(GROUP_ID, null);
        Assert.assertEquals("Incorrect number of results returned", 10, result.size());

        SecurityTestUtils.login("SYSADMIN", SYSADMIN_USER, true, "Test Capability");
        GroupEntity entity = sut.saveOrUpdate(sut.findGroup(GROUP_ID));

        result = sut.findUsersWithGroup(entity.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 10, result.size());
    }

    @Test
    public void testUpdateAsRoot() {
        List<UserCapabilitiesResultVO> result = sut.findUsersWithGroup(2L, null);
        Assert.assertEquals("Incorrect number of results returned", 0, result.size());

        SecurityTestUtils.login("ROOT", 0L, true, "Test Capability");
        GroupEntity entity = sut.saveOrUpdate(sut.findGroup(2L));

        result = sut.findUsersWithGroup(entity.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertEquals("Incorrect number of results returned", 0, result.size());
    }

    @Test
    public void testIsNameUniqueForNewGroup() {
        Boolean result = sut.isNameUnique(NEW_GROUP, -1L);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNameUniqueForExstngGrpNNewName() {
        Boolean result = sut.isNameUnique(NEW_GROUP, GROUP_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNameUniqueForNewGroupNExstngName() {
        Boolean result = sut.isNameUnique(EXISTING_GROUP, -1L);

        Assert.assertFalse(result);
    }

    @Test
    public void testIsNameUniqueForExstngGroup() {
        Boolean result = sut.isNameUnique(EXISTING_GROUP, GROUP_ID);

        Assert.assertTrue(result);
    }

    private GroupEntity getNewGroup() {
        GroupCapabilitiesEntity capability = new GroupCapabilitiesEntity();
        capability.setCapabilityId(1001L);

        GroupEntity group = new GroupEntity();
        group.setName("New Group");
        group.setGrpCapabilities(Collections.singletonList(capability));

        return group;
    }
}