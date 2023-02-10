package com.pls.shipment.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

/**
 * Load event data.
 * 
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "LOAD_GENERIC_EVENTS_DATA")
public class LoadEventDataEntity implements Serializable {

    private static final long serialVersionUID = 6022305512467877922L;

    @EmbeddedId
    private LoadEventDataPK eventDataPK;

    @Column(name = "DATA_TYPE")
    private Character dataType;

    @Column(name = "DATA")
    @Type(type = "com.pls.core.domain.usertype.StringUserType")
    private String data;

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getEventDataPK()).append(dataType).append(data).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoadEventDataEntity) {
            final LoadEventDataEntity other = (LoadEventDataEntity) obj;
            return new EqualsBuilder().append(getEventDataPK(), other.getEventDataPK()).
                    append(dataType, other.dataType).append(data, other.data).isEquals();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(dataType).append(", ").append(data);

        return builder.toString();
    }

    public LoadEventDataPK getEventDataPK() {
        return eventDataPK;
    }

    public void setEventDataPK(LoadEventDataPK eventDataPK) {
        this.eventDataPK = eventDataPK;
    }

    public Character getDataType() {
        return dataType;
    }

    public void setDataType(Character dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
