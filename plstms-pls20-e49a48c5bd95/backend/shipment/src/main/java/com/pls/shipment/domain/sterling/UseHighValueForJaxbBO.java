package com.pls.shipment.domain.sterling;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.shipment.domain.sterling.enums.YesNo;

/**
 * Use high value for class has Miles, Revenue and Cost parameters.
 * 
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "UseHighValueFor")
@XmlAccessorType(XmlAccessType.FIELD)
public class UseHighValueForJaxbBO implements Serializable {

    private static final long serialVersionUID = -6854574976441278960L;

    @XmlElement(name = "Miles")
    private YesNo miles;

    @XmlElement(name = "Revenue")
    private YesNo revenue;

    @XmlElement(name = "Cost")
    private YesNo cost;

    public YesNo getMiles() {
        return miles;
    }

    public void setMiles(YesNo miles) {
        this.miles = miles;
    }

    public YesNo getRevenue() {
        return revenue;
    }

    public void setRevenue(YesNo revenue) {
        this.revenue = revenue;
    }

    public YesNo getCost() {
        return cost;
    }

    public void setCost(YesNo cost) {
        this.cost = cost;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getMiles()).append(getRevenue()).append(getCost());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof UseHighValueForJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                UseHighValueForJaxbBO other = (UseHighValueForJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getMiles(), other.getMiles()).append(getRevenue(), other.getRevenue())
                        .append(getCost(), other.getCost());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Miles", getMiles()).append("Revenue", getRevenue()).append("Cost", getCost());

        return builder.toString();
    }
}
