package com.pls.dto;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.address.AddressBookEntryDTO;

/**
 * FreightBillPayTo DTO.
 * 
 * @author Aleksandr Leshchenko
 */
public class FreightBillPayToDTO {
    private Long id;
    private String company;
    private String contactName;
    private String accountNum;
    private AddressBookEntryDTO address;
    private PhoneBO phone;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public AddressBookEntryDTO getAddress() {
        return address;
    }

    public void setAddress(AddressBookEntryDTO address) {
        this.address = address;
    }

    public PhoneBO getPhone() {
        return phone;
    }

    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
