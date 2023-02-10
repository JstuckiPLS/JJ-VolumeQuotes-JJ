package com.pls.user.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.enums.UserSearchType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.user.dao.UserDao;
import com.pls.user.domain.bo.UserCustomerBO;
import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.domain.bo.UserNotificationsBO;


/**
 * {@link com.pls.user.dao.UserDao} implementation.
 *
 * @author Denis Zhupinsky
 * @author Alexander Balan
 */
@Repository
@Transactional
public class UserDaoImpl extends AbstractDaoImpl<UserEntity, Long> implements UserDao {
    @Value("${email.from}")
    private String defaultEmail;

    @Override
    public UserEntity saveOrUpdate(UserEntity entity) {
        if (entity.getId() == null) {
            entity.setAuthToken(RandomStringUtils.randomAlphanumeric(12));
        }
        UserEntity result = super.saveOrUpdate(entity);
        getCurrentSession().flush();
        return result;
    }

    @Override
    public boolean isValidNewUserId(String userId, Long personId) {
        boolean result = true;
        if (StringUtils.isNotBlank(userId)) {
            Query query = getCurrentSession().getNamedQuery(UserEntity.Q_GET_ID_BY_LOGIN);
            query.setParameter("userId", StringUtils.trimToEmpty(userId));
            Number exustedPersonId = (Number) query.uniqueResult();
            if (personId != null && exustedPersonId != null) {
                result = personId.equals(exustedPersonId.longValue());
            } else if (personId == null && exustedPersonId != null) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public UserEntity getParentUser(Long userId) {
        Query query = getCurrentSession().getNamedQuery(UserEntity.Q_PARENT);
        query.setParameter("userId", userId);
        return (UserEntity) query.uniqueResult();
    }

    @Override
    public UserEntity getUser(String userId) {
        Query query = getCurrentSession().getNamedQuery(UserEntity.Q_GET_USERS_BY_LOGIN);
        query.setParameter("userId", userId);
        return (UserEntity) query.uniqueResult();
    }

    @Override
    public Long getAdminUsersCountForCustomer(Long customerId) {
        return (Long) getCurrentSession().getNamedQuery(UserEntity.Q_COUNT_ADMIN_USERS_BY_CUSTOMER)
                .setLong("organizationId", customerId).uniqueResult();
    }

    @Override
    public void changeStatus(Long personId, UserStatus newStatus, Long currentPersonId) {
        if (personId != null && newStatus != null && currentPersonId != null) {
            Query query = getCurrentSession().getNamedQuery(UserEntity.Q_UPDATE_USER_STATUS);
            query.setParameter("status", newStatus);
            query.setLong("personId", personId);
            query.setLong("modifiedBy", currentPersonId);
            query.executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    @Override

    public List<UserNotificationsBO> getUserDefaultNotifications(Long customerId, Long locationId) {
        String defaultEmail = getDefaultEmail();

        Query query = getCurrentSession().getNamedQuery(UserEntity.Q_GET_USERS_NOTIFICATIONS).setString("defaultEmail", defaultEmail);
        query.setLong("customerId", customerId);
        query.setLong("locationId", locationId);
        query.setResultTransformer(new NotificationsResultTransformer());
        return query.list();
    }

    private String getDefaultEmail() {
        Pattern p = Pattern.compile("^.*<(.*)>.*$");
        Matcher m = p.matcher(defaultEmail);
        String trimmedEmail = defaultEmail;
        if (m.matches()) {
            trimmedEmail = m.group(1);
        }
        return trimmedEmail;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserListItemBO> searchUsers(Long personId, UserStatus status,
            Long businessUnitId, Boolean allBusinessUnits, String company, UserSearchType searchName, String searchValue) {
        return getCurrentSession().getNamedQuery(UserEntity.Q_SEARCH_USERS)
                .setLong("personId", personId)
                .setParameter("status", status)
                .setParameter("businessUnitId", businessUnitId, LongType.INSTANCE)
                .setBoolean("allBusinessUnits", allBusinessUnits != null && allBusinessUnits)
                .setParameter("company", StringUtils.upperCase(company), StringType.INSTANCE)
                .setString("searchName", searchName.name())
                .setParameter("searchValue", StringUtils.upperCase(searchValue), StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(UserListItemBO.class)).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserCustomerBO> getCustomersAssociatedWithUserByCriteria(Long currentUserId, Long userId, String customerName) {
        return getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_ASSOCIATED_UNASSIGNED_BY_NAME)
                .setLong("currentUserId", currentUserId)
                .setLong("userId", userId == null ? Long.MIN_VALUE : userId)
                .setString("criteria", StringUtils.upperCase(customerName))
                .setResultTransformer(new AliasToBeanResultTransformer(UserCustomerBO.class))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserCustomerBO> getCustomersAssociatedWithUserByAE(Long currentUserId, Long userId, String aeName) {
        return getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_ASSOCIATED_UNASSIGNED_BY_AE)
                .setLong("currentUserId", currentUserId)
                .setLong("userId", userId == null ? Long.MIN_VALUE : userId)
                .setString("criteria", StringUtils.upperCase(aeName))
                .setResultTransformer(new AliasToBeanResultTransformer(UserCustomerBO.class))
                .list();
    }

    @Override
    public boolean isUserAssignedToCustomerLocation(Long personId, Long customerId, Long locationId) {
        return getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_ASSIGNED_TO_USER)
                .setLong("personId", personId)
                .setLong("customerId", customerId)
                .setParameter("locationId", locationId, LongType.INSTANCE)
                .setMaxResults(1)
                .uniqueResult() != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserListItemBO> findUsers(String filter, long personId, int count) {
        return getCurrentSession().getNamedQuery(UserEntity.Q_SEARCH_USERS)
                .setLong("personId", personId)
                .setParameter("status", UserStatus.ACTIVE)
                .setParameter("businessUnitId", null, LongType.INSTANCE)
                .setBoolean("allBusinessUnits", false)
                .setParameter("company", null, StringType.INSTANCE)
                .setString("searchName", UserSearchType.NAME.name())
                .setString("searchValue", "%" + StringUtils.upperCase(filter) + "%")
                .setResultTransformer(Transformers.aliasToBean(UserListItemBO.class))
                .setMaxResults(count)
                .list();
    }

    private final class NotificationsResultTransformer extends BasicTransformerAdapter {
        private static final long serialVersionUID = 3149246978375154896L;

        Map<Long, UserNotificationsBO> notifications = new HashMap<Long, UserNotificationsBO>();

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            Long userId = (Long) tuple[0];
            UserNotificationsBO bo = null;
            if (notifications.containsKey(userId)) {
                notifications.get(userId).getNotifications().add((String) tuple[2]);
            } else {
                bo = new UserNotificationsBO();
                bo.setUserId(userId);
                bo.setEmail((String) tuple[1]);
                bo.getNotifications().add((String) tuple[2]);
                notifications.put(userId, bo);
            }
            return bo;
        }

        @Override
        public List<UserNotificationsBO> transformList(@SuppressWarnings("rawtypes") List list) {
            return new ArrayList<UserNotificationsBO>(notifications.values());
        }
    }
}
