package com.pls.dto;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.address.PlainAddressDTO;

/**
 * Ltl Third Party Info DTO.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlThirdPartyInfoDTO {

    private Long id;

    private Long profileDetailId;

    private String company;

    private String contactName;

    private PlainAddressDTO address;

    private PhoneBO phone;

    private PhoneBO fax;

    private String email;

    private String accountNum;

    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileDetailId() {
        return profileDetailId;
    }

    public void setProfileDetailId(Long profileDetailId) {
        this.profileDetailId = profileDetailId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
