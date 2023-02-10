package com.pls.core.domain.bo;

import org.apache.commons.lang3.StringUtils;

/**
 * customer contact info.
 * 
 * @author Gleb Zgonikov
 */
public class ContactInfoBO {

    private String name;

    private String phone;

    private String fax;

    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns complete email contact.
     * <p>
     * For instance: 'Ltl Customer Service &ltdonotreply@plslogistics.com&gt'
     * 
     * @return formatted contact information.
     */
    public String getEmailContactInfo() {
        if (name != null && !name.isEmpty()) {
            return StringUtils.join(name, " <", email, ">");
        } else {
            return email;
        }
    }
}
