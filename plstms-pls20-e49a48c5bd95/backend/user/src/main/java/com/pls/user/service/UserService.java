package com.pls.user.service;

import java.io.IOException;
import java.util.List;

import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.bo.user.UserEmailBO;
import com.pls.core.domain.enums.UserSearchType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.validation.ValidationException;
import com.pls.user.domain.UserCapabilityEntity;
import com.pls.user.domain.UserGroupEntity;
import com.pls.user.domain.UserSettingsEntity;
import com.pls.user.domain.bo.CustomerUserSearchFieldBO;
import com.pls.user.domain.bo.UserCustomerBO;
import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.domain.bo.UserNotificationsBO;
import com.pls.user.service.exc.PasswordsDoNotMatchException;

import freemarker.template.TemplateException;

/**
 * Service to manage {@link UserEntity} information.
 *
 * @author Denis Zhupinsky
 */
public interface UserService {
    /**
     * Change user password if old one corresponds.
     *
     * @param userId
     *            user id.
     * @param oldPassword
     *            Old password.
     * @param newPassword
     *            New password.
     * @throws PasswordsDoNotMatchException
     *             when old password does not match.
     * @throws PasswordsDoNotMatchException
     *             when user not found.
     * @throws EntityNotFoundException
     *             when user not found.
     * @throws ValidationException
     *             when new password has invalid format.
     */
    void changePassword(Long userId, String oldPassword, String newPassword)
            throws PasswordsDoNotMatchException, EntityNotFoundException, ValidationException;

    /**
     * Change status for user.
     *
     * @param personId
     *            Not <code>null</code> {@link UserEntity#getId()}.
     * @param status
     *            Not <code>null</code> {@link UserStatus}.
     * @param currentPersonId
     *            {@link UserEntity#getId()} of current user.
     * @throws EntityNotFoundException
     *             when user not found.
     */
    void changeStatus(Long personId, UserStatus status, Long currentPersonId) throws EntityNotFoundException;

    /**
     * Create new user.
     *
     * @param user
     *            Not <code>null</code> {@link UserEntity}.
     * @param groups
     *            list of groups that should be assigned to user
     * @param capabilities
     *            list of capabilities that should be assigned to user
     * @throws ValidationException
     *             Invalid {@link UserEntity}.
     * @throws IOException
     *             Unable to send email.
     * @throws TemplateException
     *             Unable to send email.
     */
    void saveUser(UserEntity user, List<Long> groups, List<Long> capabilities) throws ValidationException, IOException, TemplateException;

    /**
     * Find {@link UserEntity} by ID.
     *
     * @param personId
     *            user id. Provide null or id <= 0 to get current user.
     * @return Not <code>null</code> {@link UserEntity} if required entity was found. Otherwise returns
     *         <code>null</code>.
     */
    UserEntity findByPersonId(Long personId);

    /**
     * GEt groups.
     *
     * @param personId
     *            presonId
     * @return list
     */
    List<UserCapabilityEntity> getCaps(Long personId);

    /**
     *
     * get groups.
     *
     * @param personId
     *            persn id
     * @return list.
     */
    List<UserGroupEntity> getGroups(Long personId);

    /**
     * Get list of all organizations that can be used as parent for new user by name.
     *
     * @param name
     *            parent organization name
     * @param limit
     *            page size
     * @param currentPersonId
     *            not null ID.
     * @param plsUser
     *            true for PLS user.
     * @return Not null list.
     */
    List<ParentOrganizationBO> getParentOrganizationByName(String name, int limit, Long currentPersonId, boolean plsUser);

    /**
     * Check that specified userId is new and is not used for existed users.
     *
     * @param login
     *            login to check
     * @param personId
     *            Person ID for existed user or null for new user.
     * @return if user with current login already exists
     */
    boolean isValidNewUserId(String login, Long personId);

    /**
     * Find users by parameters.
     *
     * @param currentPersonId - id of current user
     * @param status - user status
     * @param businessUnitId - network id
     * @param allBusinessUnits - search with at least one Business Unit enabled
     * @param company - organization
     * @param searchName - wildcard search name
     * @param searchValue - wildcard search value
     * @return list of filtered users
     */
    List<UserListItemBO> searchUsers(Long currentPersonId, UserStatus status, Long businessUnitId, Boolean allBusinessUnits,
            String company, UserSearchType searchName, String searchValue);

    /**
     * Reset password for user.
     *
     * @param personId
     *            Not <code>null</code> {@link UserEntity#getId()}.
     * @throws Exception
     *             if user id is invalid or user doesn't have valid email or some error occurred while sending
     *             email.
     */
    // TODO change Exception to concrete classes and describes cause of related errors.
    void resetPasword(Long personId) throws Exception;

    /**
     * Find users which satisfy filter value.
     *
     * @param currentPersonId
     *            User ID for search..
     * @param filterValue
     *            Value to filter.
     * @return Not <code>null</code> {@link List}.
     */
    List<UserEmailBO> findUsers(Long currentPersonId, String filterValue);

    /**
     * Find users default notifications.
     *
     * @param customerId
     *            id of the customer.
     * @param locationId
     *            id of user location for which notifications will be searched.
     * @return user notifications.
     */

    List<UserNotificationsBO> getUserDefaultNotifications(Long customerId, Long locationId);

    /**
     * Returns list of customers associated with user and filtered by specified criteria.
     *
     * @param currentUserId
     *            id of logged person associated with customers
     * @param userId
     *            id of edited user
     * @param searchField
     *            field that should be used for search
     * @param criteria
     *            wildcard search criteria
     * @return list of customers
     */
    List<UserCustomerBO> getCustomersAssociatedWithUserByCriteria(Long currentUserId, Long userId, CustomerUserSearchFieldBO searchField,
            String criteria);

    /**
     * Get list of {@link CustomerUserEntity} filtered by customer name.
     *
     * @param userId       id of user
     * @param customerName customer customerName filter
     * @return list of organization users filtered by customer customerName
     */
    List<CustomerUserEntity> getUserCustomersByName(Long userId, String customerName);

    /**
     * Reset password for users by login.
     *
     * @param userId
     *            login
     * @throws Exception
     *             if user not found or some error occurred while sending
     *             email.
     */
    void resetPasword(String userId) throws Exception;

    /**
     * Find {@link UserEntity} by Username and password.
     *
     * @param username
     *            user id.
     * @param password
     *            password of the user
     * @return Not <code>null</code> {@link UserEntity} if required entity was found. Otherwise returns <code>null</code>.
     */
    UserEntity findByUserId(String username, String password);

    /**
     * Find {@link UserEntity} by Username and auth token.
     *
     * @param username
     *            user id.
     * @param authToken
     *            authentication token of the user
     * @return Not <code>null</code> {@link UserEntity} if required entity was found. Otherwise returns <code>null</code>.
     */
    UserEntity findByUserAndToken(String username, String authToken);

    /**
     * Find list {@link SimpleValue} of Networks for user.
     *
     * @param personId
     *            user id.
     * @return list {@link SimpleValue} of Networks.
     */
    List<SimpleValue> getUserActiveNetworks(Long personId);

    /**
     * Check if current user has access to customer location.
     *
     * @param personId
     *            id of user
     * @param customerId
     *            id of customer
     * @param locationId
     *            id of location. can be <code>null</code>.
     * @return <code>true</code> if user has access. <code>false</code> otherwise.
     */
    boolean isUserAssignedToCustomerLocation(Long personId, Long customerId, Long locationId);

    /**
     * Get list of users by specified filter.
     *
     * @param filter
     *            user name filter
     * @param count
     *            max count of returned items
     * @return list of filtered users
     */
    List<SimpleValue> findUsers(String filter, Integer count);

    /**
     * Get list of teams names.
     * 
     * @return teams
     */
    List<SimpleValue> getTeams();

    List<UserSettingsEntity> getUserSettings(Long currentPersonId);

    void saveUserSettings(Long personId, String key, String value);

}
