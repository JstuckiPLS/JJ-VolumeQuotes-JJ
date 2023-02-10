package com.pls.core.domain.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.shared.Status;

/**
 * Organization user.
 * 
 * @author Denis Zhupinsky
 */
@Entity
@Table(name = "ORGANIZATION_USERS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ORG_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force = true)
public abstract class OrganizationUserEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 4187072944041236985L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_usr_sequence")
    @SequenceGenerator(name = "org_usr_sequence", sequenceName = "ORGU_SEQ", allocationSize = 1)
    @Column(name = "ORG_USER_ID")
    protected Long id;

    @Embedded
    protected final PlainModificationObject modification = new PlainModificationObject();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "customerUser")
    protected final List<UserNotificationsEntity> notifications = new ArrayList<UserNotificationsEntity>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "customerUser")
    protected final List<UserPhoneEntity> phones = new ArrayList<UserPhoneEntity>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "customerUser")
    protected final List<UserAddressEntity> addresses = new ArrayList<UserAddressEntity>();

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    protected Status status = Status.ACTIVE;

    /**
     * Find first active {@link UserPhoneEntity} with specified {@link UserPhoneEntity#getType()}.
     * 
     * @param type
     *            Not <code>null</code> {@link PhoneType}.
     * @return Not <code>null</code> value if required entity was found. Otherwise returns <code>null</code>.
     */
    public UserPhoneEntity findFirstActivePhone(PhoneType type) {
        UserPhoneEntity result = null;
        for (UserPhoneEntity entity : getPhones()) {
            if (isActive(entity) && type.equals(entity.getType())) {
                result = entity;
                break;
            }
        }
        return result;
    }

    /**
     * Get list of active notification types ( {@link UserNotificationsEntity#getNotificationType()} values).
     * 
     * @return Not <code>null</code> {@link List}.
     */
    public List<String> getActiveNotifications() {
        List<String> result = new ArrayList<String>();
        for (UserNotificationsEntity entity : getNotifications()) {
            if (isActive(entity) && StringUtils.isNotBlank(entity.getNotificationType())) {
                result.add(entity.getNotificationType());
            }
        }
        return result;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public List<UserNotificationsEntity> getNotifications() {
        return notifications;
    }

    public List<UserPhoneEntity> getPhones() {
        return phones;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private boolean isActive(UserNotificationsEntity entity) {
        return entity != null && Status.ACTIVE.equals(entity.getStatus());
    }

    private boolean isActive(UserPhoneEntity entity) {
        return entity != null && Status.ACTIVE.equals(entity.getStatus());
    }

    public List<UserAddressEntity> getAddresses() {
        return addresses;
    }

    /**
     * Get the first item from {@link #getAddresses()} (or null if this collection is empty).
     * 
     * @return may return null.
     */
    public UserAddressEntity getAddress() {
        UserAddressEntity result = null;
        if (!addresses.isEmpty()) {
            result = addresses.get(0);
        }
        return result;
    }
}
