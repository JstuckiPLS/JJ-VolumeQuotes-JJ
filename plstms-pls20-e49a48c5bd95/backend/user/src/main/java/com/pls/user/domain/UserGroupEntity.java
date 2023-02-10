package com.pls.user.domain;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

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

/**
 * User Groups (USER_GROUPS) entity. This table contains the information on the
 * Groups/Roles assigned to the users.
 * 
 * @author Pavani Challa
 */
@Entity
@Table(name = "USER_GROUPS")
public class UserGroupEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 5424658598562541475L;

    public static final String Q_UPDATE_STATUS_STATEMENT = "com.pls.user.domain.UserGroupEntity.Q_UPDATE_STATUS_STATEMENT";
    public static final String Q_INACTIVATE_ALL_USERS_STATEMENT = "com.pls.user.domain.UserGroupEntity.Q_INACTIVATE_ALL_USERS_STATEMENT";
    public static final String Q_GET_BY_PERSON_ID = "com.pls.user.domain.UserGroupEntity.Q_GET_BY_PERSON_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_GROUPS_SEQUENCE")
    @SequenceGenerator(name = "USER_GROUPS_SEQUENCE", sequenceName = "USER_GROUPS_SEQ", allocationSize = 1)
    @Column(name = "USER_GROUP_ID", nullable = false)
    private Long id;

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID", nullable = false, insertable = false, updatable = false)
    private GroupEntity group;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
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
        builder.append(getGroupId()).append(getPersonId()).append(getStatus());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof UserGroupEntity) {
            if (obj == this) {
                result = true;
            } else {
                UserGroupEntity other = (UserGroupEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getGroupId(), other.getGroupId()).append(getPersonId(), other.getPersonId())
                        .append(getStatus(), other.getStatus());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("groupId", getGroupId()).append("personId", getPersonId()).append("status", getStatus());

        return builder.toString();
    }
}
