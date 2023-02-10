package com.pls.core.dao.impl;

import com.pls.core.dao.NotificationTypesDao;
import com.pls.core.domain.NotificationTypeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO implementation for {@link NotificationTypeEntity}.
 *
 * @author Alexander Kirichenko
 */
@Repository
@Transactional
public class NotificationTypesDaoImpl extends DictionaryDaoImpl<NotificationTypeEntity> implements NotificationTypesDao {

    @Override
    public NotificationTypeEntity getNotificationTypesById(String id) {
        return (NotificationTypeEntity) getCurrentSession().get(NotificationTypeEntity.class, id);
    }
}
