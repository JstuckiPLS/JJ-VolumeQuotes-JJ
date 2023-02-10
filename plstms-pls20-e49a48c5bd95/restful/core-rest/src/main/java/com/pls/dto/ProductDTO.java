package com.pls.dto;

import java.math.BigDecimal;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.enums.CommodityClassDTO;

/**
 * DTO for product.
 * 
 * @author Gleb Zgonikov
 */
public class ProductDTO {

    private Long id;

    private String packageType;

    private BigDecimal weight;

    private boolean hazmat;

    private String hazmatClass;

    private String hazmatPackingGroup;

    private String hazmatUnNumber;

    private Long pieces;

    private String nmfc;

    private String nmfcSubNum;

    private String productCode;

    private CommodityClassDTO commodityClass;

    private String description;

    private String hazmatEmergencyCompany;

    private String hazmatEmergencyContract;

    private PhoneBO hazmatEmergencyPhone;

    private String hazmatInstructions;

    private boolean sharedProduct;

    private Long createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getHazmatPackingGroup() {
        return hazmatPackingGroup;
    }

    public String getNmfcSubNum() {
        return nmfcSubNum;
    }

    public void setNmfcSubNum(String nmfcSubNum) {
        this.nmfcSubNum = nmfcSubNum;
    }

    public void setHazmatPackingGroup(String hazmatPackingGroup) {
        this.hazmatPackingGroup = hazmatPackingGroup;
    }

    public String getHazmatUnNumber() {
        return hazmatUnNumber;
    }

    public void setHazmatUnNumber(String hazmatUnNumber) {
        this.hazmatUnNumber = hazmatUnNumber;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public boolean isHazmat() {
        return hazmat;
    }

    public void setHazmat(boolean hazmat) {
        this.hazmat = hazmat;
    }

    public String getHazmatClass() {
        return hazmatClass;
    }

    public void setHazmatClass(String hazmatClass) {
        this.hazmatClass = hazmatClass;
    }

    public Long getPieces() {
        return pieces;
    }

    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public CommodityClassDTO getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClassDTO commodityClass) {
        this.commodityClass = commodityClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHazmatEmergencyCompany() {
        return hazmatEmergencyCompany;
    }

    public void setHazmatEmergencyCompany(String hazmatEmergencyCompany) {
        this.hazmatEmergencyCompany = hazmatEmergencyCompany;
    }

    public String getHazmatEmergencyContract() {
        return hazmatEmergencyContract;
    }

    public void setHazmatEmergencyContract(String hazmatEmergencyContract) {
        this.hazmatEmergencyContract = hazmatEmergencyContract;
    }

    public PhoneBO getHazmatEmergencyPhone() {
        return hazmatEmergencyPhone;
    }

    public void setHazmatEmergencyPhone(PhoneBO hazmatEmergencyPhone) {
        this.hazmatEmergencyPhone = hazmatEmergencyPhone;
    }

    public String getHazmatInstructions() {
        return hazmatInstructions;
    }

    public void setHazmatInstructions(String hazmatInstructions) {
        this.hazmatInstructions = hazmatInstructions;
    }

    public boolean isSharedProduct() {
        return sharedProduct;
    }

    public void setSharedProduct(boolean isSharedProduct) {
        this.sharedProduct = isSharedProduct;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

}
