package com.pls.dto;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.address.PlainAddressDTO;

/**
 * Ltl Terminal Info DTO.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlTerminalInfoDTO {

    private Long id;

    private Long profileId;

    private String terminal;

    private String contactName;

    private Long transiteTime;

    private PlainAddressDTO address;

    private PhoneBO phone;

    private PhoneBO fax;

    private String email;

    private String accountNum;

    private Boolean visible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public PlainAddressDTO getAddress() {
        return address;
    }

    public void setAddress(PlainAddressDTO address) {
        this.address = address;
    }

    public Long getTransiteTime() {
        return transiteTime;
    }

    public void setTransiteTime(Long transiteTime) {
        this.transiteTime = transiteTime;
    }

    public PhoneBO getPhone() {
        return phone;
    }

    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }

    public PhoneBO getFax() {
        return fax;
    }

    public void setFax(PhoneBO fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    private Long version;
}
