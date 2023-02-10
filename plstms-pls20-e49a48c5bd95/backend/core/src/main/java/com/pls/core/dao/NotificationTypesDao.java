package com.pls.core.dao;

import com.pls.core.domain.NotificationTypeEntity;

/**
 * DAO for {@link NotificationTypeEntity}.
 *
 * @author Alexander Kirichenko
 */
public interface NotificationTypesDao extends DictionaryDao<NotificationTypeEntity> {

    /**
     * Returns notification type by id.
     * @param id
     *          notification type id
     * @return notification type
     */
    NotificationTypeEntity getNotificationTypesById(String id);
}
