package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.enums.CommodityClass;

/**
 * Materials Criteria class to get the selected rates.
 * @author Hima Bindu Challa
 *
 */
public class RateMaterialCO implements Serializable {

    private static final long serialVersionUID = -32356217357123123L;

    private BigDecimal weight;
    private String weightUnit;
    private CommodityClass commodityClassEnum;
    private String commodityClass;
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal length;
    private String dimensionUnit;
    private Integer quantity;
    private Integer pieces;
    private String packageType;
    private String hazmat;
    private Boolean hazmatBool;
    private String nmfc;

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    public String getWeightUnit() {
        return weightUnit;
    }
    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }
    public CommodityClass getCommodityClassEnum() {
        return commodityClassEnum;
    }
    public void setCommodityClassEnum(CommodityClass commodityClassEnum) {
        this.commodityClassEnum = commodityClassEnum;
    }
    public String getCommodityClass() {
        return commodityClass;
    }
    public void setCommodityClass(String commodityClass) {
        this.commodityClass = commodityClass;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }
    public String getDimensionUnit() {
        return dimensionUnit;
    }
    public void setDimensionUnit(String dimensionUnit) {
        this.dimensionUnit = dimensionUnit;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Integer getPieces() {
        return pieces;
    }
    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }
    public String getPackageType() {
        return packageType;
    }
    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }
    public Boolean getHazmatBool() {
        return hazmatBool;
    }
    public void setHazmatBool(Boolean hazmatBool) {
        this.hazmatBool = hazmatBool;
    }
    public String getHazmat() {
        return hazmat;
    }
    public void setHazmat(String hazmat) {
        this.hazmat = hazmat;
    }
    public String getNmfc() {
        return nmfc;
    }
    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("weight", weight)
                .append("weightUnit", weightUnit)
                .append("commodityClassEnum", commodityClassEnum)
                .append("commodityClass", commodityClass)
                .append("height", height)
                .append("width", width)
                .append("length", length)
                .append("dimensionUnit", dimensionUnit)
                .append("quantity", quantity)
                .append("pieces", pieces)
                .append("packageType", packageType)
                .append("hazmat", hazmat)
                .append("hazmatBool", hazmatBool)
                .append("nmfc", nmfc);

        return builder.toString();
    }

    public BigDecimal getHeightInInches() {
        return getDimensionInInches(getHeight());
    }

    public BigDecimal getWidthInInches() {
        return getDimensionInInches(getWidth());
    }

    public BigDecimal getLengthInInches() {
        return getDimensionInInches(getLength());
    }
    
    private BigDecimal getDimensionInInches(BigDecimal value) {
        if(getDimensionUnit()==null) {
            return value;
        }
        switch (getDimensionUnit()) {
        case "INCH":
            return value;
        case "CMM":
            return value.multiply(new BigDecimal("0.393701"));
        case "M":
            return value.multiply(new BigDecimal("39.3701"));
        case "FT":
            return value.multiply(new BigDecimal(12));
        default:
            return value;
        }
    }

    public BigDecimal getWeightInLBS() {
        if(getWeightUnit()==null) {
            return getWeight();
        }
        switch (getWeightUnit()) {
        case "LBS":
            return getWeight();
        case "KG":
            return getWeight().multiply(new BigDecimal("2.20462"));
        case "G":
            return getWeight().multiply(new BigDecimal("0.00220462"));
        case "OZ":
            return getWeight().multiply(new BigDecimal("0.0625"));
        default:
            return getWeight();
        }
    }
}
