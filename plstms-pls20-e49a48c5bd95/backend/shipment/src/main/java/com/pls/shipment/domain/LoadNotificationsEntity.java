package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.shared.NotificationSource;

/**
 * Load notifications entity.
 *
 * @author Alexander Kirichenko
 */
@Entity
@Table(name = "LOAD_NOTIFICATIONS")
public class LoadNotificationsEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 707671650627039075L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_notifications_sequence")
    @SequenceGenerator(name = "load_notifications_sequence", sequenceName = "LOAD_NOTIFICATIONS_SEQ",
            allocationSize = 1)
    @Column(name = "LOAD_NOTIFICATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @ManyToOne
    @JoinColumn(name = "NOTIFICATION_TYPE", nullable = false)
    private NotificationTypeEntity notificationType;

    @Column(name = "EMAIL_ADDRESS", nullable = false)
    private String emailAddress;

    @Column(name = "NOTIFICATION_SOURCE")
    @Enumerated(EnumType.STRING)
    private NotificationSource notificationSource;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public NotificationTypeEntity getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEntity notificationType) {
        this.notificationType = notificationType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public NotificationSource getNotificationSource() {
        return notificationSource;
    }

    public void setNotificationSource(NotificationSource notificationSource) {
        this.notificationSource = notificationSource;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getLoad().getId()).append(getNotificationType()).append(getEmailAddress()).toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof LoadNotificationsEntity)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        LoadNotificationsEntity that = (LoadNotificationsEntity) o;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(getLoad().getId(), that.getLoad().getId()).
                append(getNotificationType(), that.getNotificationType()).append(getEmailAddress(), that.getEmailAddress());
        return builder.isEquals();
    }
}
