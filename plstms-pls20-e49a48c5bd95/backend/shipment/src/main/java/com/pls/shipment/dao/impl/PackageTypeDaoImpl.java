package com.pls.shipment.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.shipment.dao.PackageTypeDao;
import com.pls.shipment.domain.PackageTypeEntity;

/**
 * Implementation of {@link PackageTypeDao}.
 * 
 * @author Sergey Kirichenko
 */
@Repository
@Transactional
public class PackageTypeDaoImpl implements PackageTypeDao {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public PackageTypeEntity getById(String id) {
        return (PackageTypeEntity) getCurrentSession().get(PackageTypeEntity.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PackageTypeEntity> getAll() {
        Query query = getCurrentSession().getNamedQuery(PackageTypeEntity.GET_ALL_SORT_BY_DESCRIPTION);
        return query.list();
    }
}
