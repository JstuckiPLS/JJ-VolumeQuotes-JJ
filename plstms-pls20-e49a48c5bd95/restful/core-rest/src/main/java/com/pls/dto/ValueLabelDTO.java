package com.pls.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DTO to transfer key/value pair. Main use case to fill dropdown UI elements.
 * 
 * @author Gleb Zgonikov
 */
public class ValueLabelDTO {
    private String label;
    private Object value;

    /**
     * Default constructor.
     */
    public ValueLabelDTO() {
    }

    /**
     * Constructor.
     * 
     * @param value
     *            field
     * @param label
     *            field
     */
    public ValueLabelDTO(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof ValueLabelDTO) {
            if (obj == this) {
                result = true;
            } else {
                final ValueLabelDTO other = (ValueLabelDTO) obj;
                result = new EqualsBuilder().append(getValue(), other.getValue()).isEquals();
            }
        }
        return result;
    }

    public String getLabel() {
        return label;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getValue()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("value", getValue()).append("label", getLabel()).toString();
    }
}
