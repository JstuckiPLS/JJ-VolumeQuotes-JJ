package com.pls.core.domain.user;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Capabilities (CAPABILITIES) entity. This table holds all the permissions/capabilities that can be assigned
 * to a user either directly or through role for restricting the access to certain features of application.
 * This is entity is needed for information purpose only and not for creating and/or updating.
 *
 * @author Sergey Kirichenko
 */
@Entity
@Table(name = "CAPABILITIES")
public class CapabilityInfoEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1634027867948421259L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAP_SEQUENCE")
    @SequenceGenerator(name = "CAP_SEQUENCE", sequenceName = "CAP_SEQ", allocationSize = 1)
    @Column(name = "CAPABILITY_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    private PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        this.id = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        this.name = pName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        this.description = pDescription;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer pVersion) {
        this.version = pVersion;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject pModification) {
        this.modification = pModification;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getName());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof CapabilityInfoEntity) {
            if (obj == this) {
                result = true;
            } else {
                CapabilityInfoEntity other = (CapabilityInfoEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getName(), other.getName());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("name", getName())
                .append("description", getDescription())
                .append("version", getVersion())
                .append("modification", getModification());

        return builder.toString();
    }
}
