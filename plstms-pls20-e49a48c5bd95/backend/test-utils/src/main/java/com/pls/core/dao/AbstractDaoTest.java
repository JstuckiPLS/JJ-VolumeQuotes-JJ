package com.pls.core.dao;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the base class for all DAO integration test. Opens and rolls back transaction for each test
 * execution automatically.
 * 
 * @author Alexander Balan (TEAM International)
 * 
 */
@Transactional
@Rollback
@ContextConfiguration({ "classpath*:spring/testContext.xml", "classpath*:spring/dao-*.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractDaoTest {

    private static final String UPDATE_ORG_STATUS = " UPDATE FLATBED.ORGANIZATIONS "
            + "SET STATUS = :status WHERE ORG_ID = :orgId";

    @Autowired
    private DataSource dataSource;

    /**
     * SessionFactory for interaction with database.
     */
    @Autowired
    private SessionFactory sessionFactory;

    protected void executeScript(String relativeFileName) {
        ResourceLoader resourceLoader = new ClassRelativeResourceLoader(getClass());
        String scriptName = getClass().getSimpleName() + "." + relativeFileName;
        Resource resource = resourceLoader.getResource(scriptName);
        Assert.assertTrue("'" + scriptName + "' was not found!", resource.exists());

        ScriptUtils.executeSqlScript(DataSourceUtils.getConnection(dataSource), new EncodedResource(resource, "UTF-8"));
    }

    /**
     * Calls {@link Session#flush()} and {@link Session#clear()} for current session.
     */
    protected void flushAndClearSession() {
        getSession().flush();
        getSession().clear();
    }

    /**
     * Get entity with specified ID. This method fails test if required entity was not found.
     * 
     * @param entityClass
     *            Not <code>null</code> {@link Class} of required entity.
     * @param entityId
     *            Not <code>null</code> ID of required entity.
     * 
     * @return Not <code>null</code> entity.
     * 
     * @param <T>
     *            Type of required entity.
     */
    protected <T> T getEntity(Class<T> entityClass, Serializable entityId) {
        Assert.assertNotNull("Entity class should be not null", entityClass);
        Assert.assertNotNull("Entity ID should be not null", entityId);
        Object resultObject = getSession().get(entityClass, entityId);
        Assert.assertNotNull(entityClass.getSimpleName() + " with ID='" + entityId + "' was not found",
                resultObject);
        return entityClass.cast(resultObject);
    }

    /**
     * Get current {@link Session}.
     * 
     * @return Not <code>null</code> {@link Session}.
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Load all entities.
     * 
     * @param entityClass
     *            Not <code>null</code> {@link Class}.
     * 
     * @return Not <code>null</code> {@link List}.
     * @param <T>
     *            Type of target entity.
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> loadAll(Class<T> entityClass) {
        return getSession().createCriteria(entityClass).list();
    }

    /**
     * Load the first N entities.
     * 
     * @param entityClass
     *            Not <code>null</code> {@link Class}.
     * @param maxResults
     *            maximum loaded entities.
     * 
     * @return Not <code>null</code> {@link List}.
     * @param <T>
     *            Type of target entity.
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> loadFirst(Class<T> entityClass, int maxResults) {
        return getSession().createCriteria(entityClass).setMaxResults(maxResults).list();
    }

    /**
     * Save entity and flush session data.
     * 
     * @param entities
     *            Not <code>null</code> entities.
     */
    protected void save(Object... entities) {
        for (Object entity : entities) {
            getSession().saveOrUpdate(entity);
        }
        flushAndClearSession();
    }

    protected Resource loadResource(String name) {
        ResourceLoader resourceLoader = new ClassRelativeResourceLoader(getClass());
        return resourceLoader.getResource(name);
    }

    /**
     * Change the status of the organization.
     * 
     * @param orgId
     *            orgId Organization.
     * @param status
     *            status Organization.
     */
    public void updateOrganizationStatus(Long orgId, boolean status) {
        Query query = getSession().createSQLQuery(UPDATE_ORG_STATUS);
        query.setParameter("orgId", orgId);
        query.setParameter("status", status ? "A" : "I");
        Assert.assertEquals(1, query.executeUpdate());
    }
}
