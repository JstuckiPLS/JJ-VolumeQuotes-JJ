package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.NotificationTypeEntity;

/** Per organization notification settings. */
@Entity
@Table(name = "org_notifications")
public class OrganizationNotificationsEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -727671250327049175L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_notifications_sequence")
    @SequenceGenerator(name = "org_notifications_sequence", sequenceName = "ORG_NOTIFICATIONS_SEQ",
            allocationSize = 1)
    @Column(name = "org_notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganizationEntity organization;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @ManyToOne
    @JoinColumn(name = "NOTIFICATION_TYPE")
    private NotificationTypeEntity notificationType;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public NotificationTypeEntity getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEntity notificationType) {
        this.notificationType = notificationType;
    }

}
