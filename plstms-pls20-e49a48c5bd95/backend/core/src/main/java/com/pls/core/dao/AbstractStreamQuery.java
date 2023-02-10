package com.pls.core.dao;

import java.util.stream.Stream;

import com.pls.core.domain.Identifiable;

/**
 * Implements logic to get query result as {@link Stream}.
 * 
 * @param <Type> type of entity.
 * @param <IdType> type of entity id.
 * 
 * @author Artem Arapov
 *
 */
public interface AbstractStreamQuery<Type extends Identifiable<IdType>, IdType> extends AbstractDao<Type, IdType> {

    int DEFAULT_FETCH_SIZE = 10000;

    /**
     * Returns query result as {@link Stream} of specified <code>Type</code>.
     * 
     * @param queryName - the name of query defined externally.
     * @param fetchSize - the fetch size hint.
     * @return Stream of specified <code>Type</code>.
     */
    Stream<Type> getResultAsStream(String queryName, int fetchSize);
}
