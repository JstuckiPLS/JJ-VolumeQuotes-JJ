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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.enums.CommodityClass;

/**
 *  LoadMaterial Entity.
 *
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "LOAD_MATERIALS")
public class LoadMaterialEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -1304045319666718948L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loads_materials_sequence")
    @SequenceGenerator(name = "loads_materials_sequence", sequenceName = "LOAD_MATERIALS_SEQ", allocationSize = 1)
    @Column(name = "LOAD_MATERIAL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_DETAIL_ID")
    private LoadDetailsEntity loadDetail;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "HEIGHT", nullable = true, columnDefinition = "NUMBER(10,2)")
    private BigDecimal height;

    @Column(name = "WIDTH", nullable = true, columnDefinition = "NUMBER(10,2)")
    private BigDecimal width;

    @Column(name = "LENGTH", nullable = true, columnDefinition = "NUMBER(15,5)")
    private BigDecimal length;

    @Column(name = "WEIGHT", nullable = false, columnDefinition = "NUMBER(13,3)")
    private BigDecimal weight;

    @Column(name = "PIECES", nullable = true, columnDefinition = "NUMBER(6,0)")
    private Integer pieces;

    @Column(name = "COMMODITY_CLASS_CODE", nullable = true)
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass commodityClass;

    @Column(name = "HAZMAT", nullable = false)
    @Type(type = "yes_no")
    private boolean hazmat;

    @Column(name = "HAZMAT_CLASS")
    private String hazmatClass;

    @Column(name = "HAZMAT_INSTRUCTIONS")
    private String hazmatInstruction;

    @Column(name = "EMERGENCY_COMPANY", nullable = true)
    private String emergencyCompany;

    @Column(name = "EMERGENCY_CONTRACT", nullable = true)
    private String emergencyContract;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "countryCode",
                    column = @Column(name = "EMERGENCY_COUNTRY_CODE", nullable = true)),
            @AttributeOverride(name = "areaCode",
                    column = @Column(name = "EMERGENCY_AREA_CODE", nullable = true)),
            @AttributeOverride(name = "extension",
                    column = @Column(name = "EMERGENCY_EXTENSION", nullable = true)),
            @AttributeOverride(name = "number",
                    column = @Column(name = "EMERGENCY_NUMBER", nullable = true))
    })
    private PhoneEmbeddableObject emergencyPhone = new PhoneEmbeddableObject();

    @Column(name = "NMFC", nullable = true)
    private String nmfc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LTL_PACKAGE_TYPE", referencedColumnName = "PACKAGE_TYPE")
    @NotFound(action = NotFoundAction.IGNORE)
    private PackageTypeEntity packageType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PICKUP_DATE")
    private Date pickupDate;

    @Column(name = "QUANTITY", nullable = true)
    private String quantity;

    @Column(name = "PRODUCT_ID")
    private String productCode;

    @Column(name = "PART_DESCRIPTION")
    private String productDescription;

    @Column(name = "UN_NUM")
    private String unNumber;

    @Column(name = "PACKING_GROUP")
    private String packingGroup;

    @Column(name = "STACKABLE", nullable = false)
    @Type(type = "yes_no")
    private boolean stackable;

    @Column(name = "LTL_PRODUCT_ID", nullable = true)
    private Long referencedProductId;

    /**
     * This product is added for informational purposes only! We are using it to find recently used products.
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "LTL_PRODUCT_ID", nullable = true, insertable = false, updatable = false)
    private LtlProductEntity referencedProduct;
    
    @Column(name = "HARMONIZE_CODE", nullable = true)
    private String harmonizeCode;

    @Column(name = "COUNTRY_OF_ORIGIN", nullable = true)
    private String countryOfOrigin;
    
    @Column(name = "VALUE", nullable = true)
    private BigDecimal value;

    @Column(name = "CONDITION", nullable = true)
    private String condition;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoadDetailsEntity getLoadDetail() {
        return loadDetail;
    }

    public void setLoadDetail(LoadDetailsEntity loadDetail) {
        this.loadDetail = loadDetail;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
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

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
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

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    public PackageTypeEntity getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageTypeEntity packageType) {
        this.packageType = packageType;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
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

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public Long getReferencedProductId() {
        return referencedProductId;
    }

    public void setReferencedProductId(Long referencedProductId) {
        this.referencedProductId = referencedProductId;
    }

    public LtlProductEntity getReferencedProduct() {
        return referencedProduct;
    }

    public void setReferencedProduct(LtlProductEntity referencedProduct) {
        this.referencedProduct = referencedProduct;
    }

	public String getHarmonizeCode() {
		return harmonizeCode;
	}

	public void setHarmonizeCode(String harmonizeCode) {
		this.harmonizeCode = harmonizeCode;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
