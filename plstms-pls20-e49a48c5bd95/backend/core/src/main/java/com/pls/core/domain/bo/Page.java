package com.pls.core.domain.bo;

import java.util.Iterator;
import java.util.List;

/**
 * Class to store a list of elements <code>T</code>,
 * and count of all elements on server side.
 * 
 * @param <T> type of element.
 * 
 * @author Artem Arapov
 *
 */
public class Page<T> implements Iterable<T> {
    private final List<T> list;
    private final int totalCount;

    /**
     * Constructor.
     * 
     * @param list items.
     * @param totalCount total count.
     */
    public Page(List<T> list, int totalCount) {
        this.list = list;
        this.totalCount = totalCount;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    /**
     * Size of page.
     * 
     * @return Positive value.
     */
    public int size() {
        return list.size();
    }
}
