package com.pls.ltlrating.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Ltl pricing types entity.
 *
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "LTL_PRICING_TYPES")
public class LtlPricingTypesEntity implements Serializable {

    private static final long serialVersionUID = 9105884393648805903L;

    public static final String GET_BY_GROUP =
            "com.pls.ltlrating.domain.profile.LtlPricingTypesEntity.GET_BY_GROUP";
    public static final String GET_BY_TYPE =
            "com.pls.ltlrating.domain.profile.LtlPricingTypesEntity.GET_BY_TYPE";

    @Id
    @Column(name = "LTL_PRICING_TYPE")
    private String ltlPricingType;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "GROUP_TYPE")
    private String groupType;

    public String getLtlPricingType() {
        return ltlPricingType;
    }

    public void setLtlPricingType(String ltlPricingType) {
        this.ltlPricingType = ltlPricingType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(getLtlPricingType())
                .append(getDescription())
                .append(getGroupType());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LtlPricingTypesEntity) {
            if (obj == this) {
                result = true;
            } else {
                LtlPricingTypesEntity other = (LtlPricingTypesEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getLtlPricingType(), other.getLtlPricingType())
                        .append(getDescription(), other.getDescription())
                        .append(getGroupType(), other.getGroupType());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("ltlPricingType", getLtlPricingType())
                .append("description", getDescription())
                .append("group", getGroupType());

        return builder.toString();
    }
}
