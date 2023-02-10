package com.pls.user.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.user.dao.UserSettingsDao;
import com.pls.user.domain.UserSettingsEntity;


@Repository
public class UserSettingsDaoImpl extends AbstractDaoImpl<UserSettingsEntity, Long> implements UserSettingsDao {

    @Override
    public List<UserSettingsEntity> getByPersonId(Long personId) {
        Query query = getCurrentSession().getNamedQuery(UserSettingsEntity.Q_GET_BY_PERSON_ID);
        query.setParameter("personId", personId);
        return (List<UserSettingsEntity>) query.list();
    }

    @Override
    public UserSettingsEntity getUserSettingsByPersonIdAndKey(Long personId, String key) {
        Query query = getCurrentSession().getNamedQuery(UserSettingsEntity.Q_GET_BY_PERSON_ID_AND_KEY);
        query.setParameter("personId", personId);
        query.setParameter("key", key);
        return (UserSettingsEntity) query.uniqueResult();
    }
    
    
}
