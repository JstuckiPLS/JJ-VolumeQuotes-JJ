package com.pls.shipment.domain.sterling;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Tracking Notification class contains EmailAddress and NotificationType.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "TrackingNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrackingNotificationJaxbBO implements Serializable {

    private static final long serialVersionUID = -725211356357037028L;

    @XmlElement(name = "EmailAddress")
    private String emailAddress;

    @XmlElement(name = "NotificationType")
    private String notificationType;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getNotificationType()).append(getEmailAddress());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TrackingNotificationJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                TrackingNotificationJaxbBO other = (TrackingNotificationJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getNotificationType(), other.getNotificationType()).append(getEmailAddress(),
                        other.getEmailAddress());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("NotificationType", getNotificationType()).append("EmailAddress",
                getEmailAddress());

        return builder.toString();
    }
}
