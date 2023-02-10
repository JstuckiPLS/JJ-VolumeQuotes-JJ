package com.pls.core.shared;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Contains information about entity's field that may be used for sorting or filtering.
 * 
 * @author Maxim Medvedev
 */
public class FieldDescriptor {

    private final String[] fields;

    private final boolean filterable;

    private final boolean sortable;

    private final FilterType type;

    private final OrderType orderType;

    /**
     * Constructor for field with {@link FilterType#LIKE} that may be used for filtering and sorting.
     * 
     * @param fields
     *            Not <code>null</code> {@link String}.
     */
    public FieldDescriptor(String fields) {
        this(fields, true, true, FilterType.LIKE, OrderType.DEFAULT);
    }

    /**
     * Constructor for field with {@link FilterType#LIKE}.
     * 
     * @param fields
     *            Not <code>null</code> {@link String}.
     * @param filterable
     *            <code>true</code> if this field may be used for filtering. Otherwise <code>false</code>.
     * @param sortable
     *            <code>true</code> if this field may be used for sorting. Otherwise <code>false</code>.
     */
    public FieldDescriptor(String fields, boolean filterable, boolean sortable) {
        this(fields, filterable, sortable, FilterType.LIKE, OrderType.DEFAULT);
    }

    /**
     * Constructor.
     *
     * @param fields
     *            Not <code>null</code> {@link String}.
     * @param filterable
     *            <code>true</code> if this field may be used for filtering. Otherwise <code>false</code>.
     * @param sortable
     *            <code>true</code> if this field may be used for sorting. Otherwise <code>false</code>.
     * @param type
     *            Not <code>null</code> {@link FilterType}.
     * @param orderType {@link OrderType} of fields
     */
    public FieldDescriptor(String fields, boolean filterable, boolean sortable, FilterType type, OrderType orderType) {
        if (StringUtils.isBlank(fields)) {
            this.fields = new String[0];
        } else {
            this.fields = StringUtils.trimToEmpty(fields).split("\\s*,\\s*");
        }
        this.type = type;
        this.filterable = filterable && (this.fields.length > 0);
        this.sortable = sortable && (this.fields.length > 0);
        this.orderType = orderType;
    }

    /**
     * Constructor.
     * 
     * @param fields
     *            Not <code>null</code> {@link String}.
     * @param filterable
     *            <code>true</code> if this field may be used for filtering. Otherwise <code>false</code>.
     * @param sortable
     *            <code>true</code> if this field may be used for sorting. Otherwise <code>false</code>.
     * @param type
     *            Not <code>null</code> {@link FilterType}.
     */
    public FieldDescriptor(String fields, boolean filterable, boolean sortable, FilterType type) {
        this(fields, filterable, sortable, type, OrderType.DEFAULT);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && getClass().isInstance(obj)) {
            if (obj == this) {
                result = true;
            } else {
                FieldDescriptor other = (FieldDescriptor) obj;
                result = new EqualsBuilder().append(getFields(), other.getFields())
                        .append(getFilterType(), other.getFilterType()).isEquals();
            }
        }
        return result;
    }

    /**
     * Get name of entity fields that should be used.
     * 
     * @return Not <code>null</code> array.
     */
    public final String[] getFields() {
        return fields;
    }

    /**
     * Get type for filtering for this field. Has sense only if {@link #isFilterable()} is <code>true</code>.
     * 
     * @return Not <code>null</code> {@link FilterType}.
     */
    public final FilterType getFilterType() {
        return type;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getFields()).append(getFilterType()).toHashCode();
    }

    /**
     * Whether this field used for filtration.
     * 
     * @return <code>true</code> if this we may filter result by this filed. Otherwise returns
     *         <code>false</code>.
     */
    public final boolean isFilterable() {
        return filterable;
    }

    /**
     * Whether this field used for sorting.
     * 
     * @return <code>true</code> if this we may sort result by this filed. Otherwise returns
     *         <code>false</code>.
     */
    public final boolean isSortable() {
        return sortable;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
