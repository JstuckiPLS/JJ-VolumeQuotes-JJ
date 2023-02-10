package com.pls.core.domain.bo;

import java.io.Serializable;

/**
 * BO for represent customer info in table views.
 *
 * @author Alexander Kirichenko
 */
public class CustomerListItemBO implements Serializable {
    private static final long serialVersionUID = -6528381755563962198L;

    private String name;

    private String contactFirstName;

    private String contactLastName;

    private String areaCode;

    private String dialingCode;

    private String phoneNumber;

    private String extension;

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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getDialingCode() {
        return dialingCode;
    }

    public void setDialingCode(String dialingCode) {
        this.dialingCode = dialingCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
