package com.pls.shipment.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.shared.Status;

/**
 * Entity for Ltl pricing proposal materials.
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LTL_PRIC_PROP_MATERIALS")
public class LtlPricPropMaterialsEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -8126474392692594313L;

    public static final String U_INACTIVATE_FOR_LOAD = "com.pls.shipment.domain.LtlPricPropMaterialsEntity.U_INACTIVATE_FOR_LOAD";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pric_prop_mat_seq")
    @SequenceGenerator(name = "ltl_pric_prop_mat_seq", sequenceName = "LTL_PRIC_PROP_MAT_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRIC_PROP_MAT_ID")
    private Long ltlPricPropMatId;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "QUOTE_ID")
    private Long savedQuote;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    @Column(name = "HEIGHT")
    private BigDecimal height;

    @Column(name = "WIDTH")
    private BigDecimal width;

    @Column(name = "LENGTH")
    private BigDecimal length;

    @Column(name = "COMMODITY_CLASS_CODE")
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass commodityClass;

    @Column(name = "PIECES")
    private Integer pieces;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "HAZMAT")
    @Type(type = "yes_no")
    private Boolean hazmat;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "LTL_PACKAGE_TYPE")
    private String packageType;

    @Column(name = "PART_DESCRIPTION")
    private String productDescription;

    @Column(name = "LTL_PRODUCT_ID")
    private Long referencedProductId;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

    @Override
    public Long getId() {
        return ltlPricPropMatId;
    }

    @Override
    public void setId(Long ltlPricPropMatId) {
        this.ltlPricPropMatId = ltlPricPropMatId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getSavedQuote() {
        return savedQuote;
    }

    public void setSavedQuote(Long savedQuote) {
        this.savedQuote = savedQuote;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
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

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean isHazmat() {
        return hazmat;
    }

    public void setHazmat(Boolean hazmat) {
        this.hazmat = hazmat;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Long getReferencedProductId() {
        return referencedProductId;
    }

    public void setReferencedProductId(Long referencedProductId) {
        this.referencedProductId = referencedProductId;
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
