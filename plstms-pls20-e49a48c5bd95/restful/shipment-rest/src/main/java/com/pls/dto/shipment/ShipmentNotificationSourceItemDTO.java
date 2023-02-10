package com.pls.dto.shipment;

import com.pls.core.domain.bo.PhoneBO;

/**
 * DTO object used for selecting email for adding shipment notifications.
 *
 * @author Alexander Kirichenko
 */
public class ShipmentNotificationSourceItemDTO {
    private Long id;
    private String contactName;
    private String name;
    private String email;
    private String origin;
    private PhoneBO phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PhoneBO getPhone() {
        return phone;
    }

    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
