package com.pls.user.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.CustomerUserDao;
import com.pls.core.dao.NetworkDao;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.bo.user.UserEmailBO;
import com.pls.core.domain.enums.UserSearchType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.PlsMD5Encoder;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.PasswordUtils;
import com.pls.core.service.validation.EmailValidator;
import com.pls.core.service.validation.PasswordValidator;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.Status;
import com.pls.user.dao.TeamDao;
import com.pls.user.dao.UserCapabilitiesDao;
import com.pls.user.dao.UserDao;
import com.pls.user.dao.UserGroupDao;
import com.pls.user.dao.UserSettingsDao;
import com.pls.user.domain.UserCapabilityEntity;
import com.pls.user.domain.UserGroupEntity;
import com.pls.user.domain.UserSettingsEntity;
import com.pls.user.domain.bo.CustomerUserSearchFieldBO;
import com.pls.user.domain.bo.UserCustomerBO;
import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.domain.bo.UserNotificationsBO;
import com.pls.user.service.UserService;
import com.pls.user.service.exc.PasswordsDoNotMatchException;
import com.pls.user.service.impl.email.UserManagementEmailSender;
import com.pls.user.service.validation.UserValidator;

import freemarker.template.TemplateException;

/**
 * {@UserService} implementation.
 *
 * @author Denis Zhupinsky
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private UserManagementEmailSender emailProducer;
    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private OrganizationDao organizationDao;
    private final PlsMD5Encoder passwordEncoder = new PlsMD5Encoder();

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private UserCapabilitiesDao userCapDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private CustomerUserDao customerUserDao;

    @Autowired
    private NetworkDao networkDao;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private TeamDao teamDao;
    @Autowired
    private UserSettingsDao userSettingsDao;

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword)
            throws PasswordsDoNotMatchException, EntityNotFoundException, ValidationException {
        UserEntity userEntity = userDao.get(userId);

        if (!userEntity.getPassword().equals(passwordUtils.encodePassword(oldPassword))) {
            throw new PasswordsDoNotMatchException();
        }
        passwordValidator.validate(newPassword);
        userEntity.setPassword(passwordUtils.encodePassword(newPassword));
        userDao.update(userEntity);
    }

    @Override
    public void changeStatus(Long personId, UserStatus status, Long currentPersonId)
            throws EntityNotFoundException {
        userDao.changeStatus(personId, status, currentPersonId);
    }

    @Override
    public void saveUser(UserEntity user, List<Long> groups, List<Long> capabilities) throws ValidationException, IOException, TemplateException {
        boolean newUser = user.getId() == null;
        String newPassword = null;

        if (newUser) {
            newPassword = passwordUtils.generatePassword();
            user.setPassword(passwordUtils.encodePassword(newPassword));
        }

        userValidator.validate(user);
        userDao.saveOrUpdate(user);

        updateGroups(user.getPersonId(), groups);
        updateCapabilities(user.getPersonId(), capabilities);

        if (newUser) {
            emailProducer.sendUserRegistredEmail(user, newPassword);
        }
    }

    @Override
    public UserEntity findByPersonId(Long personId) {
        return userDao.find(personId);
    }

    @Override
    public List<UserCapabilityEntity> getCaps(Long personId) {
        return userCapDao.getCaps(personId);
    }

    @Override
    public List<UserGroupEntity> getGroups(Long personId) {
        return userGroupDao.getGroups(personId);
    }

    @Override
    public List<ParentOrganizationBO> getParentOrganizationByName(String name, int limit, Long personId, boolean plsUser) {
        ArrayList<ParentOrganizationBO> result = new ArrayList<ParentOrganizationBO>();
        result.addAll(customerDao.findCustomersForUserByName(personId, name, limit));
        if (plsUser && result.size() < limit) {
            result.addAll(organizationDao.getRootOrganizationByName(name, limit - result.size()));
        }
        return result;
    }

    @Override
    public boolean isValidNewUserId(String login, Long personId) {
        return userDao.isValidNewUserId(login, personId);
    }

    @Override
    public List<UserListItemBO> searchUsers(Long currentPersonId, UserStatus status, Long businessUnitId, Boolean allBusinessUnits,
            String company, UserSearchType searchName, String searchValue) {
        return userDao.searchUsers(currentPersonId, status, businessUnitId, allBusinessUnits, company, searchName, searchValue);
    }

    @Override
    public void resetPasword(Long userId) throws EntityNotFoundException, ValidationException, IOException,
    TemplateException {
        UserEntity userEntity = userDao.get(userId);

        emailValidator.validate(userEntity.getEmail());

        String newPassword = passwordUtils.generatePassword();
        userEntity.setPassword(passwordUtils.encodePassword(newPassword));
        userDao.update(userEntity);

        emailProducer.sendPasswordResetEmail(userDao.find(userEntity.getId()), newPassword);
    }

    @Override
    public void resetPasword(String userId) throws Exception {
        UserEntity userEntity = userDao.getUser(userId);
        String newPassword = passwordUtils.generateNewPasswordForUser(userEntity);
        userEntity.setPassword(passwordUtils.encodePassword(newPassword));
        userDao.update(userEntity);

        try {
            emailProducer.sendPasswordResetEmail(userDao.find(userEntity.getId()), newPassword);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApplicationException("Sending failed, try later.");
        }
    }

    @Override
    public List<UserCustomerBO> getCustomersAssociatedWithUserByCriteria(Long currentUserId, Long userId, CustomerUserSearchFieldBO searchField,
            String criteria) {
        if (searchField == CustomerUserSearchFieldBO.ACCOUNT_EXECUTIVE_NAME) {
            return userDao.getCustomersAssociatedWithUserByAE(currentUserId, userId, criteria);
        } else {
            return userDao.getCustomersAssociatedWithUserByCriteria(currentUserId, userId, criteria);
        }
    }

    private void updateCapabilities(Long personId, List<Long> capabilities) {
        List<UserCapabilityEntity> userCapabilities = userCapDao.getCaps(personId);
        Set<String> currentUserCapabilities = SecurityUtils.getCapabilities();
        for (UserCapabilityEntity userCapability : userCapabilities) {
            if (currentUserCapabilities.contains(userCapability.getCapability().getName())
                    && (CollectionUtils.isEmpty(capabilities) || !capabilities.contains(userCapability.getCapabilityId()))) {
                userCapability.setStatus(Status.INACTIVE);
            }
        }
        if (!CollectionUtils.isEmpty(capabilities)) {
            for (Long capabilityId : capabilities) {
                UserCapabilityEntity userCapability = findOrCreateUserCapability(userCapabilities, capabilityId);
                userCapability.setStatus(Status.ACTIVE);
                userCapability.setPersonId(personId);
            }
        }
        userCapDao.saveAll(userCapabilities);
    }

    private UserCapabilityEntity findOrCreateUserCapability(List<UserCapabilityEntity> userCapabilities, Long capabilityId) {
        UserCapabilityEntity userCapability = findUserCapability(userCapabilities, capabilityId);
        if (userCapability == null) {
            userCapability = new UserCapabilityEntity();
            userCapability.setCapabilityId(capabilityId);
            userCapabilities.add(userCapability);
        }
        return userCapability;
    }

    private UserCapabilityEntity findUserCapability(List<UserCapabilityEntity> userCapabilities, Long capabilityId) {
        for (UserCapabilityEntity userCapability : userCapabilities) {
            if (ObjectUtils.equals(capabilityId, userCapability.getCapabilityId())) {
                return userCapability;
            }
        }
        return null;
    }

    private void updateGroups(Long personId, List<Long> groups) {
        List<UserGroupEntity> userGroups = userGroupDao.getGroups(personId);
        List<UserGroupEntity> currentUserGroups = userGroupDao.getGroups(SecurityUtils.getCurrentPersonId());
        for (UserGroupEntity currentUserGroup : currentUserGroups) {
            if (groups == null || !groups.contains(currentUserGroup.getGroupId())) {
                inactivateUserGroup(userGroups, currentUserGroup.getGroupId());
            } else {
                UserGroupEntity userGroup = findOrCreateUserGroup(userGroups, currentUserGroup.getGroupId());
                userGroup.setStatus(Status.ACTIVE);
                userGroup.setPersonId(personId);
            }
        }
        userGroupDao.saveAll(userGroups);
    }

    private UserGroupEntity findOrCreateUserGroup(List<UserGroupEntity> userGroups, Long groupId) {
        UserGroupEntity group = findUserGroup(userGroups, groupId);
        if (group == null) {
            group = new UserGroupEntity();
            group.setGroupId(groupId);
            userGroups.add(group);
        }
        return group;
    }

    private void inactivateUserGroup(List<UserGroupEntity> userGroups, Long groupId) {
        UserGroupEntity userGroup = findUserGroup(userGroups, groupId);
        if (userGroup != null) {
            userGroup.setStatus(Status.INACTIVE);
        }
    }

    private UserGroupEntity findUserGroup(List<UserGroupEntity> userGroups, Long groupId) {
        for (UserGroupEntity userGroup : userGroups) {
            if (ObjectUtils.equals(groupId, userGroup.getGroupId())) {
                return userGroup;
            }
        }
        return null;
    }

    @Override
    public List<UserEmailBO> findUsers(Long currentPersonId, String filterValue) {
        return userInfoDao.findUsers(currentPersonId, filterValue);
    }

    @Override
    public List<UserNotificationsBO> getUserDefaultNotifications(Long customerId, Long locationId) {
        return userDao.getUserDefaultNotifications(customerId, locationId);
    }

    @Override
    public List<CustomerUserEntity> getUserCustomersByName(Long userId, String customerName) {
        return customerUserDao.getByName(userId, customerName);
    }

    @Override
    public UserEntity findByUserId(String username, String password) {
        UserEntity user =  userDao.getUser(username);
        if (user != null && passwordEncoder.isPasswordValid(user.getPassword(), password, null)) {
            return user;
        }

        return null;
    }

    @Override
    public UserEntity findByUserAndToken(String username, String authToken) {
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(authToken)) {
            UserEntity user =  userDao.getUser(username);
            if (user != null && authToken.equals(user.getAuthToken())) {
                return user;
            }
        }

        return null;
    }


    @Override
    public List<SimpleValue> getUserActiveNetworks(Long personId) {
        return networkDao.getActiveNetworksByUser(personId);
    }

    @Override
    public boolean isUserAssignedToCustomerLocation(Long personId, Long customerId, Long locationId) {
        return userDao.isUserAssignedToCustomerLocation(personId, customerId, locationId);
    }

    @Override
    public List<SimpleValue> findUsers(String filter, Integer count) {
        List<UserListItemBO> users = userDao.findUsers(filter, SecurityUtils.getCurrentPersonId(), ObjectUtils.defaultIfNull(count, 10));
        return users.stream().map(u -> new SimpleValue(u.getPersonId(), UserNameBuilder.buildFullName(u.getFirstName(), u.getLastName())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SimpleValue> getTeams() {
        return teamDao.getNames();
    }

    @Override
    public List<UserSettingsEntity> getUserSettings(Long personId) {
        return userSettingsDao.getByPersonId(personId);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveUserSettings(Long personId, String key, String value) {
        UserSettingsEntity userSettings = userSettingsDao.getUserSettingsByPersonIdAndKey(personId, key);
        if(userSettings == null) {
            userSettings = new UserSettingsEntity();
            userSettings.setPersonId(personId);
            userSettings.setKey(key);
        }
        userSettings.setValue(value);
        userSettingsDao.saveOrUpdate(userSettings);
    }

}
