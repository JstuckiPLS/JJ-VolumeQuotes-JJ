package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.enums.CommodityClass;

/**
 * Manual BOL Material Entity.
 *
 * @author Alexander Nalapko
 */
@Entity
@Table(name = "MANUAL_BOL_MATERIALS")
public class ManualBolMaterialEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -1304045319666718948L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_bol_materials_sequence")
    @SequenceGenerator(name = "manual_bol_materials_sequence", sequenceName = "MANUAL_BOL_MATERIALS_SEQ", allocationSize = 1)
    @Column(name = "MANUAL_BOL_MATERIAL_ID")
    private Long id;

    @Column(name = "COMMODITY_CLASS_CODE", nullable = true)
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass commodityClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANUAL_BOL_ID", nullable = false)
    private ManualBolEntity manualBol;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "PIECES", nullable = true, columnDefinition = "NUMBER(6,0)")
    private Integer pieces;

    @Column(name = "HEIGHT", nullable = true, columnDefinition = "NUMBER(10,2)")
    private BigDecimal height;

    @Column(name = "WIDTH", nullable = true, columnDefinition = "NUMBER(10,2)")
    private BigDecimal width;

    @Column(name = "WEIGHT", nullable = false, columnDefinition = "NUMBER(13,3)")
    private BigDecimal weight;

    @Column(name = "LENGTH", nullable = true, columnDefinition = "NUMBER(15,5)")
    private BigDecimal length;

    @Column(name = "QUANTITY")
    private BigDecimal quantity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PICKUP_DATE")
    private Date pickupDate;

    @Column(name = "STACKABLE")
    @Type(type = "yes_no")
    private boolean stackable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LTL_PACKAGE_TYPE", referencedColumnName = "PACKAGE_TYPE")
    private PackageTypeEntity packageType;

    @Column(name = "LTL_PRODUCT_ID")
    private Long referencedProductId;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PART_DESCRIPTION")
    private String productDescription;

    @Column(name = "NMFC")
    private String nmfc;

    @Column(name = "HAZMAT")
    @Type(type = "yes_no")
    private boolean hazmat;

    @Column(name = "EMERGENCY_CONTRACT", nullable = true)
    private String emergencyContract;

    @Column(name = "EMERGENCY_COMPANY", nullable = true)
    private String emergencyCompany;

    @Column(name = "HAZMAT_INSTRUCTIONS")
    private String hazmatInstruction;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "countryCode",
                    column = @Column(name = "EMERGENCY_COUNTRY_CODE", nullable = true)),
            @AttributeOverride(name = "areaCode",
                    column = @Column(name = "EMERGENCY_AREA_CODE", nullable = true)),
            @AttributeOverride(name = "number",
                    column = @Column(name = "EMERGENCY_NUMBER", nullable = true)),
            @AttributeOverride(name = "extension",
                    column = @Column(name = "EMERGENCY_EXTENSION", nullable = true))
    })
    private PhoneEmbeddableObject emergencyPhone = new PhoneEmbeddableObject();

    @Column(name = "HAZMAT_CLASS")
    private String hazmatClass;

    @Column(name = "UN_NUM")
    private String unNumber;

    @Column(name = "PACKING_GROUP")
    private String packingGroup;

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
    }

    public ManualBolEntity getManualBol() {
        return manualBol;
    }

    public void setManualBol(ManualBolEntity manualBol) {
        this.manualBol = manualBol;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public boolean isHazmat() {
        return hazmat;
    }

    public void setHazmat(boolean hazmat) {
        this.hazmat = hazmat;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public PackageTypeEntity getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageTypeEntity packageType) {
        this.packageType = packageType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getHazmatClass() {
        return hazmatClass;
    }

    public void setHazmatClass(String hazmatClass) {
        this.hazmatClass = hazmatClass;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getHazmatInstruction() {
        return hazmatInstruction;
    }

    public void setHazmatInstruction(String hazmatInstruction) {
        this.hazmatInstruction = hazmatInstruction;
    }

    public String getEmergencyCompany() {
        return emergencyCompany;
    }

    public void setEmergencyCompany(String emergencyCompany) {
        this.emergencyCompany = emergencyCompany;
    }

    public String getEmergencyContract() {
        return emergencyContract;
    }

    public void setEmergencyContract(String emergencyContract) {
        this.emergencyContract = emergencyContract;
    }

    public PhoneEmbeddableObject getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(PhoneEmbeddableObject emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getUnNumber() {
        return unNumber;
    }

    public void setUnNumber(String unNumber) {
        this.unNumber = unNumber;
    }

    public String getPackingGroup() {
        return packingGroup;
    }

    public void setPackingGroup(String packingGroup) {
        this.packingGroup = packingGroup;
    }

}
