package com.pls.ltlrating.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.shared.Status;

/**
 * Mileage types entity.
 *
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "MILEAGE_TYPES")
public class MileageTypesEntity implements Identifiable<MileageTypePK> {

    private static final long serialVersionUID = -7966361037831935972L;

    @EmbeddedId
    private MileageTypePK id;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).append(description).append(status).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MileageTypesEntity) {
            final MileageTypesEntity other = (MileageTypesEntity) obj;
            return new EqualsBuilder().append(getId(), other.getId()).append(description,
                    other.description).append(status, other.status).isEquals();
        } else {
            return false;
        }
    }

    public MileageTypePK getId() {
        return id;
    }

    public void setId(MileageTypePK mileageTypePK) {
        this.id = mileageTypePK;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
