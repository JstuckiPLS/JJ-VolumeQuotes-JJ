package com.pls.shipment.domain.bo;

import com.pls.core.domain.PhoneNumber;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Business object for shipment notification sources.
 *
 * @author Alexander Kirichenko
 */
public class ShipmentNotificationSourceItemBo implements Serializable {
    private static final long serialVersionUID = 7652277425538997052L;

    private Long id;
    private String name;
    private String contactName;
    private String email;
    private String origin;
    private PhoneNumber phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumber phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentNotificationSourceItemBo)) {
            return false;
        }

        ShipmentNotificationSourceItemBo other = (ShipmentNotificationSourceItemBo) o;

        return new EqualsBuilder().append(id, other.id).append(contactName, other.contactName).
                append(email, other.email).append(name, other.name).append(origin, other.origin).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(contactName).append(email).append(name).append(origin).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("contactName", contactName)
                .append("email", email)
                .append("origin", origin)
                .append("phone", phone)
                .toString();
    }
}
