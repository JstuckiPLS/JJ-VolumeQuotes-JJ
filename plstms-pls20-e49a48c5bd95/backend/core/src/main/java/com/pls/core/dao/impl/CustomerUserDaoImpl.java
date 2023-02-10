package com.pls.core.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CustomerUserDao;
import com.pls.core.domain.user.CustomerUserEntity;

/**
 * {@link CustomerUserDao} implementation.
 * 
 * @author Denis Zhupinsky (Team International)
 */
@Transactional
@Repository
public class CustomerUserDaoImpl extends AbstractDaoImpl<CustomerUserEntity, Long> implements
        CustomerUserDao {
    @Override
    @SuppressWarnings("unchecked")
    public List<CustomerUserEntity> getActive(Long customerId) {
        Query query = getCurrentSession().getNamedQuery(CustomerUserEntity.Q_LIST_ACTIVE_BY_CUSTOMER_ID);
        query.setParameter("orgId", customerId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerUserEntity> getByName(Long userId, String customerName) {
        String nameCriteria = customerName != null ? customerName.replace("*", "%") : null;
        Query query = getCurrentSession().getNamedQuery(CustomerUserEntity.Q_GET_USER_CUSTOMERS_BY_NAME);
        query.setParameter("userId", userId);
        query.setParameter("customerName", nameCriteria);
        return query.list();
    }

    @Override
    public CustomerUserEntity findByPersonIdOrgIdLocationId(Long personId, Long orgId) {
        Query query = getCurrentSession().getNamedQuery(CustomerUserEntity.Q_GET_BY_PERSON_ID_AND_ORG_ID);
        query.setLong("personId", personId);
        query.setLong("orgId", orgId);
        return (CustomerUserEntity) query.uniqueResult();
    }
}
