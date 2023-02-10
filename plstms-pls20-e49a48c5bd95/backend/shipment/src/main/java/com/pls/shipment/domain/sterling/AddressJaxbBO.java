package com.pls.shipment.domain.sterling;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.YesNo;

/**
 * Class designed for address fields.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "Address")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressJaxbBO implements Serializable {

    private static final long serialVersionUID = -8625103105490994895L;

    @XmlElement(name = "AddressType")
    private AddressType addressType;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "AddressCode")
    private String addressCode;

    @XmlElement(name = "Address1")
    private String address1;

    @XmlElement(name = "Address2")
    private String address2;

    @XmlElement(name = "City")
    private String city;

    @XmlElement(name = "StateCode")
    private String stateCode;

    @XmlElement(name = "PostalCode")
    private String postalCode;

    @XmlElement(name = "CountryCode")
    private String countryCode;

    @XmlElement(name = "ContactName")
    private String contactName;

    @XmlElement(name = "ContactPhone")
    private String contactPhone;

    @XmlElement(name = "ContactFax")
    private String contactFax;

    @XmlElement(name = "ContactEmail")
    private String contactEmail;

    @XmlElement(name = "AccountNum")
    private String accountNum;

    @XmlElement(name = "Notes")
    private String notes;

    @XmlElement(name = "TransitDate")
    private TransitDateJaxbBO transitDate;

    @XmlElement(name = "AddToAddressBook")
    private YesNo addToAddressBook;

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TransitDateJaxbBO getTransitDate() {
        return transitDate;
    }

    public void setTransitDate(TransitDateJaxbBO transitDate) {
        this.transitDate = transitDate;
    }

    public YesNo getAddToAddressBook() {
        return addToAddressBook;
    }

    public void setAddToAddressBook(YesNo addToAddressBook) {
        this.addToAddressBook = addToAddressBook;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getAddressType()).append(getName()).append(getAddressCode()).append(getAddress1())
                .append(getAddress2()).append(getCity()).append(getStateCode()).append(getPostalCode()).append(getCountryCode())
                .append(getContactName()).append(getContactPhone()).append(getContactFax()).append(getContactEmail()).append(getNotes())
                .append(getTransitDate()).append(getAddToAddressBook()).append(getAccountNum());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof AddressJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                AddressJaxbBO other = (AddressJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getAddressType(), other.getAddressType()).append(getName(), other.getName())
                        .append(getAddressCode(), other.getAddressCode()).append(getAddress1(), other.getAddress1())
                        .append(getAddress2(), other.getAddress2()).append(getCity(), other.getCity()).append(getStateCode(), other.getStateCode())
                        .append(getPostalCode(), other.getPostalCode()).append(getCountryCode(), other.getCountryCode())
                        .append(getContactName(), other.getContactName()).append(getContactPhone(), other.getContactPhone())
                        .append(getContactFax(), other.getContactFax()).append(getContactEmail(), other.getContactEmail())
                        .append(getNotes(), other.getNotes()).append(getTransitDate(), other.getTransitDate())
                        .append(getAddToAddressBook(), other.getAddToAddressBook()).append(getAccountNum(), other.getAccountNum());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("AddressType", getAddressType()).append("Name", getName())
                .append("AddressCode", getAddressCode()).append("Address1", getAddress1()).append("Address2", getAddress2())
                .append("City", getCity()).append("StateCode", getStateCode()).append("PostalCode", getPostalCode())
                .append("CountryCode", getCountryCode()).append("ContactName", getContactName()).append("ContactPhone", getContactPhone())
                .append("ContactFax", getContactFax()).append("ContactEmail", getContactEmail()).append("Notes", getNotes())
                .append("TransitDate", getTransitDate()).append("AddToAddressBook", getAddToAddressBook()).append("Account", getAccountNum());

        return builder.toString();
    }
}
