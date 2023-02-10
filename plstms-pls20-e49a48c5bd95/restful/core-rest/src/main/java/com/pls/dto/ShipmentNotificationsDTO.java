package com.pls.dto;

import com.pls.core.domain.enums.AddressNotificationDirectionEnum;
import com.pls.core.shared.NotificationSource;

/**
 * DTO for shipment notification.
 *
 * @author Alexander Kirichenko
 */
public class ShipmentNotificationsDTO {

    private Long id;
    private String notificationType;
    private String emailAddress;
    private AddressNotificationDirectionEnum direction;
    private NotificationSource notificationSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public AddressNotificationDirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(AddressNotificationDirectionEnum direction) {
        this.direction = direction;
    }

    public NotificationSource getNotificationSource() {
        return notificationSource;
    }

    public void setNotificationSource(NotificationSource notificationSource) {
        this.notificationSource = notificationSource;
    }

}
