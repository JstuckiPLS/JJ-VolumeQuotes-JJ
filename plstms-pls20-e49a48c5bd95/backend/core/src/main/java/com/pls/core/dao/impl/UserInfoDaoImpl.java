package com.pls.core.dao.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.bo.user.UserEmailBO;
import com.pls.core.domain.bo.user.UserInfoBO;
import com.pls.core.domain.bo.user.UserLoginBO;
import com.pls.core.domain.user.UserEntity;

/**
 * Implementation of {@link UserInfoDao} interface.
 * 
 * @author Maxim Medvedev
 */
@Transactional
@Repository
public class UserInfoDaoImpl implements UserInfoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public UserInfoBO getByPersonId(Long personId) {
        return (UserInfoBO) sessionFactory.getCurrentSession().getNamedQuery(UserEntity.Q_FIND_USER_INFO).setLong("personId", personId)
                .setResultTransformer(new AliasToBeanResultTransformer(UserInfoBO.class)).uniqueResult();
    }

    @Override
    public UserLoginBO findByLogin(String login) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(UserEntity.Q_FIND_BY_USER_ID);
        query.setParameter("userId", StringUtils.trimToEmpty(login).toUpperCase(Locale.ENGLISH));
        query.setFetchSize(1);
        query.setResultTransformer(new AliasToBeanResultTransformer(UserLoginBO.class));
        return (UserLoginBO) query.uniqueResult();
    }

    @Override
    public UserEntity getUserEntityById(Long personId) {
        return (UserEntity) sessionFactory.getCurrentSession().get(UserEntity.class, personId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserEmailBO> findUsers(Long currentPersonId, String filterValue) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(UserEntity.Q_GET_EMAIL_BY_FILTER_VALUE);
        query.setParameter("filterValue", "%" + StringUtils.upperCase(filterValue) + "%");
        query.setParameter("personId", currentPersonId);
        query.setResultTransformer(new AliasToBeanResultTransformer(UserEmailBO.class));
        return query.setMaxResults(10).list();
    }
}
