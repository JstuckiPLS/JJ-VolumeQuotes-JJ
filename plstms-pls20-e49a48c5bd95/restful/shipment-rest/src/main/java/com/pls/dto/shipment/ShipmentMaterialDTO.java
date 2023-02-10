package com.pls.dto.shipment;

import java.math.BigDecimal;
import java.util.Date;

import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dto.enums.DimensionUnit;
import com.pls.dto.enums.WeightUnit;

/**
 * Shipment Material DTO.
 *
 * @author Alexander Kirichenko
 */
public class ShipmentMaterialDTO {
    private Long id;

    private BigDecimal weight;

    private WeightUnit weightUnit;

    private Integer pieces;

    private CommodityClassDTO commodityClass;

    private BigDecimal height;

    private BigDecimal width;

    private BigDecimal length;

    private DimensionUnit dimensionUnit;

    private String quantity;

    private String packageType;

    private String productCode;

    private String productDescription;

    private Long productId;

    private String nmfc;

    private Boolean hazmat;

    private Boolean stackable;

    private Date pickupDate;

    private String unNum;

    private String packingGroup;

    private String hazmatClass;

    private String emergencyResponseCompany;

    private String emergencyResponsePhoneCountryCode;

    private String emergencyResponsePhoneAreaCode;

    private String emergencyResponsePhone;

    private String emergencyResponseContractNumber;

    private String emergencyResponseInstructions;

    private String emergencyResponsePhoneExtension;

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

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public CommodityClassDTO getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClassDTO commodityClass) {
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

    public DimensionUnit getDimensionUnit() {
        return dimensionUnit;
    }

    public void setDimensionUnit(DimensionUnit dimensionUnit) {
        this.dimensionUnit = dimensionUnit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public Boolean getStackable() {
        return stackable;
    }

    public void setStackable(Boolean stackable) {
        this.stackable = stackable;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
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

    public String getHazmatClass() {
        return hazmatClass;
    }

    public void setHazmatClass(String hazmatClass) {
        this.hazmatClass = hazmatClass;
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getEmergencyResponsePhoneExtension() {
        return emergencyResponsePhoneExtension;
    }

    public void setEmergencyResponsePhoneExtension(String emergencyResponsePhoneExtension) {
        this.emergencyResponsePhoneExtension = emergencyResponsePhoneExtension;
    }
}
