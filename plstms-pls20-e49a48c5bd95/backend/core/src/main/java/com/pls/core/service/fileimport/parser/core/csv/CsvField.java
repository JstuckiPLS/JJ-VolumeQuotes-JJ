package com.pls.core.service.fileimport.parser.core.csv;

import com.pls.core.service.fileimport.parser.core.BaseField;

/**
 * {@link Field} implementation for CSV file.
 * 
 * @author Artem Arapov
 *
 */
public class CsvField extends BaseField {

    private final String name;

    /**
     * Constructor.
     * 
     * @param value
     *            {@link String} value for this field.
     * @param itemName
     *            name of parent row.
     * @param columnIndex
     *            index of this column.
     */
    CsvField(String value, String itemName, int columnIndex) {
        super(value);
        name = itemName + "; Col#" + columnIndex;
    }

    @Override
    public String getName() {
        return name;
    }

}
