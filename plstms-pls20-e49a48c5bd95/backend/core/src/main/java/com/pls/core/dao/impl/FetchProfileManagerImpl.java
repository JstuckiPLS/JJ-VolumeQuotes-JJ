package com.pls.core.dao.impl;

import com.pls.core.dao.FetchProfileManager;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link com.pls.core.dao.FetchProfileManager}.
 * 
 * @author Viacheslav Krot
 * 
 */
@Repository
@Transactional
public class FetchProfileManagerImpl implements FetchProfileManager {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void enableFetchProfile(String profileName) {
        sessionFactory.getCurrentSession().enableFetchProfile(profileName);
    }
}
