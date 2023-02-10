package com.pls.core.dao.impl;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.SecurityDao;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.EntityNotFoundException;

/**
 * Implementation for {@link SecurityDao} interface.
 * 
 * @author Maxim Medvedev
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class SecurityDaoImpl implements SecurityDao, InitializingBean {
    public static final long DEFAULT_PLS_ORG_ID = 38941L;

    private Long plsRootOrganization = DEFAULT_PLS_ORG_ID;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        Session session = sessionFactory.openSession();
        try {
            Query query = session.getNamedQuery(OrganizationEntity.Q_GET_PLS_PRO_ORGANIZATION);
            Number result = (Number) query.uniqueResult();
            if (result != null) {
                plsRootOrganization = result.longValue();
            } else {
                throw new EntityNotFoundException("PLS PRO Organization was not found!");
            }
        } finally {
            session.close();
        }
    }

    @Override
    public boolean isPlsUser(Long parentOrgId) {
        return plsRootOrganization.equals(parentOrgId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> loadCapabilities(Long personId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(UserEntity.Q_LOAD_CAPABILITIES);
        query.setParameter("personId", personId, LongType.INSTANCE);
        List<String> list = query.list();
        return new HashSet<String>(list);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> loadOrganizations(Long personId) {
        Set<Long> result = new HashSet<Long>();
        if (personId != null) {
            Query query = sessionFactory.getCurrentSession().getNamedQuery(UserEntity.Q_LOAD_ORGANIZATIONS);
            query.setLong("personId", personId);
            List<Number> list = query.list();
            for (Number id : list) {
                if (id != null) {
                    result.add(id.longValue());
                }
            }
        }
        return result;
    }

    @Override
    public boolean isCustomerAssignedThroughNetwork(Long personId, Long customerId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(UserEntity.Q_USER_NETWORK_CUSTOMER);
        query.setLong("personId", personId);
        query.setLong("customerId", customerId);
        return query.uniqueResult() != null;
    }

    @Override
    public void saveLastLoginDateByPersonId(Long currentPersonId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(UserEntity.Q_SAVE_LAST_LOGIN_DATE_BY_USER_ID);
        query.setDate("newDate", new Date());
        query.setLong("personId", currentPersonId);
        query.executeUpdate();
    }
}
