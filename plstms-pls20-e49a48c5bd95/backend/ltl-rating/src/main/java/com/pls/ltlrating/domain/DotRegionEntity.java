package com.pls.ltlrating.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.Identifiable;

/**
 * DotRegionEntity.
 *
 * @author Stas Norochevskiy
 *
 */
@Entity
@Table(name = "DOT_REGIONS")
public class DotRegionEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dot_region_seq")
    @SequenceGenerator(name = "dot_region_seq",
                       sequenceName = "DOT_REGIONS_SEQ", allocationSize = 1)
    @Column(name = "DOT_REGION_ID")
    private Long id;

    @Column(name = "DOT_REGION_NAME")
    private String dotRegionName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getDotRegionName() {
        return dotRegionName;
    }

    public void setDotRegionName(String dotRegionName) {
        this.dotRegionName = dotRegionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.dotRegionName).
                append(this.description).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else {
            if (obj instanceof DotRegionEntity) {
                final DotRegionEntity other = (DotRegionEntity) obj;
                result = new EqualsBuilder().
                        append(this.dotRegionName, other.dotRegionName).
                        append(this.description, other.description).
                        isEquals();
            }
        }

        return result;
    }
}
