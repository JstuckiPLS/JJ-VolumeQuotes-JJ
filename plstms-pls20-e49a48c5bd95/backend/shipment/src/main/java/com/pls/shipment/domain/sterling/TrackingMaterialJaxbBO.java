package com.pls.shipment.domain.sterling;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Load Tracking material class. Class designed to store tracking material info. for EDI 214 inbound communication.
 * 
 * @author Jasmin Dhamelia
 */
@XmlRootElement(name = "TrackingMaterial")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrackingMaterialJaxbBO implements Serializable {

    private static final long serialVersionUID = -452841896351131221L;

    @XmlElement(name = "Weight")
    private Long weight;

    @XmlElement(name = "CommodityClassCode")
    private String commodityClassCode;

    @XmlElement(name = "PackagingType")
    private String packagingType;

    @XmlElement(name = "Pieces")
    private Long pieces;

    @XmlElement(name = "Quanity")
    private Long quanity;

    @XmlElement(name = "WeightUOM")
    private String weightUOM;

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public String getCommodityClassCode() {
        return commodityClassCode;
    }

    public void setCommodityClassCode(String commodityClassCode) {
        this.commodityClassCode = commodityClassCode;
    }

    public String getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(String packagingType) {
        this.packagingType = packagingType;
    }

    public Long getPieces() {
        return pieces;
    }

    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }

    public Long getQuanity() {
        return quanity;
    }

    public void setQuanity(Long quanity) {
        this.quanity = quanity;
    }

    public String getWeightUOM() {
        return weightUOM;
    }

    public void setWeightUOM(String weightUOM) {
        this.weightUOM = weightUOM;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TrackingMaterialJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                TrackingMaterialJaxbBO other = (TrackingMaterialJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getWeight(), other.getWeight());
                builder.append(getCommodityClassCode(), other.getCommodityClassCode());
                builder.append(getPackagingType(), other.getPackagingType());
                builder.append(getPieces(), other.getPieces());
                builder.append(getQuanity(), other.getQuanity());
                builder.append(getWeightUOM(), other.getWeightUOM());
                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getWeight());
        builder.append(getCommodityClassCode());
        builder.append(getPackagingType());
        builder.append(getPieces());
        builder.append(getQuanity());
        builder.append(getWeightUOM());
        return builder.toHashCode();
    }

}
