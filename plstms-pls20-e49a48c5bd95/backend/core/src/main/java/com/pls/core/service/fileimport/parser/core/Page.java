package com.pls.core.service.fileimport.parser.core;

/**
 * Represents page of {@link Document}.
 * 
 * @author Artem Arapov
 *
 */
public interface Page extends Iterable<Record> {

    /**
     * Get name of current page.
     * 
     * @return Not <code>null</code> {@link String}.
     * */
    String getName();

    /**
     * Does page is empty?
     * 
     * @return <code>true</code> if page contain {@link Record}, otherwise return <code>false</code>.
     */
    boolean isEmpty();

    /**
     * Return header {@link Record} of page.
     * 
     * @return Not <code>null</code> {@link Record}.
     */
    Record getHeader();

    /**
     * Returns the number of elements in this page.
     *
     * @return the number of elements in this page.
     */
    int size();

    /**
     * Return source object of this page.
     * 
     * @return source of page.
     */
    Object getSource();
}
