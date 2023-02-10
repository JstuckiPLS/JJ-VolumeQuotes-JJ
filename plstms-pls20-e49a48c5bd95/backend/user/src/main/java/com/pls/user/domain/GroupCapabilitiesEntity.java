package com.pls.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * Group Capabilities (GROUP_CAPABILITIES) entity. This table holds the association between the groups and the
 * capabilities.
 * 
 * @author Pavani Challa
 */
@Entity
@Table(name = "GROUP_CAPABILITIES")
public class GroupCapabilitiesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 3652256289562566527L;

    public static final String Q_DELETE_FOR_GROUP_STATEMENT = "com.pls.user.domain.GroupCapabilitiesEntity.Q_DELETE_FOR_GROUP_STATEMENT";
    public static final String Q_GET_GROUP_CAPABILITIES = "com.pls.user.domain.GroupCapabilitiesEntity.Q_GET_GROUP_CAPABILITIES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_CAPS_SEQUENCE")
    @SequenceGenerator(name = "GROUP_CAPS_SEQUENCE", sequenceName = "GROUP_CAPABILITIES_SEQ", allocationSize = 1)
    @Column(name = "GROUP_CAPABILITY_ID", nullable = false)
    private Long id;

    @Column(name = "GROUP_ID", nullable = false, insertable = false, updatable = false)
    private Long groupId;

    @Column(name = "CAPABILITY_ID", nullable = false)
    private Long capabilityId;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long pGroupId) {
        this.groupId = pGroupId;
    }

    public Long getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(Long pCapabilityId) {
        this.capabilityId = pCapabilityId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        this.status = pStatus;
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
        builder.append(getGroupId())
                .append(getCapabilityId())
                .append(getStatus());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof GroupCapabilitiesEntity) {
            if (obj == this) {
                result = true;
            } else {
                GroupCapabilitiesEntity other = (GroupCapabilitiesEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getGroupId(), other.getGroupId())
                        .append(getCapabilityId(), other.getCapabilityId())
                        .append(getStatus(), other.getStatus())
                        .append(getModification(), other.getModification());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("groupId", getGroupId())
                .append("capabilityId", getCapabilityId())
                .append("status", getStatus());

        return builder.toString();
    }
}

