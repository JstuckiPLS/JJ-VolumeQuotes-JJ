package com.pls.core.service.fileimport.parser.core;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;

/**
 * Base implementation of {@link com.pls.core.service.fileimport.parser.core.Field}.
 * 
 * @author Artem Arapov
 * @author Maxim Medvedev
 */
public abstract class BaseField implements Field {

    private String value;
    protected static final String[] DATE_FORMATS = new String[] {"yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy", "yyyyMMdd", "h:mm a"};

    private String prepareInvalidCellValueMessage() {
        return getName() + " cell has invalid data '" + value + "'.";
    }

    /**
     * Constructor.
     * 
     * @param value <code>String</code> value of field.
     */
    public BaseField(String value) {
        this.value = StringUtils.trimToEmpty(value);
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }

    @Override
    public String getString() throws ImportFileInvalidDataException {
        return value;
    }

    @Override
    public Long getLong() throws ImportFileInvalidDataException {
        try {
            return StringUtils.isBlank(value) ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ImportFileInvalidDataException(prepareInvalidCellValueMessage(), e);
        }
    }

    @Override
    public BigDecimal getBigDecimal() throws ImportFileInvalidDataException {
        try {
            return StringUtils.isBlank(value) ? null : new BigDecimal(value);
        } catch (NumberFormatException exc) {
            throw new ImportFileInvalidDataException(prepareInvalidCellValueMessage(), exc);
        }
    }

    @Override
    public Boolean getBoolean() throws ImportFileInvalidDataException {
        boolean result;
        if (StringUtils.equalsIgnoreCase("yes", value) || StringUtils.equalsIgnoreCase("true", value)) {
            result = true;
        } else if (StringUtils.equalsIgnoreCase("no", value) || StringUtils.equalsIgnoreCase("false", value)) {
            result = false;
        } else {
            throw new ImportFileInvalidDataException(prepareInvalidCellValueMessage());
        }
        return result;
    }

    @Override
    public Date getDate() throws ImportFileInvalidDataException {
        try {
            return DateUtils.parseDateStrictly(value, DATE_FORMATS);
        } catch (ParseException nfe) {
            throw new ImportFileInvalidDataException(prepareInvalidCellValueMessage(), nfe);
        }
    }
}
