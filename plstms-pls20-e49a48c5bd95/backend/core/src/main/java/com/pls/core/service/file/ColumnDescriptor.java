package com.pls.core.service.file;

/**
 * Interface to obtain information for column.
 * 
 * @author Maxim Medvedev
 * 
 * @param <T>
 *            Type of data item.
 */
public interface ColumnDescriptor<T extends Object> {
    /**
     * Get title for this column.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    String getTitle();

    /**
     * Extract column value from data object.
     * 
     * @param item
     *            Not <code>null</code> data item.
     * @return Value of specified object. May be <code>null</code>.
     */
    Object getValue(T item);
}