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
 * Cross Reference Entity to get DOT region id for given State Code .
 *
 * @author Hima Bindu Challa
 *
 */
@Entity
@Table(name = "DOT_REGION_STATE_XREF")
public class DotRegionStateXrefEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 936781908983791L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dot_region_st_xref_seq")
    @SequenceGenerator(name = "dot_region_st_xref_seq",
                       sequenceName = "DOT_REGION_ST_XREF_SEQ", allocationSize = 1)
    @Column(name = "DOT_REGION_ST_XREF_ID")
    private Long id;

    @Column(name = "DOT_REGION_ID")
    private Long dotRegionId;

    @Column(name = "STATE_CODE")
    private String stateCode;

    public Long getDotRegionId() {
        return dotRegionId;
    }

    public void setDotRegionId(Long dotRegionId) {
        this.dotRegionId = dotRegionId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.dotRegionId).
                append(this.stateCode).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DotRegionStateXrefEntity other = (DotRegionStateXrefEntity) obj;
        return new EqualsBuilder().
                append(this.dotRegionId, other.dotRegionId).
                append(this.stateCode, other.stateCode).
                isEquals();
    }
}
