package com.pls.core.domain.address;

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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.enums.AddressNotificationDirectionEnum;

/**
 * Address notifications entity.
 *
 * @author Sergii Belodon
 */
@Entity
@Table(name = "ADDRESS_NOTIFICATIONS")
public class AddressNotificationsEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -727671250327049175L;

    public static final String Q_GET_EMAILS = "com.pls.shipment.domain.AddressNotificationsEntity.Q_GET_EMAILS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_notifications_sequence")
    @SequenceGenerator(name = "address_notifications_sequence", sequenceName = "ADDRESS_NOTIFICATIONS_SEQ",
            allocationSize = 1)
    @Column(name = "ADDRESS_NOTIFICATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ADDRESS_BOOK_ID", nullable = false)
    private UserAddressBookEntity address;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @Column(name = "DIRECTION")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.AddressNotificationDirectionEnum"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private AddressNotificationDirectionEnum direction;

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

    public AddressNotificationDirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(AddressNotificationDirectionEnum direction) {
        this.direction = direction;
    }

    public NotificationTypeEntity getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEntity notificationType) {
        this.notificationType = notificationType;
    }

    public UserAddressBookEntity getAddress() {
        return address;
    }

    public void setAddress(UserAddressBookEntity address) {
        this.address = address;
    }

}
