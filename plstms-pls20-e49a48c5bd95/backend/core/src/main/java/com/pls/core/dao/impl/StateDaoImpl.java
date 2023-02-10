package com.pls.core.dao.impl;

import com.pls.core.dao.StateDao;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link com.pls.core.dao.StateDao}.
 *
 * @author Artem Arapov
 */
@Repository
@Transactional
public class StateDaoImpl implements StateDao {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public StateEntity getState(StatePK statePK) {
        return (StateEntity) sessionFactory.getCurrentSession().get(StateEntity.class, statePK);
    }

    @Override
    public StateEntity getState(String stateCode, String countryCode) {
        StatePK statePK = new StatePK();
        statePK.setCountryCode(countryCode);
        statePK.setStateCode(stateCode);
        return getState(statePK);
    }
}
