package com.pls.core.domain.user;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Set;

/**
 * Groups (GROUPS) entity. This table holds all the groups/roles that can be assigned
 * to a user for restricting/granting the access to certain features of application.
 * This is entity is needed for information purpose only and not for creating and/or updating.
 *
 * @author Sergey Kirichenko
 */
@Entity
@Table(name = "GROUPS")
public class GroupInfoEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 4988897110797773644L;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "GROUP_CAPABILITIES", joinColumns = {
            @JoinColumn(name = "GROUP_ID", insertable = false, updatable = false)
    }, inverseJoinColumns = {
            @JoinColumn(name = "CAPABILITY_ID", insertable = false, updatable = false)
    })
    @WhereJoinTable(clause = "status = 'A'")
    private Set<CapabilityInfoEntity> capabilities;

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

    public Set<CapabilityInfoEntity> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<CapabilityInfoEntity> capabilities) {
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
        if (obj instanceof GroupInfoEntity) {
            if (obj == this) {
                result = true;
            } else {
                GroupInfoEntity other = (GroupInfoEntity) obj;
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
