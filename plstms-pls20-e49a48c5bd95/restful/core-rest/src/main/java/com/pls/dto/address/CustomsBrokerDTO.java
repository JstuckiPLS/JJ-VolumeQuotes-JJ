package com.pls.dto.address;

import com.pls.core.domain.bo.PhoneBO;

/**
 * DTO to extend bill to options with customs broker info.
 *
 * @author Gleb Zgonikov
 */
public class CustomsBrokerDTO {

    private String name;

    private PhoneBO phone;

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
}
