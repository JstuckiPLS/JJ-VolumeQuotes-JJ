package com.pls.ltlrating.batch.migration;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.HasLabel;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemStatus;

/**
 * Mapper class to collect data from excel row into {@link LtlPricingItem}.
 *
 * @author Alex Krychenko
 */
public class LtlPricingItemRowMapper implements RowMapper<LtlPricingItem> {
    private static final Logger LOG = LoggerFactory.getLogger(LtlPricingItemRowMapper.class);

    private List<String> propertiesToPopulate;
    private BeanWrapper beanWrapper = new BeanWrapperImpl(LtlPricingItem.class);

    @Override
    public LtlPricingItem mapRow(final RowSet rowSet) throws Exception {
        return convertToPricingItem(rowSet.getCurrentRow());
    }

    public void setPropertiesToPopulate(final String[] propertiesToPopulate) {
        this.propertiesToPopulate = Arrays.stream(propertiesToPopulate).collect(Collectors.toList());
    }

    private LtlPricingItem convertToPricingItem(final String[] row) {
        LtlPricingItem item = new LtlPricingItem();
        item.setOrigColumns(row);
        if (row == null || row.length != propertiesToPopulate.size()) {
            String errorMessage = getErrorMessage(row);
            item.setError(new ImportException(errorMessage));
            LOG.error("Wrong format row:[{}]", errorMessage);
            return item;
        }
        try {
            int index = 0;
            for (String propertyName : propertiesToPopulate) {
                populateValue(item, row[index++], propertyName);
            }
            validateStatus(item);
        } catch (ImportException e) {
            item.setError(e);
        }
        return item;
    }

    private void validateStatus(final LtlPricingItem item) {
        if (item.getStatus() == null) {
            item.setStatus(LtlPricingItemStatus.ACTIVE.name());
        }
    }

    private String getErrorMessage(final String[] row) {
        if (row == null || row.length == 0) {
            return "Empty row";
        }
        return String.format("Row size[%d] is not corresponds to properties set size[%d]: row: %s", row.length, propertiesToPopulate.size(),
                             Arrays.deepToString(row));
    }

    @SuppressWarnings("unchecked")
    private void populateValue(final LtlPricingItem item, final String propertyStringValue, final String propertyName) throws ImportException {
        PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(propertyName);
        Class<?> propertyType = propertyDescriptor.getPropertyType();
        try {
            if (propertyDescriptor.getWriteMethod() != null && StringUtils.isNotBlank(propertyStringValue)) {
                Object propValue = propertyStringValue;
                if (isNumericPropertyType(propertyType)) {
                    propValue = getNumericPropertyValue(propertyStringValue, propertyDescriptor);
                } else if (Date.class.isAssignableFrom(propertyType)) {
                    propValue = getDateValue(propertyStringValue);
                } else if (HasLabel.class.isAssignableFrom(propertyType)) {
                    propValue = getEnumValueFromLabel((Class<? extends HasLabel>) propertyType, propertyStringValue);
                }
                propertyDescriptor.getWriteMethod().invoke(item, propValue);
            }
        } catch (InvocationTargetException | IllegalAccessException | ParseException | IllegalArgumentException e) {
            LOG.error("Cant't populate property[{}], with value[{}]: {}", propertyName, propertyStringValue, e);
            throw new ImportException(String.format("Cant't populate property[%s], with value[%s]", propertyName, propertyStringValue), e);
        }
    }

    private Object getNumericPropertyValue(String propertyStringValue, PropertyDescriptor propertyDescriptor) {
        if (Long.class.isAssignableFrom(propertyDescriptor.getWriteMethod().getParameterTypes()[0])) {
            return Long.valueOf(propertyStringValue);
        } else if (BigInteger.class.isAssignableFrom(propertyDescriptor.getWriteMethod().getParameterTypes()[0])) {
            return new BigInteger(propertyStringValue);
        } else {
            return new BigDecimal(propertyStringValue);
        }
    }

    private boolean isNumericPropertyType(Class<?> propertyType) {
        return Long.class.isAssignableFrom(propertyType) || BigDecimal.class.isAssignableFrom(propertyType)
                || BigInteger.class.isAssignableFrom(propertyType);
    }

    private String getEnumValueFromLabel(final Class<? extends HasLabel> enumCalss, final String propertyStringValue) {
        for (HasLabel hasLabelEnum : enumCalss.getEnumConstants()) {
            if (hasLabelEnum.getLabel().equals(propertyStringValue) || ((Enum<?>) hasLabelEnum).name().equalsIgnoreCase(propertyStringValue)) {
                return hasLabelEnum.toString();
            }
        }
        return null;
    }

    private Date getDateValue(final String propertyStringValue) throws ParseException {
        if (propertyStringValue.contains("/")) {
            return new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(propertyStringValue);
        }
        return new Date(Long.parseLong(propertyStringValue));

    }
}
