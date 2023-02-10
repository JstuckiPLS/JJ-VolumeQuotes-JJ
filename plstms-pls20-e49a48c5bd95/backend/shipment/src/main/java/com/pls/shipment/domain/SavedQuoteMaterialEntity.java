package com.pls.shipment.domain;

import java.math.BigDecimal;

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

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CommodityClass;

/**
 * Saved Quote Material entity.
 *
 * @author Mikhail Boldinov, 18/03/13
 */
@Entity
@Table(name = "SAVED_QUOTE_MATERIALS")
public class SavedQuoteMaterialEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1855105234436408573L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saved_quote_materials_sequence")
    @SequenceGenerator(name = "saved_quote_materials_sequence", sequenceName = "SAVED_QUOTE_MATERIALS_SEQ", allocationSize = 1)
    @Column(name = "QUOTE_MATERIAL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUOTE_ID", nullable = false)
    private SavedQuoteEntity quote;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    @Column(name = "LENGTH")
    private BigDecimal length;

    @Column(name = "WIDTH")
    private BigDecimal width;

    @Column(name = "HEIGHT")
    private BigDecimal height;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_DESCRIPTION")
    private String productDescription;

    @Column(name = "COMMODITY_CLASS_CODE")
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass commodityClass;

    @Column(name = "NMFC")
    private String nmfc;

    @Column(name = "HAZMAT", nullable = false)
    @Type(type = "yes_no")
    private Boolean hazmat;

    @Embedded
    private LtlProductHazmatInfo hazmatInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PACKAGE_TYPE")
    private PackageTypeEntity packageType;

    @Column(name = "PIECES")
    private Long pieces;

    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "STACKABLE", nullable = false)
    @Type(type = "yes_no")
    private Boolean stackable;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public SavedQuoteEntity getQuote() {
        return quote;
    }

    public void setQuote(SavedQuoteEntity quote) {
        this.quote = quote;
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

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
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

    public LtlProductHazmatInfo getHazmatInfo() {
        return hazmatInfo;
    }

    public void setHazmatInfo(LtlProductHazmatInfo hazmatInfo) {
        this.hazmatInfo = hazmatInfo;
    }

    public PackageTypeEntity getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageTypeEntity packageType) {
        this.packageType = packageType;
    }

    public Long getPieces() {
        return pieces;
    }

    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Boolean getStackable() {
        return stackable;
    }

    public void setStackable(Boolean stackable) {
        this.stackable = stackable;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
