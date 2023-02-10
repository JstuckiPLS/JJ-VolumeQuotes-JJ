package com.pls.ltlrating.shared;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.bo.PhoneBO;

/**
 * The result object that contains terminal information for the selected carrier.
 * @author Hima Bindu Challa
 *
 */
public class TerminalContactVO implements Serializable {

    private static final long serialVersionUID = -872352365273576237L;

    private String contact;
    private String contactEmail;
    private String name;
    private PhoneBO phone;

    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PhoneBO getPhone() {
        return phone;
    }
    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("Contact", contact)
                .append("Contact Email", contactEmail)
                .append("Name", name)
                .append("Phone", phone);

        return builder.toString();
    }
}
