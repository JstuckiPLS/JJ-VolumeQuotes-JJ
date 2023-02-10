package com.pls.core.domain.user;

import com.pls.core.domain.bo.PhoneBO;

/**
 * Additional user contact information BO.
 * 
 * @author Artem Arapov
 */
public class UserAdditionalContactInfoBO {
    private Long id;
    private String contactName;
    private String email;
    private PhoneBO phone;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
