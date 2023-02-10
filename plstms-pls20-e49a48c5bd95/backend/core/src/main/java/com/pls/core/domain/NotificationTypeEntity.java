package com.pls.core.domain;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Immutable;

/**
 * Notification type entity.<br />
 * Normally this entity should net be created or updated by manually.
 * 
 * @author Alexander Kirichenko
 */
@Entity
@Cacheable
@Immutable
@Table(name = "NOTIFICATION_TYPES")
public class NotificationTypeEntity implements Serializable {

    private static final long serialVersionUID = 8869101845895346045L;

    @Column(name = "DESCRIPTION")
    private String description;

    @Id
    @Column(name = "NOTIFICATION_TYPE")
    private String id;

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof NotificationTypeEntity) {
            if (obj == this) {
                result = true;
            } else {
                final NotificationTypeEntity other = (NotificationTypeEntity) obj;
                result = new EqualsBuilder().append(getId(), other.getId())
                        .append(getDescription(), other.getDescription()).isEquals();
            }
        }
        return result;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).append(getDescription()).toHashCode();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String notificationType) {
        id = notificationType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ID", getId()).append("description", getDescription())
                .toString();
    }
}
