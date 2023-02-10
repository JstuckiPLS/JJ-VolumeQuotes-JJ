package com.pls.core.domain.bo;

import java.util.List;

/**
 * Container for list of business object objects. Has additional property with total count of objects.
 * 
 * @author Denis Zhupinsky
 * 
 * @param <T>
 *            type of business object in the list.
 * @deprecated usages of this class should be removed
 */
@Deprecated
public class ListBO<T> {
    private Long totalCount;

    private List<T> list;

    /**
     * Get total objects count.
     *
     * @return total objects count.
     */
    public Long getTotalCount() {
        return totalCount;
    }

    /**
     * Set total objects count.
     * @param totalCount the total objects count.
     *
     */
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Get list of business objects.

     * @return list of business objects.
     */
    public List<T> getList() {
        return list;
    }

    /**
     * Set list of business objects.
     *
     * @param list list of business objects.
     *
     */
    public void setList(List<T> list) {
        this.list = list;
    }
}
