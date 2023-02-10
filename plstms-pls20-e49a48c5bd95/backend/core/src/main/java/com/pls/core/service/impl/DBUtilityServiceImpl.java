package com.pls.core.service.impl;

import com.pls.core.service.DBUtilityService;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link com.pls.core.service.DBUtilityService} implementation.
 *
 * @author Sergey Kirichenko
 */
@Service
public class DBUtilityServiceImpl implements DBUtilityService {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void startCommitMode() {
        sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
    }

    @Override
    public void flushSession() {
        sessionFactory.getCurrentSession().flush();
    }
}
