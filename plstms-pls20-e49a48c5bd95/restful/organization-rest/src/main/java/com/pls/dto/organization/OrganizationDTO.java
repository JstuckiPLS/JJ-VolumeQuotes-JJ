package com.pls.dto.organization;

import com.pls.dto.address.AddressBookEntryDTO;

/**
 * DTO for organization.
 * 
 * @author Alexander Nalapko
 * 
 */
public class OrganizationDTO {

    private Long id;

    private String name;

    private Long ediAccount;

    private boolean active;

    private String statusReason;

    private boolean contract;

    private String federalTaxId;

    private AddressBookEntryDTO address;

    private String contactPhone;

    private String contactFax;

    private String contactFirstName;

    private String contactLastName;

    private String contactEmail;

    private Long accountExecutive;

    private String scac;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public boolean isContract() {
        return contract;
    }

    public void setContract(boolean contract) {
        this.contract = contract;
    }

    public String getFederalTaxId() {
        return federalTaxId;
    }

    public void setFederalTaxId(String federalTaxId) {
        this.federalTaxId = federalTaxId;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public Long getEdiAccount() {
        return ediAccount;
    }

    public void setEdiAccount(Long ediAccount) {
        this.ediAccount = ediAccount;
    }

    public AddressBookEntryDTO getAddress() {
        return address;
    }

    public void setAddress(AddressBookEntryDTO address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactFax() {
        return contactFax;
    }

    public void setContactFax(String contactFax) {
        this.contactFax = contactFax;
    }

    public Long getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(Long accountExecutive) {
        this.accountExecutive = accountExecutive;
    }
}
