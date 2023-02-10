package com.pls.user.restful.dto;

import com.pls.core.domain.bo.PhoneBO;

/**
 * DTO for user phone and email information.
 * 
 * @author Aleksandr Leshchenko
 */
public class UserEmailPhoneItemDTO {
    private PhoneBO phone;
    private String email;
    private String fullName;

    public PhoneBO getPhone() {
        return phone;
    }

    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setEmail(String pEmail) {
        email = pEmail;
    }

    public void setFullName(String pFullName) {
        fullName = pFullName;
    }
}
