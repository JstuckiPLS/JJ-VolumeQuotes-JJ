package com.pls.shipment.domain.sterling;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The Holder of the two String Qualifier and Number which is not being used as the date of 4/21/2015.
 * 
 * @author Yasaman Honarvar
 *
 */

@XmlRootElement(name = "AddlRefNumber")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddlRefNumberJaxbBO implements Serializable {

    private static final long serialVersionUID = -1263106778594984360L;

    @XmlElement(name = "Qualifier")
    private String qualifier;

    @XmlElement(name = "Number")
    private String number;

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getNumber()).append(getQualifier());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof AddlRefNumberJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                AddlRefNumberJaxbBO other = (AddlRefNumberJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getNumber(), other.getNumber()).append(getQualifier(), other.getQualifier());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Number", getNumber()).append("Qualifier", getQualifier());

        return builder.toString();
    }
}
