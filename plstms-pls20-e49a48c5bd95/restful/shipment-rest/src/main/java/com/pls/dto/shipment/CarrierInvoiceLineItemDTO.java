package com.pls.dto.shipment;

import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dto.enums.WeightUnit;

import java.math.BigDecimal;

/**
 * DTO for carrier invoice (vendor bill) line item.
 *
 * @author Alexander Kirichenko
 */
public class CarrierInvoiceLineItemDTO {
    private Long id;

    private BigDecimal weight;

    private WeightUnit weightUnit;

    private CommodityClassDTO commodityClass;

    private Integer quantity;

    private String packageType;

    private String productDescription;

    private String nmfc;

    private Boolean hazmat;

    private BigDecimal rate;

    private BigDecimal cost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public CommodityClassDTO getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClassDTO commodityClass) {
        this.commodityClass = commodityClass;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    public Boolean getHazmat() {
        return hazmat;
    }

    public void setHazmat(Boolean hazmat) {
        this.hazmat = hazmat;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
