package com.pls.shipment.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Load Tracking Status id type entity.
 * 
 * This represents a "composite primary key" for the LoadTrackingStatusEntity table.
 * It contains all the columns that form a unique id.
 * Must implement equals() and hashcode() and be serializable.
 * https://community.jboss.org/wiki/EqualsAndHashCode
 * 
 * @author Sergii Belodon
 */

@Embeddable
public class LoadTrackingStatusEntityId implements Serializable {

    private static final long serialVersionUID = -5557444824635240612L;

    @Column(name = "TRACK_STATUS_CODE")
    private String code;

    @Column(name = "SOURCE")
    private Long source;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(code).
                append(source).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoadTrackingStatusEntityId) {
            final LoadTrackingStatusEntityId other = (LoadTrackingStatusEntityId) obj;
            return new EqualsBuilder().
                    append(code, other.code).
                    append(source, other.source).isEquals();
        } else {
            return false;
        }
    }

}
