package com.pls.core.service.fileimport.parser.core;

import java.math.BigDecimal;
import java.util.Date;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;

/**
 * Field of {@link Record}.
 * 
 * @author Artem Arapov
 * @author Maxim Medvedev
 *
 */
public interface Field {

    /**
     * Get name of field.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    String getName();

    /**
     * Does this field empty?
     * 
     * @return <code>true</code> if field contain data, otherwise <code>false</code>
     */
    boolean isEmpty();

    /**
     * Return field value as <code>String</code>.
     * 
     * @return Not <code>null</code> {@link String}.
     * @throws ImportFileInvalidDataException Unable to parse {@link String}.
     */
    String getString() throws ImportFileInvalidDataException;

    /**
     * Return field value as {@link Long}.
     * 
     * @return {@link long}.
     * @throws ImportFileInvalidDataException Unable to parse number.
     */
    Long getLong() throws ImportFileInvalidDataException;

    /**
     * Parse {@link Date} value.
     * 
     * @return Not <code>null</code> {@link Date} value.
     * 
     * @throws ImportFileInvalidDataException
     *             Unable to parse {@link Date}.
     */
    Date getDate() throws ImportFileInvalidDataException;

    /**
     * Parse boolean value.
     * 
     * @return Not <code>null</code> value.
     * 
     * @throws ImportFileInvalidDataException
     *             Unable to parse boolean.
     */
    Boolean getBoolean() throws ImportFileInvalidDataException;

    /**
     * Parse {@link BigDecimal} value.
     * 
     * @return Not <code>null</code> {@link BigDecimal} value.
     * 
     * @throws ImportFileInvalidDataException
     *             Unable to parse {@link BigDecimal}.
     */
    BigDecimal getBigDecimal() throws ImportFileInvalidDataException;
}
