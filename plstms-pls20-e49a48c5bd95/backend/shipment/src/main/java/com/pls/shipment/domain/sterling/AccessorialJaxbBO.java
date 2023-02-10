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
import com.pls.shipment.domain.sterling.enums.YesNo;

/**
 * Class designed for Accessorial fields.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "Accessorial")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessorialJaxbBO implements Serializable {

    private static final long serialVersionUID = 1014291390161716417L;

    @XmlElement(name = "Code")
    private String code;

    @XmlElement(name = "Description")
    private String description;

    @XmlElement(name = "Group")
    private String group;

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

    @XmlElement(name = "AutoApplied")
    private YesNo autoApplied;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
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

    public YesNo getAutoApplied() {
        return autoApplied;
    }

    public void setAutoApplied(YesNo autoApplied) {
        this.autoApplied = autoApplied;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getCode()).append(getDescription()).append(getGroup()).append(getRateType())
                .append(getUnitCost()).append(getUnitType()).append(getQuantity()).append(getSubTotal()).append(getComment())
                .append(getAutoApplied());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof AccessorialJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                AccessorialJaxbBO other = (AccessorialJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getCode(), other.getCode()).append(getDescription(), other.getDescription())
                        .append(getGroup(), other.getGroup()).append(getRateType(), other.getRateType()).append(getUnitCost(), other.getUnitCost())
                        .append(getUnitType(), other.getUnitType()).append(getQuantity(), other.getQuantity())
                        .append(getSubTotal(), other.getSubTotal()).append(getComment(), other.getComment())
                        .append(getAutoApplied(), other.getAutoApplied());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Code", getCode()).append("Description", getDescription())
                .append("Group", getGroup()).append("RateType", getRateType()).append("UnitCost", getUnitCost()).append("UnitType", getUnitType())
                .append("Quantity", getQuantity()).append("SubTotal", getSubTotal()).append("Comment", getComment())
                .append("AutoApplied", getAutoApplied());

        return builder.toString();
    }

}
