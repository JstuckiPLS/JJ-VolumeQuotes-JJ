package com.pls.core.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.type.BasicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.Identifiable;
import com.pls.core.exception.EntityNotFoundException;

/**
 * {@link com.pls.core.dao.AbstractDao} implementation.
 * 
 * @param <Type>
 *            type of entity.
 * @param <IdType>
 *            type of entity id.
 * 
 * @author Gleb Zgonikov
 */
@SuppressWarnings("unchecked")
@Transactional
public abstract class AbstractDaoImpl<Type extends Identifiable<IdType>, IdType extends Serializable> implements AbstractDao<Type, IdType> {
    /**
     * The biggest recommended values. Same as specified at hibernate properties as hibernate.jdbc.batch_size.
     */
    @Value("${hibernate.jdbc.batch_size}")
    private int jdbcBatchSize;

    @Autowired
    private SessionFactory sessionFactory;

    private final Class<Type> typeClass;

    private final String selectAllQuery;

    private final String selectAllByIdsQuery;

    /**
     * Default constructor.
     */
    public AbstractDaoImpl() {
        Object clazz = getClass().getGenericSuperclass();
        while (!(clazz instanceof ParameterizedType)) {
            clazz = ((Class<?>) clazz).getGenericSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) clazz;

        this.typeClass = (Class<Type>) parameterizedType.getActualTypeArguments()[0];

        this.selectAllQuery = "select t from " + getTypeClass().getSimpleName() + " t";
        this.selectAllByIdsQuery = "select t from " + getTypeClass().getSimpleName() + " t where id in :ids";
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void persist(Type entity) {
        getCurrentSession().persist(entity);
    }

    @Override
    public void persistBatch(Collection<Type> entities) {
        Session session = null;
        try {
            session = getCurrentSession();
            int i = 0;
            for (Type entity : entities) {
                session.persist(entity);

                i++;
                if (i % jdbcBatchSize == 0) {
                    // flush a batch of inserts and release memory:
                    session.flush();
                    session.clear();
                }
            }
        } finally {
            if (session != null) {
                session.flush();
                session.clear();
            }
        }

    }

    @Override
    public Type merge(Type entity) {
        return (Type) getCurrentSession().merge(entity);
    }

    @Override
    public Type saveOrUpdate(Type entity) {
        getCurrentSession().saveOrUpdate(entity);
        return  entity;
    }

    @Override
    public void refresh(Type entity) {
        getCurrentSession().refresh(entity);
    }

    @Override
    public void saveOrUpdateBatch(Collection<Type> entities) {
        Session session = null;
        try {
            session = getCurrentSession();
            int i = 0;
            for (Type entity : entities) {
                session.saveOrUpdate(entity);

                i++;
                if (i % jdbcBatchSize == 0) {
                    // flush a batch of inserts and release memory:
                    session.flush();
                    session.clear();
                }
            }
        } finally {
            if (session != null) {
                session.flush();
                session.clear();
            }
        }
    }

    @Override
    public Type find(IdType id) {
        Type result = null;
        if (id != null) {
            result = (Type) getCurrentSession().get(getTypeClass(), id);
        }
        return result;
    }

    @Override
    public void evict(Type entity) {
        getCurrentSession().evict(entity);
    }

    @Override
    public Type update(Type entity) {
        return (Type) getCurrentSession().merge(entity);
    }

    @Override
    public List<Type> getAll() {
        return getCurrentSession().createQuery(selectAllQuery).list();
    }

    @Override
    public List<Type> getAll(List<IdType> ids) {
        List<Type> result = Collections.emptyList();
        if (ids != null && !ids.isEmpty()) {
            result = getCurrentSession().createQuery(selectAllByIdsQuery).setParameterList("ids", ids).list();
        }
        return result;
    }

    @Override
    public List<Type> getAll(List<IdType> ids, boolean ignoreMissing) throws EntityNotFoundException {
        List<Type> result = Collections.emptyList();
        if (ids != null && !ids.isEmpty()) {
            result = getCurrentSession().createQuery(selectAllByIdsQuery)
                    .setParameterList("ids", ids).list();

            if (!ignoreMissing && result.size() != ids.size()) {
                throw new EntityNotFoundException("Requested " + ids.size() + " " + getTypeClass() + " objects, "
                        + " but found only " + result.size());
            }
        }
        return result;
    }

    /**
     * Create criteria.
     * 
     * @return criteria.
     */
    protected Criteria getCriteria() {
        return getCurrentSession().createCriteria(getTypeClass());
    }

    @Override
    public Type get(IdType id) throws EntityNotFoundException {
        Type result = (Type) getCurrentSession().get(getTypeClass(), id);
        if (result == null) {
            throw new EntityNotFoundException(getTypeClass(), id);
        } else {
            return result;
        }
    }

    /**
     * Create query returning total count.
     * 
     * @return query returning total count.
     */
    protected Criteria getCountCriteria() {
        return getCurrentSession().createCriteria(getTypeClass())
                .setProjection(Projections.rowCount());
    }

    /**
     * Execute given named query.
     *
     * @param namedQueryName query name to execute.
     * @return list of found entities.
     */
    protected List<Type> findByNamedQuery(String namedQueryName) {
        return getCurrentSession().getNamedQuery(namedQueryName).list();
    }

    /**
     * Execute given named query.
     *
     * @param namedQueryName query name to execute.
     * @param parameters named parameters for the query.
     * @return list of found entities.
     */
    protected List<Type> findByNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        return findByNamedQuery(namedQueryName, parameters, 0);
    }

    /**
     * Find objects by given named query and return list of object specified as generic parameter.
     *
     * @param queryName query name to execute.
     * @param <RESULT> type of object that will be returned
     * @return list of found entities.
     */
    protected <RESULT> List<? extends RESULT> findObjectsByNamedQuery(String queryName) {
        return findObjectsByNamedQuery(queryName, null);
    }

    /**
     *  Find objects by given named query and return list of object specified as generic parameter.
     *
     * @param queryName query name to execute.
     * @param parameters named parameters for the query.
     * @param <RESULT> type of object that will be returned
     * @return list of found entities.
     */
    protected <RESULT> List<? extends RESULT> findObjectsByNamedQuery(String queryName, Map<String, Object> parameters) {
        Query query = getCurrentSession().getNamedQuery(queryName);
        if (parameters != null && !parameters.isEmpty()) {
            Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();

            for (Map.Entry<String, Object> entry : rawParameters) {
                setNamedQueryParameter(query, entry);
            }
        }

        return query.list();
    }

    /**
     *  Find unique objects by given named query and return list of object specified as generic parameter.
     *
     * @param queryName query name to execute.
     * @param parameters named parameters for the query.
     * @param <RESULT> type of object that will be returned
     * @return list of found entities.
     */
    protected <RESULT extends Identifiable<? extends Serializable>> List<RESULT> findUniqueObjectsByNamedQuery(String queryName,
            Map<String, Object> parameters) {
        Query query = getCurrentSession().getNamedQuery(queryName);
        if (parameters != null && !parameters.isEmpty()) {
            Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();

            for (Map.Entry<String, Object> entry : rawParameters) {
                setNamedQueryParameter(query, entry);
            }
        }

        List<RESULT> resultList = query.list();
        Set<Object> ids = new HashSet<Object>();
        for (Iterator<RESULT> iterator = resultList.iterator(); iterator.hasNext();) {
            RESULT entity = iterator.next();
            if (!ids.add(entity.getId())) {
                iterator.remove();
            }
        }
        return resultList;
    }

    /**
     * Execute given named query.
     *
     * @param queryName query name to execute.
     * @param <RESULT> type of object that will be returned
     * @param parameters map of parameters that need to be applied to query
     * @return Result value.
     */
    protected <RESULT> RESULT findUniqueObjectByNamedQuery(String queryName, Map<String, Object> parameters) {
        Query query = getCurrentSession().getNamedQuery(queryName);

        if (parameters != null && !parameters.isEmpty()) {
            Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
            for (Map.Entry<String, Object> entry : rawParameters) {
                setNamedQueryParameter(query, entry);
            }
        }

        return (RESULT) query.uniqueResult();
    }

    /**
     * Execute given named query.
     *
     * @param queryName query name to execute.
     * @param <RESULT> type of object that will be returned
     * @return Result value.
     */
    protected <RESULT> RESULT findUniqueObjectByNamedQuery(String queryName) {
        return findUniqueObjectByNamedQuery(queryName, null);
    }

    /**
     * Execute given named query.
     *
     * @param namedQueryName query name to execute.
     * @param parameters named parameters for the query.
     * @param resultLimit maximal number of entities to fetch.
     * @return list of found entities.
     */
    protected List<Type> findByNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
        Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = getCurrentSession().getNamedQuery(namedQueryName);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Map.Entry<String, Object> entry : rawParameters) {
            setNamedQueryParameter(query, entry);
        }
        return query.list();
    }

    /**
     *  Execute update for specified name query.
     *
     * @param queryName name of name query
     * @param parameters query parameters
     */
    protected void executeNamedQueryUpdate(String queryName, Map<String, Object> parameters) {
        Query query = getCurrentSession().getNamedQuery(queryName);

        if (parameters != null && !parameters.isEmpty()) {
            Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
            for (Map.Entry<String, Object> entry : rawParameters) {
                setNamedQueryParameter(query, entry);
            }
        }

        query.executeUpdate();
    }

    private void setNamedQueryParameter(Query query, Map.Entry<String, Object> entry) {
        Object value = entry.getValue();
        if ((value instanceof Collection)) {
            query.setParameterList(entry.getKey(), (Collection<?>) value);
        } else if (value instanceof Object[]) {
            query.setParameterList(entry.getKey(), (Object[]) value);
        } else if (value != null && value.getClass().isArray() && value.getClass().getComponentType().isPrimitive()) {
            int arrLength = Array.getLength(value);
            Object[] paramsArr = new Object[arrLength];
            for (int i = 0; i < arrLength; i++) {
                paramsArr[i] = Array.get(value, i);
            }
            query.setParameterList(entry.getKey(), paramsArr);
        } else if (value instanceof BasicType) {
            query.setParameter(entry.getKey(), null, (BasicType) value);
        } else {
            query.setParameter(entry.getKey(), value);
        }
    }

    protected Class<Type> getTypeClass() {
        return typeClass;
    }
}
