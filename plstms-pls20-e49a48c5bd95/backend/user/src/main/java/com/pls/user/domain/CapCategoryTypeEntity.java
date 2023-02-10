package com.pls.user.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Category types for the capabilities. For eg., Finance, Shipment
 * 
 * @author Pavani Challa
 */
@Entity
@Table(name = "CAP_CATEGORY_TYPES")
public class CapCategoryTypeEntity implements Serializable {

    private static final long serialVersionUID = 4875152625411452364L;

    @Id
    @Column(name = "CATEGORY", nullable = false, insertable = false, updatable = false)
    private String category;

    @Column(name = "DESCRIPTION", nullable = false, insertable = false, updatable = false)
    private String description;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getCategory())
                .append(getDescription());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof CapCategoryTypeEntity) {
            if (obj == this) {
                result = true;
            } else {
                CapCategoryTypeEntity other = (CapCategoryTypeEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getCategory(), other.getCategory())
                        .append(getDescription(), other.getDescription());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("category", getCategory())
                .append("description", getDescription());

        return builder.toString();
    }
}
