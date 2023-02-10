package com.pls.shipment.domain.sterling;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.shipment.domain.sterling.enums.RateType;

/**
 * Base Rate information.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "BaseRate")
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseRateJaxbBO implements Serializable {

    private static final long serialVersionUID = -4614586913860171059L;

    @XmlElement(name = "RateType")
    private RateType rateType;

    @XmlElement(name = "UnitCost")
    private BigDecimal unitCost;

    @XmlElement(name = "UnitType")
    private String unitType;

    @XmlElement(name = "Quantity")
    private Long quantity;

    @XmlElement(name = "Subtotal")
    private BigDecimal subTotal;

    @XmlElement(name = "Comment")
    private String comment;

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getRateType()).append(getUnitCost()).append(getUnitType()).append(getQuantity())
                .append(getSubTotal()).append(getComment());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof BaseRateJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                BaseRateJaxbBO other = (BaseRateJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getRateType(), other.getRateType()).append(getUnitCost(), other.getUnitCost())
                        .append(getUnitType(), other.getUnitType()).append(getQuantity(), other.getQuantity())
                        .append(getSubTotal(), other.getSubTotal()).append(getComment(), other.getComment());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("RateType", getRateType()).append("UnitCost", getUnitCost())
                .append("UnitType", getUnitType()).append("Quantity", getQuantity()).append("SubTotal", getSubTotal())
                .append("Comment", getComment());

        return builder.toString();
    }
}
