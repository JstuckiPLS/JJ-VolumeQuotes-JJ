package com.pls.core.dao;

import java.util.Collection;
import java.util.List;

import com.pls.core.domain.Identifiable;
import com.pls.core.exception.EntityNotFoundException;

/**
 * Base class for all DAO implementations.
 * Contains methods common for all DAOs, like getting entity by id, persisting etc.
 *
 * @param <Type> type of entity.
 * @param <IdType> type of entity id.
 *
 * @author Gleb Zgonikov
 */
public interface AbstractDao<Type extends Identifiable<IdType>, IdType> {
    /**
     * Persist entity.
     *
     * @param entity the entity to persist.
     */
    //TODO try to use saveOrUpdate() method.
    void persist(Type entity);

    /**
     * Persist batch of entities.
     *
     * @param entities collection of entities to persist.
     */
    void persistBatch(Collection<Type> entities);

    /**
     * Merge entity.
     * 
     * @param entity
     *            the entity to merge.
     * @return merged entity
     */
    Type merge(Type entity);

    /**
     * Save new entity or update existing entity.
     * 
     * @param entity
     *            Not <code>null</code> entity.
     * @return updated entity
     */
    Type saveOrUpdate(Type entity);

    /**
     * Refresh entity state. See void {@link org.hibernate.Session#refresh(Object object)} for more details.
     * 
     * @param entity
     *            Not <code>null</code> entity.
     * @deprecated this method mustn't be used and must be removed within refactoring of Automatic Invoice
     *             Processing.
     */
    @Deprecated
    void refresh(Type entity);

    /**
     * Save or update batch of entities.
     *
     * @param entities
     *            collection of entities to save.
     */
    void saveOrUpdateBatch(Collection<Type> entities);

    /**
     * Find entity by id.
     *
     * @param id the entity id to search.
     * @return the found result or null.
     */
    Type find(IdType id);

    /**
     * Evict(Detach) entity from session.
     * 
     * @param entity
     *            the entity to evict.
     */
    void evict(Type entity);

    /**
     * Update entity.
     *
     * @param entity the entity to update.
     * @return the updated entity.
     */
    //TODO try to use saveOrUpdate() method.
    Type update(Type entity);

    /**
     * Get entity by id. Throws {@link EntityNotFoundException} when entity was not found.
     *
     * @param id the id of the entity to get.
     * @return the found entity.
     * @throws EntityNotFoundException when entity was not found.
     */
    Type get(IdType id) throws EntityNotFoundException;

    /**
     * Get all entities.
     *
     * @return all entities.
     */
    List<Type> getAll();

    /**
     * Get all entities with given ids.
     *
     * @param ids set of entities id to get
     * @return List of found entities with given ids
     */
    List<Type> getAll(List<IdType> ids);

    /**
     * Get all entities with given ids. Throws {@link EntityNotFoundException} when some of
     * the entities were not found and <code>ignoreMissing</code> parameter is <code>true</code>.
     *
     * @param ids set of entities id to get.
     * @param ignoreMissing specifies if {@link EntityNotFoundException}
     * should be thrown when some entities were not found.
     * @return List of found entities with given ids.
     * @throws EntityNotFoundException when <code>ignoreMissing</code> is true and some entity was not found.
     */
    List<Type> getAll(List<IdType> ids, boolean ignoreMissing) throws EntityNotFoundException;
}
