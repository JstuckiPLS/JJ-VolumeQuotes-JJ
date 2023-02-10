package com.pls.user.restful.dto;


/**
 * User email.
 * 
 * @author Brichak Aleksandr
 */
public class UserEmailItemDTO {

    private String email;
    private String fullName;

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
