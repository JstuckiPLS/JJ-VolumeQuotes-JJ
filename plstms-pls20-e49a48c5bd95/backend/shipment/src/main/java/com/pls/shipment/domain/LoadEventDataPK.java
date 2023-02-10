package com.pls.shipment.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Primary key for  {@link LoadEventDataEntity}.
 * 
 * @author Gleb Zgonikov
 */
@Embeddable
public class LoadEventDataPK implements Serializable {

    private static final long serialVersionUID = -4882591564944396178L;

    /**
     * Default constructor.
     */
    public LoadEventDataPK() {
    }

    /**
     * constructor.
     * 
     * @param event Event data.
     * @param ordinal Ordinal value.
     */
    public LoadEventDataPK(LoadEventEntity event, Byte ordinal) {
        this.event = event;
        this.ordinal = ordinal;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private LoadEventEntity event;

    @Column(name = "ORDINAL")
    private Byte ordinal;

    public LoadEventEntity getEvent() {
        return event;
    }

    public void setEvent(LoadEventEntity event) {
        this.event = event;
    }

    public Byte getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Byte ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(event).
                append(ordinal).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoadEventDataPK) {
            final LoadEventDataPK other = (LoadEventDataPK) obj;
            return new EqualsBuilder().
                    append(event, other.event).
                    append(ordinal, other.ordinal).isEquals();
        } else {
            return false;
        }
    }


}
