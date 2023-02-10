package com.pls.core.dao;

import java.util.List;

/**
 * Base class for all dictionary type DAO implementations.
 * Contains methods common for all dictionary type DAOs, like get all.
 *
 * @param <Type> type of entity.
 *
 * @author Sergey Kirichenko
 */
public interface DictionaryDao<Type> {

    /**
     * Get all entities.
     *
     * @return all entities.
     */
    List<Type> getAll();
}
