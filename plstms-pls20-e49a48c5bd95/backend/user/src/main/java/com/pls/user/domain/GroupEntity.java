package com.pls.user.domain;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.List;

/**
 * Groups (GROUPS) entity. This table holds all the groups/roles that can be assigned
 * to a user for restricting/granting the access to certain features of application.
 * 
 * @author Pavani Challa
 */
@Entity
@Table(name = "GROUPS")
public class GroupEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1542526535422154278L;

    public static final String Q_UPDATE_STATUS_STATEMENT = "com.pls.user.domain.GroupEntity.Q_UPDATE_STATUS_STATEMENT";
    public static final String Q_GET_ALL_USER_GROUP_ENTITIES = "com.pls.user.domain.GroupEntity.Q_GET_ALL_USER_GROUP_ENTITIES";
    public static final String Q_GET_USERS_WITH_GROUP = "com.pls.user.domain.GroupEntity.Q_GET_USERS_WITH_GROUP";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUPS_SEQUENCE")
    @SequenceGenerator(name = "GROUPS_SEQUENCE", sequenceName = "GROUPS_SEQ", allocationSize = 1)
    @Column(name = "GROUP_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "STATUS", nullable = false)
    private String status = Status.ACTIVE.getCode();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    private PlainModificationObject modification = new PlainModificationObject();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private List<GroupCapabilitiesEntity> grpCapabilities;

    @Transient
    private List<CapabilityEntity> capabilities;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String pStatus) {
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

    public List<GroupCapabilitiesEntity> getGrpCapabilities() {
        return grpCapabilities;
    }

    public void setGrpCapabilities(List<GroupCapabilitiesEntity> pGrpCapabilities) {
        this.grpCapabilities = pGrpCapabilities;
    }

    public List<CapabilityEntity> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<CapabilityEntity> capabilities) {
        this.capabilities = capabilities;
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
        if (obj instanceof GroupEntity) {
            if (obj == this) {
                result = true;
            } else {
                GroupEntity other = (GroupEntity) obj;
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
                .append("modification", getModification());

        return builder.toString();
    }
}
