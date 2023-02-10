package com.pls.core.dao.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import com.pls.core.dao.AbstractStreamQuery;
import com.pls.core.domain.Identifiable;

/**
 * Implementation of {@link AbstractStreamQuery}.
 * 
 * @param <Type> type of entity.
 * @param <IdType> type of entity id.
 * 
 * @author Artem Arapov
 *
 */
public abstract class AbstractStreamQueryImpl<Type extends Identifiable<IdType>, IdType extends Serializable>
            extends AbstractDaoImpl<Type, IdType> implements AbstractStreamQuery<Type, IdType> {

    @Override
    public Stream<Type> getResultAsStream(String queryName, int fetchSize) {
        ScrollableResults scroll = getCurrentSession().getNamedQuery(queryName).setReadOnly(true).setFetchSize(fetchSize)
                .scroll(ScrollMode.FORWARD_ONLY);

        return StreamSupport.stream(toSplitIterator(scroll, getTypeClass()), false).onClose(scroll::close);
    }

    private Spliterator<Type> toSplitIterator(ScrollableResults scroll, Class<Type> type) {
        return Spliterators.spliteratorUnknownSize(
           new ScrollableResultIterator<>(scroll, type),
              Spliterator.DISTINCT | Spliterator.NONNULL
              | Spliterator.CONCURRENT | Spliterator.IMMUTABLE
        );
     }

    private static class ScrollableResultIterator<T> implements Iterator<T> {

        private final ScrollableResults results;
        private final Class<T> type;

        ScrollableResultIterator(ScrollableResults results, Class<T> type) {
           this.results = results;
           this.type = type;
        }

        @Override
        public boolean hasNext() {
           return results.next();
        }

        @Override
        public T next() {
           return type.cast(results.get(0));
        }
     }
}
