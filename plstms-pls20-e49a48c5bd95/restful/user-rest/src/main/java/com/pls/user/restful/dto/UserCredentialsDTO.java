package com.pls.user.restful.dto;
/**
 * Data transfer object to manage user credentials.
 * 
 * @author Dmitriy Nefedchenko
 *
 */
public class UserCredentialsDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmedPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getConfirmedPassword() {
        return confirmedPassword;
    }
    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }
}
