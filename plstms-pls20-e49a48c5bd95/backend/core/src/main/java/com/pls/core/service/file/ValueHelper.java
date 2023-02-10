package com.pls.core.service.file;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class to convert different types to string.
 * 
 * @author Maxim Medvedev
 */
public final class ValueHelper {
    public static final String DATE_FORMAT = "MM/dd/yyyy";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

    /**
     * Convert {@link Object} to string value.
     * 
     * @param value
     *            Value.
     * @return Not <code>null</code> {@link String}.
     */
    public String prepare(Object value) {
        String result = StringUtils.EMPTY;

        if (value instanceof Date) {
            result = dateFormatter.format((Date) value);
        } else if (value != null) {
            result = String.valueOf(value);
        }
        return result;
    }
}
