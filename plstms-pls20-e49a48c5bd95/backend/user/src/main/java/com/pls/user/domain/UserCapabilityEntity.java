package com.pls.user.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * User Capabilities (USER_CAPABILITIES_XREF) entity. This table contains the information on the
 * capabilities/permissions assigned to the users directly.
 * 
 * @author Pavani Challa
 */
@Entity
@Table(name = "USER_CAPABILITIES_XREF")
public class UserCapabilityEntity implements  Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1L;

    public static final String Q_UPDATE_STATUS_STATEMENT = "com.pls.user.domain.UserCapabilityEntity.Q_UPDATE_STATUS_STATEMENT";
    public static final String Q_GET_BY_PERSON_ID = "com.pls.user.domain.UserCapabilityEntity.Q_GET_BY_PERSON_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_CAPABILITIES_XREF_SEQUENCE")
    @SequenceGenerator(name = "USER_CAPABILITIES_XREF_SEQUENCE", sequenceName = "USER_CAPABILITIES_XREF_SEQ", allocationSize = 1)
    @Column(name = "USER_CAPABILITY_ID", nullable = false)
    private Long id;

    @Column(name = "CAPABILITY_ID")
    private Long capabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAPABILITY_ID", updatable = false, insertable = false)
    private CapabilityEntity capability;

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long pId) {
        this.id = pId;
    }

    public Long getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(Long capabilityId) {
        this.capabilityId = capabilityId;
    }

    public CapabilityEntity getCapability() {
        return capability;
    }

    public void setCapability(CapabilityEntity capability) {
        this.capability = capability;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer pVersion) {
        this.version = pVersion;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getCapabilityId()).append(getPersonId()).append(getStatus());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof UserCapabilityEntity) {
            if (obj == this) {
                result = true;
            } else {
                UserCapabilityEntity other = (UserCapabilityEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getCapabilityId(), other.getCapabilityId()).append(getPersonId(), other.getPersonId())
                        .append(getStatus(), other.getStatus());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("capabilityId", getCapabilityId()).append("personId", getPersonId()).append("status", getStatus());

        return builder.toString();
    }

}
