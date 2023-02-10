package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.bo.user.UserEmailBO;
import com.pls.core.domain.bo.user.UserInfoBO;
import com.pls.core.domain.bo.user.UserLoginBO;
import com.pls.core.domain.user.UserEntity;

/**
 * DAO to obtain basic user related information.
 * 
 * @author Maxim Medvedev
 */
public interface UserInfoDao {

    /**
     * Find user by login.
     * 
     * @param login
     *            {@link UserEntity#getUserId()} value.
     * @return Not <code>null</code> {@link UserLoginBO} with specified login or <code>null</code> if user was
     *         not found.
     */
    UserLoginBO findByLogin(String login);

    /**
     * Get User Information by personId.
     * 
     * @param personId
     *            user ID
     * @return {@link UserInfoBO} with specified personId or <code>null</code> if user was not found.
     */
    UserInfoBO getByPersonId(Long personId);

    /**
     * Gets {@link UserEntity} by Id.
     * 
     * @param personId
     *            ID of User to find.
     * @return {@link UserEntity}
     */
    UserEntity getUserEntityById(Long personId);

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
}
