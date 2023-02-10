package com.pls.core.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.DictionaryDao;

/**
 * {@link com.pls.core.dao.DictionaryDao} implementation.
 *
 * @param <Type> type of entity.
 *
 * @author Sergey Kirichenko
 */
public abstract class DictionaryDaoImpl<Type> implements DictionaryDao<Type> {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    private final String selectAllQuery;

    /**
     * Default constructor.
     */
    protected DictionaryDaoImpl() {
        Object clazz = getClass().getGenericSuperclass();
        while (!(clazz instanceof ParameterizedType)) {
            clazz = ((Class<?>) clazz).getGenericSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) clazz;

        @SuppressWarnings("unchecked")
        Class<Type> typeClass = (Class<Type>) parameterizedType.getActualTypeArguments()[0];

        selectAllQuery = "select t from " + typeClass.getSimpleName() + " t";
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Type> getAll() {
        return getCurrentSession().createQuery(selectAllQuery).list();
    }
}

