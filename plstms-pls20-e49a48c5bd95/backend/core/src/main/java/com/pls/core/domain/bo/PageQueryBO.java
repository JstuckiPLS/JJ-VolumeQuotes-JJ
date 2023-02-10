package com.pls.core.domain.bo;

/**
 * Page info.
 * 
 * @author Artem Arapov
 */
public class PageQueryBO {

    private int pageFirstIndex;

    private int pageSize;

    /**
     * Default constructor.
     * */
    public PageQueryBO() {
    }

    /**
     * Constructor.
     * 
     * @param pageFirstIndex first page index.
     * @param pageSize date size.
     */
    public PageQueryBO(int pageFirstIndex, int pageSize) {
        this.pageFirstIndex = pageFirstIndex;
        this.pageSize = pageSize;
    }

    public int getPageFirstIndex() {
        return pageFirstIndex;
    }

    public void setPageFirstIndex(int pageFirstIndex) {
        this.pageFirstIndex = pageFirstIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
