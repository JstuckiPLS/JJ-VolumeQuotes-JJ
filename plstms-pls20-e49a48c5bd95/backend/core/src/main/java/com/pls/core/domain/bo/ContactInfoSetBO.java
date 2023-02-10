package com.pls.core.domain.bo;

/**
 * Customer contact info.
 * 
 * @author Gleb Zgonikov
 */
public class ContactInfoSetBO {

    private ContactInfoBO salesRep;

    private ContactInfoBO customerRep;

    private ContactInfoBO plsCorporate;

    private ContactInfoBO terminal;

    public ContactInfoBO getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(ContactInfoBO salesRep) {
        this.salesRep = salesRep;
    }

    public ContactInfoBO getCustomerRep() {
        return customerRep;
    }

    public void setCustomerRep(ContactInfoBO customerRep) {
        this.customerRep = customerRep;
    }

    public ContactInfoBO getPlsCorporate() {
        return plsCorporate;
    }

    public void setPlsCorporate(ContactInfoBO plsCorporate) {
        this.plsCorporate = plsCorporate;
    }

    public ContactInfoBO getTerminal() {
        return terminal;
    }

    public void setTerminal(ContactInfoBO terminal) {
        this.terminal = terminal;
    }

}
