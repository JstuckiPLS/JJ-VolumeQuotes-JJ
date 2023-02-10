package com.pls.dto.organization;

import com.pls.core.domain.bo.PhoneBO;

/**
 * DTO for represent customer info in table views.
 * 
 * @author Aleksandr Leshchenko
 */
public class CustomerListItemDTO {

    private String name;

    private String contactFirstName;

    private String contactLastName;

    private PhoneBO phone;

    private String email;

    private Long id;

    private String ediNumber;

    private Boolean contract;

    private String accountExecutive;

    private boolean multipleAccountExecitive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEdiNumber() {
        return ediNumber;
    }

    public void setEdiNumber(String ediNumber) {
        this.ediNumber = ediNumber;
    }

    public Boolean isContract() {
        return contract;
    }

    public void setContract(Boolean contract) {
        this.contract = contract;
    }

    public String getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(String accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public boolean isMultipleAccountExecitive() {
        return multipleAccountExecitive;
    }

    public void setMultipleAccountExecitive(boolean multipleAccountExecitive) {
        this.multipleAccountExecitive = multipleAccountExecitive;
    }
}
