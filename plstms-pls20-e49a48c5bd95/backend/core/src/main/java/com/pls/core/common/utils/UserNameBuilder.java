package com.pls.core.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.user.UserEntity;

/**
 * Contains rules about how to build full name using users field.
 * 
 * @author Maxim Medvedev
 */
public final class UserNameBuilder {
    /**
     * Prepare user's full name.
     * 
     * @param firstName
     *            The first name.
     * 
     * @param lastName
     *            The last name.
     * @return Not <code>null</code> {@link String}.
     */
    public static String buildFullName(String firstName, String lastName) {
        String delimeter = StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName) ? " "
                : StringUtils.EMPTY;
        return StringUtils.trimToEmpty(firstName) + delimeter + StringUtils.trimToEmpty(lastName);
    }

    /**
     * Prepare user's full name.
     * 
     * @param user
     *            Normally should be not <code>null</code> {@link UserEntity}.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    public static String buildFullName(UserEntity user) {
        String result = StringUtils.EMPTY;
        if (user != null) {
            result = UserNameBuilder.buildFullName(user.getFirstName(), user.getLastName());
        }
        return result;
    }

    private UserNameBuilder() {
    }
}
