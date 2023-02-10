package com.pls.shipment.domain.sterling;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Load Tracking status address class. Class designed to store address information for EDI 214 inbound communication.
 * 
 * @author Jasmin Dhamelia
 */
@XmlRootElement(name = "TrackingStatusAddress")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrackingStatusAddressJaxbBO implements Serializable {

    private static final long serialVersionUID = -723717304937681007L;

    @XmlElement(name = "City")
    private String city;

    @XmlElement(name = "State")
    private String state;

    @XmlElement(name = "Country")
    private String country;

    @XmlElement(name = "PostalCode")
    private String postalCode;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TrackingStatusAddressJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                TrackingStatusAddressJaxbBO other = (TrackingStatusAddressJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getCity(), other.getCity());
                builder.append(getState(), other.getState());
                builder.append(getCountry(), other.getCountry());
                builder.append(getPostalCode(), other.getPostalCode());
                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getCity());
        builder.append(getState());
        builder.append(getCountry());
        builder.append(getPostalCode());
        return builder.toHashCode();
    }

}