package com.pls.user.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.user.dao.UserCapabilitiesDao;
import com.pls.user.domain.UserCapabilityEntity;

/**
 * DAO.
 * @author Maxim Medvedev
 *
 */
@Transactional
@Repository
class UserCapabilitiesDaoImpl extends AbstractDaoImpl<UserCapabilityEntity, Long>  implements UserCapabilitiesDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<UserCapabilityEntity> getCaps(Long personId) {
            Query namedQuery = getCurrentSession().getNamedQuery(UserCapabilityEntity.Q_GET_BY_PERSON_ID);
            namedQuery.setParameter("personId", personId);
            return namedQuery.list();
    }

    @Override
    public void saveAll(List<UserCapabilityEntity> all) {
        for (UserCapabilityEntity e :  all) {
            getCurrentSession().saveOrUpdate(e);
        }
    }

}
