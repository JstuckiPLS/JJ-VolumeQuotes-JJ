package com.pls.core.domain.address;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.PhoneNumber;

/**
 * Embeddable phone object contains fields to fully define voice phone.
 *
 * @author Denis Zhupinsky
 */
@Embeddable
public class PhoneEmbeddableObject implements PhoneNumber, Serializable {
    private static final long serialVersionUID = 2577632894467869258L;

    @Column(name = "PHONE_COUNTRY_CODE", nullable = false)
    private String countryCode;

    @Column(name = "PHONE_AREA_CODE", nullable = false)
    private String areaCode;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String number;

    @Column(name = "EXTENSION")
    private String extension;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PhoneEmbeddableObject) {
            final PhoneEmbeddableObject other = (PhoneEmbeddableObject) o;

            return new EqualsBuilder().append(getNumber(), other.getNumber()).append(getAreaCode(),
                    other.getAreaCode()).append(getCountryCode(), other.getCountryCode()).isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getNumber()).append(getAreaCode()).append(getCountryCode()).toHashCode();
    }
}
