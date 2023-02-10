package com.pls.dto;

/**
 * DTO for contact infromation.
 * 
 * @author Gleb Zgonikov
 */
public class ContactInfoSetDTO {

    private ContactInfoDTO salesRep;

    private ContactInfoDTO customerRep;

    private ContactInfoDTO plsCorporate;

    private ContactInfoDTO terminal;

    public ContactInfoDTO getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(ContactInfoDTO salesRep) {
        this.salesRep = salesRep;
    }

    public ContactInfoDTO getCustomerRep() {
        return customerRep;
    }

    public void setCustomerRep(ContactInfoDTO customerRep) {
        this.customerRep = customerRep;
    }

    public ContactInfoDTO getPlsCorporate() {
        return plsCorporate;
    }

    public void setPlsCorporate(ContactInfoDTO plsCorporate) {
        this.plsCorporate = plsCorporate;
    }

    public ContactInfoDTO getTerminal() {
        return terminal;
    }

    public void setTerminal(ContactInfoDTO terminal) {
        this.terminal = terminal;
    }
}
