package com.pls.core.domain.xml.finance.customer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Address JAXB-oriented object.
 * 
 * @author Jasmin Dhamelia
 */
@XmlRootElement(name = "Address")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillToAddress implements Serializable {

    private static final long serialVersionUID = -5943703289830875905L;

    @XmlElement(name = "BillToName")
    private String billToName;

    @XmlElement(name = "Address1")
    private String address1;

    @XmlElement(name = "Address2")
    private String address2;

    @XmlElement(name = "City")
    private String city;

    @XmlElement(name = "State")
    private String state;

    @XmlElement(name = "PostalCode")
    private String postalCode;

    @XmlElement(name = "Country")
    private String country;

    public String getBillToName() {
        return billToName;
    }

    public void setBillToName(String billToName) {
        this.billToName = billToName;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getBillToName()).append(getAddress1()).append(getAddress2()).append(getCity())
                .append(getState()).append(getPostalCode()).append(getCountry());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof BillToAddress) {
            if (obj == this) {
                result = true;
            } else {
                BillToAddress other = (BillToAddress) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getBillToName(), other.getBillToName()).append(getAddress1(), other.getAddress1())
                        .append(getAddress2(), other.getAddress2()).append(getCity(), other.getCity()).append(getState(), other.getState())
                        .append(getPostalCode(), other.getPostalCode()).append(getCountry(), other.getCountry());

                result = builder.isEquals();
            }
        }
        return result;
    }

}
