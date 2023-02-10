package com.pls.user.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.user.dao.UserGroupDao;
import com.pls.user.domain.UserGroupEntity;

/**
 * Implementation of {@link UserGroupDao}.
 * 
 * @author Pavani Challa
 */
@Transactional
@Repository
public class UserGroupDaoImpl extends AbstractDaoImpl<UserGroupEntity, Long> implements UserGroupDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<UserGroupEntity> getGroups(Long personId) {
        Query namedQuery = getCurrentSession().getNamedQuery(UserGroupEntity.Q_GET_BY_PERSON_ID);
        namedQuery.setParameter("personId", personId);
        return namedQuery.list();
    }

    /* (non-Javadoc)
     * @see com.pls.user.dao.UserGroupDao#saveAll(java.util.List)
     */
    @Override
    public void saveAll(List<UserGroupEntity> all) {
        for (UserGroupEntity e :  all) {
            getCurrentSession().saveOrUpdate(e);
        }


    }

}
