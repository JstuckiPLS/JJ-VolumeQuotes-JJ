package com.pls.core.domain.user;

import java.io.Serializable;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * This class represents set of notification flags that can be set for user.
 * 
 * @author Stas Norochevskiy
 */
@Entity
@Table(name = "USER_NOTIFICATIONS")
public class UserNotificationsEntity implements Serializable, HasModificationInfo {
    private static final long serialVersionUID = 7049495879361163859L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_notification_sequence")
    @SequenceGenerator(name = "usr_notification_sequence", sequenceName = "USER_NOTOFICATIONS_SEQ",
            allocationSize = 1)
    @Column(name = "USER_NOTIFICATION_ID")
    private Long id;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    /**
     * Reference to {@link NotificationTypeEntity#getId()}.
     */
    @Column(name = "NOTIFICATION_TYPE", nullable = false)
    private String notificationType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_USER_ID", nullable = false)
    private CustomerUserEntity customerUser;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    public Long getId() {
        return id;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public CustomerUserEntity getCustomerUser() {
        return customerUser;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNotificationType(String notificationTypes) {
        notificationType = notificationTypes;
    }

    public void setCustomerUser(CustomerUserEntity customerUser) {
        this.customerUser = customerUser;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
