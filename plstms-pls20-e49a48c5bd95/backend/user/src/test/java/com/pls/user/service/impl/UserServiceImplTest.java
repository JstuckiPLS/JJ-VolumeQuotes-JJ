package com.pls.user.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.NetworkDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.PlsMD5Encoder;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.util.PasswordUtils;
import com.pls.core.service.validation.EmailValidator;
import com.pls.core.service.validation.PasswordValidator;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.Status;
import com.pls.user.dao.UserCapabilitiesDao;
import com.pls.user.dao.UserDao;
import com.pls.user.dao.UserGroupDao;
import com.pls.user.domain.CapabilityEntity;
import com.pls.user.domain.UserCapabilityEntity;
import com.pls.user.domain.UserGroupEntity;
import com.pls.user.service.exc.PasswordsDoNotMatchException;
import com.pls.user.service.impl.email.UserManagementEmailSender;
import com.pls.user.service.validation.UserValidator;

/**
 * Test cases for {@link UserServiceImpl} class.
 * 
 * @author Maxim Medvedev
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Mock
    private CustomerDao customerDao;
    @Mock
    private UserGroupDao userGroupDao;
    @Mock
    private UserCapabilitiesDao userCapDao;
    @Mock
    private UserDao userDao;
    @Mock
    private UserInfoDao userInfoDao;
    @Mock
    private NetworkDao networkDao;

    @Mock
    private EmailValidator emailValidator;
    @Mock
    private PasswordValidator passwordValidator;
    @Mock
    private UserValidator userValidator;

    @Mock
    private UserManagementEmailSender emailProducer;

    @Mock
    private PasswordUtils passwordUtils;

    @InjectMocks
    private UserServiceImpl service;

    private final PlsMD5Encoder passwordEncoder = new PlsMD5Encoder();

    @Test
    public void shouldChangePassword() throws Exception {
        String userNewPassword = "newPwd" + Math.random();
        String userOldPassowrd = "oldPwd" + Math.random();
        long userId = (long) (Math.random() * 100);

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(new PlsMD5Encoder().encodePassword(userOldPassowrd, null));

        Mockito.when(userDao.get(userId)).thenReturn(userEntity);
        Mockito.when(passwordUtils.encodePassword(userNewPassword))
                .thenReturn(new PlsMD5Encoder().encodePassword(userNewPassword, null));
        Mockito.when(passwordUtils.encodePassword(userOldPassowrd))
                .thenReturn(new PlsMD5Encoder().encodePassword(userOldPassowrd, null));

        service.changePassword(userId, userOldPassowrd, userNewPassword);

        Mockito.verify(passwordValidator).validate(userNewPassword);
        Assert.assertEquals(new PlsMD5Encoder().encodePassword(userNewPassword, null),
                userEntity.getPassword());
    }

    @Test(expected = PasswordsDoNotMatchException.class)
    public void shouldFailChangingWrongPassword() throws ApplicationException {
        String userNewPassword = "newPwd" + Math.random();
        String userOldPassowrd = "oldPwd" + Math.random();
        long userId = (long) (Math.random() * 100);

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(new PlsMD5Encoder().encodePassword(userOldPassowrd + "1", null));

        Mockito.when(userDao.get(userId)).thenReturn(userEntity);
        Mockito.when(passwordUtils.encodePassword(userNewPassword))
                .thenReturn(new PlsMD5Encoder().encodePassword(userNewPassword, null));
        Mockito.when(passwordUtils.encodePassword(userOldPassowrd))
                .thenReturn(new PlsMD5Encoder().encodePassword(userOldPassowrd, null));

        service.changePassword(userId, userOldPassowrd, userNewPassword);
    }

    @Test
    public void testChangeStatus() throws EntityNotFoundException {
        long userId = (long) (Math.random() * 100);
        long currentUserId = (long) (Math.random() * 100);

        service.changeStatus(userId, UserStatus.ACTIVE, currentUserId);

        Mockito.verify(userDao).changeStatus(userId, UserStatus.ACTIVE, currentUserId);
    }

    @Test
    public void testFindByPersonId() {
        long userId = (long) (Math.random() * 100);
        UserEntity user = new UserEntity();
        Mockito.when(userDao.find(userId)).thenReturn(user);

        UserEntity result = service.findByPersonId(userId);

        Assert.assertSame(user, result);
        Mockito.verify(userDao).find(userId);
    }

    @Test
    public void testGetUserActiveNetworks() {
        long userId = (long) (Math.random() * 100);
        List<SimpleValue> networks = new ArrayList<SimpleValue>();
        networks.add(new SimpleValue(1L, "NETWORK_NAME_1"));
        networks.add(new SimpleValue(2L, "NETWORK_NAME_2"));

        Mockito.when(networkDao.getActiveNetworksByUser(userId)).thenReturn(networks);

        List<SimpleValue> result = service.getUserActiveNetworks(userId);
        Assert.assertSame(networks, result);
        Mockito.verify(networkDao).getActiveNetworksByUser(Mockito.eq(userId));
    }

    @Ignore
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void shouldCreateUser() throws Exception {
        long currentUserId = (long) (Math.random() * 100);
        final long editedUserId = (long) (Math.random() * 100) + 101;
        SecurityTestUtils.login("username", currentUserId);

        final UserEntity user = new UserEntity();
        user.setPassword("AAAAA");

        List<Long> roles = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101);
        List<Long> permissions = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101);

        Mockito.when(userGroupDao.getGroups(currentUserId)).thenReturn(
                Arrays.asList(getUserGroup(roles.get(0)), getUserGroup((long) (Math.random() * 100) + 202), getUserGroup(roles.get(1))));
        Mockito.doThrow(ValidationException.class).doNothing().when(passwordValidator).validate(Mockito.anyString());
        Mockito.when(userDao.saveOrUpdate(user)).thenAnswer(new Answer<UserEntity>() {
            @Override
            public UserEntity answer(InvocationOnMock invocation) throws Throwable {
                user.setId(editedUserId);
                return user;
            }
        });

        String password1 = RandomStringUtils.randomAlphanumeric(16);
        Mockito.when(passwordUtils.encodePassword(RandomStringUtils.randomAlphanumeric(16)))
                .thenReturn(new PlsMD5Encoder().encodePassword(password1, null));

        service.saveUser(user, roles, permissions);

        Mockito.verify(userValidator).validate(user);
        Mockito.verify(userDao).saveOrUpdate(user);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);

        String password = passwordCaptor.getAllValues().get(1);
        Mockito.verify(emailProducer).sendUserRegistredEmail(user, password);
        Mockito.verify(userGroupDao).getGroups(currentUserId);
        Mockito.verify(userGroupDao).getGroups(editedUserId);
        Mockito.verify(userCapDao).getCaps(editedUserId);

        String encodedPassword = passwordEncoder.encodePassword(password, null);
        Assert.assertEquals(encodedPassword, user.getPassword());

        ArgumentCaptor<List> userGroupsCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(userGroupDao).saveAll(userGroupsCaptor.capture());
        List<UserGroupEntity> userGroups = userGroupsCaptor.getValue();
        Assert.assertEquals(2, userGroups.size());
        Assert.assertNotNull(findRole(userGroups, roles.get(0), Status.ACTIVE, editedUserId));
        Assert.assertNotNull(findRole(userGroups, roles.get(1), Status.ACTIVE, editedUserId));
        ArgumentCaptor<List> userCapabilitiesCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(userCapDao).saveAll(userCapabilitiesCaptor.capture());
        List<UserCapabilityEntity> userCapabilities = userCapabilitiesCaptor.getValue();
        Assert.assertEquals(2, userCapabilities.size());
        Assert.assertNotNull(findCapability(userCapabilities, permissions.get(0), Status.ACTIVE, editedUserId));
        Assert.assertNotNull(findCapability(userCapabilities, permissions.get(1), Status.ACTIVE, editedUserId));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void shouldUpdateUser() throws Exception {
        long currentUserId = (long) (Math.random() * 100);
        final long editedUserId = (long) (Math.random() * 100) + 101;
        String[] permissionNames = new String[] { "1permission" + Math.random(), "2permission" + Math.random(), "3permission" + Math.random() };
        SecurityTestUtils.login("username", currentUserId, permissionNames[0], permissionNames[1], permissionNames[2]);

        final UserEntity user = new UserEntity();
        user.setId(editedUserId);

        List<Long> roles = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101, (long) (Math.random() * 100) + 202);
        List<Long> permissions = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101, (long) (Math.random() * 100) + 202);
        long deactivatedRoleId = (long) (Math.random() * 100) + 404;
        long deactivatedPermissionId = (long) (Math.random() * 100) + 303;
        long existingPermissionId = (long) (Math.random() * 100) + 404;

        Mockito.when(userGroupDao.getGroups(currentUserId)).thenReturn(new ArrayList<UserGroupEntity>(Arrays.asList(getUserGroup(roles.get(0)),
                getUserGroup((long) (Math.random() * 100) + 303), getUserGroup(roles.get(1)), getUserGroup(deactivatedRoleId))));
        Mockito.when(userGroupDao.getGroups(editedUserId)).thenReturn(new ArrayList<UserGroupEntity>(
                Arrays.asList(getUserGroup(roles.get(0)), getUserGroup(deactivatedRoleId), getUserGroup(roles.get(2)))));
        Mockito.when(userCapDao.getCaps(editedUserId)).thenReturn(new ArrayList<UserCapabilityEntity>(Arrays.asList(
                getUserCapability(permissions.get(0), permissionNames[0]),
                getUserCapability(deactivatedPermissionId, permissionNames[1]),
                getUserCapability(permissions.get(2), "4permission" + Math.random()),
                        getUserCapability(existingPermissionId, "5permission" + Math.random()))));
        Mockito.doThrow(ValidationException.class).doNothing().when(passwordValidator).validate(Mockito.anyString());

        service.saveUser(user, roles, permissions);

        Mockito.verify(userValidator).validate(user);
        Mockito.verify(userDao).saveOrUpdate(user);
        Mockito.verifyNoMoreInteractions(passwordValidator);
        Mockito.verifyNoMoreInteractions(emailProducer);
        Mockito.verify(userGroupDao).getGroups(currentUserId);
        Mockito.verify(userGroupDao).getGroups(editedUserId);
        Mockito.verify(userCapDao).getCaps(editedUserId);

        ArgumentCaptor<List> userGroupsCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(userGroupDao).saveAll(userGroupsCaptor.capture());
        List<UserGroupEntity> userGroups = userGroupsCaptor.getValue();
        Assert.assertEquals(4, userGroups.size());
        Assert.assertNotNull(findRole(userGroups, roles.get(0), Status.ACTIVE, editedUserId));
        Assert.assertNotNull(findRole(userGroups, roles.get(1), Status.ACTIVE, editedUserId));
        Assert.assertNotNull(findRole(userGroups, roles.get(2), Status.ACTIVE, null));
        Assert.assertNotNull(findRole(userGroups, deactivatedRoleId, Status.INACTIVE, null));
        ArgumentCaptor<List> userCapabilitiesCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(userCapDao).saveAll(userCapabilitiesCaptor.capture());
        List<UserCapabilityEntity> userCapabilities = userCapabilitiesCaptor.getValue();
        Assert.assertEquals(5, userCapabilities.size());
        Assert.assertNotNull(findCapability(userCapabilities, permissions.get(0), Status.ACTIVE, editedUserId));
        Assert.assertNotNull(findCapability(userCapabilities, permissions.get(1), Status.ACTIVE, editedUserId));
        Assert.assertNotNull(findCapability(userCapabilities, permissions.get(2), Status.ACTIVE, editedUserId));
        Assert.assertNotNull(findCapability(userCapabilities, deactivatedPermissionId, Status.INACTIVE, null));
        Assert.assertNotNull(findCapability(userCapabilities, existingPermissionId, Status.ACTIVE, null));
    }

    private UserGroupEntity findRole(List<UserGroupEntity> roles, Long roleId, Status status, Long personId) {
        for (UserGroupEntity role : roles) {
            if (role.getGroupId().equals(roleId) && role.getStatus() == status && ObjectUtils.equals(role.getPersonId(), personId)) {
                return role;
            }
        }
        return null;
    }

    private UserCapabilityEntity findCapability(List<UserCapabilityEntity> capabilities, Long capabilityId, Status status, Long personId) {
        for (UserCapabilityEntity capability : capabilities) {
            if (capability.getCapabilityId().equals(capabilityId) && capability.getStatus() == status
                    && ObjectUtils.equals(capability.getPersonId(), personId)) {
                return capability;
            }
        }
        return null;
    }

    private UserCapabilityEntity getUserCapability(Long capabilityId, String permissionName) {
        UserCapabilityEntity userCapability = new UserCapabilityEntity();
        userCapability.setCapabilityId(capabilityId);
        CapabilityEntity capability = new CapabilityEntity();
        capability.setName(permissionName);
        userCapability.setCapability(capability);
        return userCapability;
    }

    private UserGroupEntity getUserGroup(Long groupId) {
        UserGroupEntity group = new UserGroupEntity();
        group.setGroupId(groupId);
        return group;
    }
}
