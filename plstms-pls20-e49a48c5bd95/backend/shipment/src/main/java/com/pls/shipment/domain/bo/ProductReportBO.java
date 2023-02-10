package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import com.pls.core.domain.enums.CommodityClass;

/**
 * BO to retrieve Product Report Information from DB.
 * 
 * @author Sergey Vovchuk
 */
public class ProductReportBO {
    private Long loadId;
    private BigDecimal weight;
    private BigDecimal width;
    private BigDecimal length;
    private BigDecimal height;
    private Integer pieces;
    private CommodityClass commodityClass;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
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

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
    }
}
