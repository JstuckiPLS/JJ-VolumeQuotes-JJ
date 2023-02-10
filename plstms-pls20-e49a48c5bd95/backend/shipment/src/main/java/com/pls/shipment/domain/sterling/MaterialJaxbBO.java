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

import com.pls.shipment.domain.sterling.enums.DimensionsUOM;
import com.pls.shipment.domain.sterling.enums.WeightUOM;
import com.pls.shipment.domain.sterling.enums.YesNo;

/**
 * Class comprises of fields required for specific type of material.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "Material")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialJaxbBO implements Serializable {

    private static final long serialVersionUID = 8958620952822972963L;

    @XmlElement(name = "Weight")
    private BigDecimal weight;

    @XmlElement(name = "CommodityClassCode")
    private String commodityClassCode;

    @XmlElement(name = "ProductCode")
    private String productCode;

    @XmlElement(name = "ProductDesc")
    private String productDesc;

    @XmlElement(name = "Length")
    private BigDecimal length;

    @XmlElement(name = "Width")
    private BigDecimal width;

    @XmlElement(name = "Height")
    private BigDecimal height;

    @XmlElement(name = "Quantity")
    private Integer quantity;

    @XmlElement(name = "PackagingType")
    private String packagingType;

    @XmlElement(name = "PackagingDesc")
    private String packagingDesc;

    @XmlElement(name = "Stackable")
    private YesNo stackable = YesNo.NO;

    @XmlElement(name = "Pieces")
    private Integer pieces;

    @XmlElement(name = "Nmfc")
    private String nmfc;

    @XmlElement(name = "DimensionsUOM")
    private DimensionsUOM dimensionsUOM = DimensionsUOM.INCH;

    @XmlElement(name = "WeightUOM")
    private WeightUOM weightUOM = WeightUOM.LBS;

    @XmlElement(name = "Hazmat")
    private YesNo hazmat = YesNo.NO;

    @XmlElement(name = "HazmatInfo")
    private HazmatInfoJaxbBO hazmatInfo;

    @XmlElement(name = "OrderNum")
    private Integer orderNum;

    @XmlElement(name = "Subtotal")
    private BigDecimal subtotal;

    @XmlElement(name = "SpecialChargeCode")
    private String specialChargeCode;

    @XmlElement(name = "UnitCost")
    private BigDecimal unitCost;

    @XmlElement(name = "AddToProducts")
    private YesNo addToProducts = YesNo.NO;

    public HazmatInfoJaxbBO getHazmatInfo() {
        return hazmatInfo;
    }

    public void setHazmatInfo(HazmatInfoJaxbBO hazmatInfo) {
        this.hazmatInfo = hazmatInfo;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getCommodityClassCode() {
        return commodityClassCode;
    }

    public void setCommodityClassCode(String commodityClassCode) {
        this.commodityClassCode = commodityClassCode;
    }

    /**
     * Returns if the material is a hazardous material. Default value is "NO".
     * 
     * @return if the material is hazmat.
     */
    public YesNo getHazmat() {
        if (hazmat == null) {
            hazmat = YesNo.NO;
        }
        return hazmat;
    }

    public void setHazmat(YesNo hazmat) {
        this.hazmat = hazmat;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(String packagingType) {
        this.packagingType = packagingType;
    }

    public String getPackagingDesc() {
        return packagingDesc;
    }

    public void setPackagingDesc(String packagingDesc) {
        this.packagingDesc = packagingDesc;
    }

    /**
     * Returns if the material is stackable. If null, returns "No".
     * 
     * @return if the material is stackable.
     */
 public YesNo getStackable() {
        if (stackable == null) {
            stackable = YesNo.NO;
        }
        return stackable;
    }

    public void setStackable(YesNo stackable) {
        this.stackable = stackable;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    /**
     * Returns the Unit of Measure for dimensions. Default is "INCHES".
     * 
     * @return the unit of measure for dimensions.
     */
    public DimensionsUOM getDimensionsUOM() {
        if (dimensionsUOM == null) {
            dimensionsUOM = DimensionsUOM.INCH;
        }
        return dimensionsUOM;
    }

    public void setDimensionsUOM(DimensionsUOM dimensionsUOM) {
        this.dimensionsUOM = dimensionsUOM;
    }

    /**
     * Returns the unit of measure for Weight. Default is "LBS".
     * 
     * @return unit of measure for weight.
     */
    public WeightUOM getWeightUOM() {
        if (weightUOM == null) {
            weightUOM = WeightUOM.LBS;
        }
        return weightUOM;
    }

    public void setWeightUOM(WeightUOM weightUOM) {
        this.weightUOM = weightUOM;
    }

    public YesNo getAddToProducts() {
        return addToProducts;
    }

    public void setAddToProducts(YesNo addToProducts) {
        this.addToProducts = addToProducts;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getSpecialChargeCode() {
        return specialChargeCode;
    }

    public void setSpecialChargeCode(String specialChargeCode) {
        this.specialChargeCode = specialChargeCode;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getOrderNum()).append(getSpecialChargeCode()).append(getSubtotal())
                .append(getUnitCost()).append(getWeight()).append(getWeightUOM()).append(getCommodityClassCode()).append(getProductCode())
                .append(getProductDesc()).append(getLength()).append(getHeight()).append(getWidth()).append(getDimensionsUOM()).append(getQuantity())
                .append(getPieces()).append(getPackagingType()).append(getPackagingDesc()).append(getStackable()).append(getNmfc())
                .append(getHazmat()).append(getHazmatInfo()).append(getAddToProducts());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof MaterialJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                MaterialJaxbBO other = (MaterialJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getOrderNum(), other.getOrderNum())
                        .append(getSpecialChargeCode(), other.getSpecialChargeCode()).append(getSubtotal(), other.getSubtotal())
                        .append(getUnitCost(), other.getUnitCost()).append(getWeight(), other.getWeight())
                        .append(getWeightUOM(), other.getWeightUOM()).append(getCommodityClassCode(), other.getCommodityClassCode())
                        .append(getProductCode(), other.getProductCode()).append(getProductDesc(), other.getProductDesc())
                        .append(getLength(), other.getLength()).append(getHeight(), other.getHeight()).append(getWidth(), other.getWidth())
                        .append(getDimensionsUOM(), other.getDimensionsUOM()).append(getQuantity(), other.getQuantity())
                        .append(getPieces(), other.getPieces()).append(getPackagingType(), other.getPackagingType())
                        .append(getPackagingDesc(), other.getPackagingDesc()).append(getStackable(), other.getStackable())
                        .append(getNmfc(), other.getNmfc()).append(getHazmat(), other.getHazmat()).append(getHazmatInfo(), other.getHazmatInfo())
                        .append(getAddToProducts(), other.getAddToProducts());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("OrderNum", getOrderNum()).append("SpecialChargeCode", getSpecialChargeCode())
                .append("Subtotal", getSubtotal()).append("UnitCost", getUnitCost()).append("Weight", getWeight())
                .append("WeightUOM", getWeightUOM()).append("CommodityClassCode", getCommodityClassCode()).append("ProductCode", getProductCode())
                .append("ProductDesc", getProductDesc()).append("Length", getLength()).append("Width", getWidth()).append("Height", getHeight())
                .append("DimensionsUOM", getDimensionsUOM()).append("Quantity", getQuantity()).append("Pieces", getPieces())
                .append("PackagingType", getPackagingType()).append("PackagingDesc", getPackagingDesc()).append("Stackable", getStackable())
                .append("Nmfc", getNmfc()).append("Hazmat", getHazmat()).append("HazmatInfo", getHazmatInfo())
                .append("AddToProducts", getAddToProducts());

        return builder.toString();
    }
}
