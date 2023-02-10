package com.pls.dto.shipment;

import java.math.BigDecimal;

import com.pls.dto.enums.CommodityClassDTO;

/**
 * Manual BOL Material DTO.
 *
 * @author Alexander Nalapko
 */
public class ManualBolMaterialDTO {

    private CommodityClassDTO commodityClass;

    private Long id;

    private Integer pieces;

    private BigDecimal height;

    private BigDecimal width;

    private BigDecimal weight;

    private BigDecimal length;

    private Boolean hazmat;

    private BigDecimal quantity;

    private Boolean stackable;

    private String packageType;

    private Long referencedProductId;

    private String productCode;

    private String productDescription;

    private String nmfc;

    private String hazmatClass;

    private String emergencyResponseCompany;

    private String emergencyResponsePhoneCountryCode;

    private String emergencyResponsePhoneAreaCode;

    private String emergencyResponsePhone;

    private String emergencyResponseContractNumber;

    private String emergencyResponseInstructions;

    private String unNum;

    private String packingGroup;

    private Integer version;

    public CommodityClassDTO getCommodityClass() {
        return commodityClass;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCommodityClass(CommodityClassDTO commodityClass) {
        this.commodityClass = commodityClass;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public Boolean getHazmat() {
        return hazmat;
    }

    public void setHazmat(Boolean hazmat) {
        this.hazmat = hazmat;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Boolean getStackable() {
        return stackable;
    }

    public void setStackable(Boolean stackable) {
        this.stackable = stackable;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Long getReferencedProductId() {
        return referencedProductId;
    }

    public void setReferencedProductId(Long referencedProductId) {
        this.referencedProductId = referencedProductId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    public String getEmergencyResponseContractNumber() {
        return emergencyResponseContractNumber;
    }

    public void setEmergencyResponseContractNumber(String emergencyResponseContractNumber) {
        this.emergencyResponseContractNumber = emergencyResponseContractNumber;
    }

    public String getEmergencyResponseInstructions() {
        return emergencyResponseInstructions;
    }

    public void setEmergencyResponseInstructions(String emergencyResponseInstructions) {
        this.emergencyResponseInstructions = emergencyResponseInstructions;
    }

    public String getHazmatClass() {
        return hazmatClass;
    }

    public void setHazmatClass(String hazmatClass) {
        this.hazmatClass = hazmatClass;
    }

    public String getUnNum() {
        return unNum;
    }

    public void setUnNum(String unNum) {
        this.unNum = unNum;
    }

    public String getPackingGroup() {
        return packingGroup;
    }

    public void setPackingGroup(String packingGroup) {
        this.packingGroup = packingGroup;
    }

    public String getEmergencyResponseCompany() {
        return emergencyResponseCompany;
    }

    public void setEmergencyResponseCompany(String emergencyResponseCompany) {
        this.emergencyResponseCompany = emergencyResponseCompany;
    }

    public String getEmergencyResponsePhoneCountryCode() {
        return emergencyResponsePhoneCountryCode;
    }

    public void setEmergencyResponsePhoneCountryCode(String emergencyResponsePhoneCountryCode) {
        this.emergencyResponsePhoneCountryCode = emergencyResponsePhoneCountryCode;
    }

    public String getEmergencyResponsePhoneAreaCode() {
        return emergencyResponsePhoneAreaCode;
    }

    public void setEmergencyResponsePhoneAreaCode(String emergencyResponsePhoneAreaCode) {
        this.emergencyResponsePhoneAreaCode = emergencyResponsePhoneAreaCode;
    }

    public String getEmergencyResponsePhone() {
        return emergencyResponsePhone;
    }

    public void setEmergencyResponsePhone(String emergencyResponsePhone) {
        this.emergencyResponsePhone = emergencyResponsePhone;
    }
}
