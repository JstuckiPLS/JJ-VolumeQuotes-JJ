package com.pls.user.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.enums.UserSearchType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.UserEntity;
import com.pls.user.domain.bo.UserCustomerBO;
import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.domain.bo.UserNotificationsBO;

/**
 * DAO for application users.
 *
 * @author Denis Zhupinsky
 */
public interface UserDao extends AbstractDao<UserEntity, Long> {
    /**
     * Change status for specified {@link UserEntity}.
     *
     * @param personId
     *            id of {@link UserEntity}.
     * @param newStatus
     *            Not <code>null</code> {@link UserStatus}.
     * @param currentPersonId
     *            PErsonId that will be used to update modification info.
     */
    void changeStatus(Long personId, UserStatus newStatus, Long currentPersonId);

    /**
     * Check that specified userId is new and is not used for existed users.
     *
     * @param login
     *            user login to check.
     *            @param personId not null for existed user or null for new.
     * @return true if user already exists.
     */
    boolean isValidNewUserId(String login, Long personId);

    /**
     * Returns the amount of the admin users for specified customer.
     *
     * @param customerId
     *            the customer's id
     * @return amount as a {@link Long}.
     */
    Long getAdminUsersCountForCustomer(Long customerId);

    /**
     * Gets the parent user node from user hierarchy.
     *
     * @param userId
     *            id of user whose parent user should be found
     * @return parent User Entity if exists, otherwise null
     */
    UserEntity getParentUser(Long userId);

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
     * Find users by login.
     *
     * @param userId
     *            - login
     * @return User Entity
     */
    UserEntity getUser(String userId);

    /**
     * Find users by parameters.
     *
     * @param currentPersonId
     *            - id of current user
     * @param status
     *            - user status
     * @param businessUnitId
     *            - network id
     * @param allBusinessUnits
     *            - search with at least one Business Unit enabled
     * @param company
     *            - organization
     * @param searchName
     *            - wildcard search name
     * @param searchValue
     *            - wildcard search value
     * @return list of filtered users
     */
    List<UserListItemBO> searchUsers(Long currentPersonId, UserStatus status, Long businessUnitId, Boolean allBusinessUnits,
            String company, UserSearchType searchName, String searchValue);

    /**
     * Returns list of customers associated with user and filtered by specified customer name.
     *
     * @param currentUserId
     *            id of logged person associated with customers
     * @param userId
     *            id of edited user
     * @param customerName
     *            wildcard search customer name criteria
     * @return list of customers
     */
    List<UserCustomerBO> getCustomersAssociatedWithUserByCriteria(Long currentUserId, Long userId, String customerName);

    /**
     * Returns list of customers associated with user and filtered by specified Account Executive name.
     *
     * @param currentUserId
     *            id of logged person associated with customers
     * @param userId
     *            id of edited user
     * @param aeName
     *            wildcard search Account Executive name criteria
     * @return list of customers
     */
    List<UserCustomerBO> getCustomersAssociatedWithUserByAE(Long currentUserId, Long userId, String aeName);

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
     * @param personId
     *            id of current user
     * @param count
     *            max count of returned items
     * @return list of filtered users
     */
    List<UserListItemBO> findUsers(String filter, long personId, int count);
}
