package com.pls.core.service.fileimport.parser.core;

/**
 * Represents record with data.
 * 
 * @author Artem Arapov
 *
 */
public interface Record {

    /**
     * Get name of record.
     * 
     * @return Not <code>null</code> {@link String} value
     */
    String getName();

    /**
     * Get count of fields in current record.
     * 
     * @return count of fields.
     */
    int getFieldsCount();

    /**
     * Get {@link Field} by index.
     * 
     * @param idx <code>int</code> position of field in current record.
     * @return {@link Field}
     */
    Field getFieldByIdx(int idx);

    /**
     * Is this row empty?
     * 
     * @return <code>true</code> if not empty, otherwise return <code>false</code>
     */
    boolean isEmpty();
}
