package com.pls.ltlrating.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 *
 * Entity object for the API Materials criteria.
 *
 * @author Pavani Challa
 *
 */
@Entity
@Table(name = "LTL_PRICING_API_MATERIALS_CRIT")
public class LtlPricingApiMaterialsCritEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 5122745714658625255L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pricing_api_mats_crit_seq")
    @SequenceGenerator(name = "ltl_pricing_api_mats_crit_seq", sequenceName = "ltl_pricing_api_mats_crit_seq", allocationSize = 1)
    @Column(name = "LTL_PRICING_API_MATERIAL_ID")
    private Long id;

    @Column(name = "LTL_PRICING_API_CRIT_ID", updatable = false, insertable = false)
    private Long pricingApiCritId;

    @Column(name = "COMMODITY_CLASS")
    private String commodityClass;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    @Column(name = "HEIGHT")
    private BigDecimal height;

    @Column(name = "WIDTH")
    private BigDecimal width;

    @Column(name = "LENGTH")
    private BigDecimal length;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "DIMENSION_UNIT")
    private String dimensionUnit;

    @Column(name = "PACKAGE_TYPE")
    private String packageType;

    @Column(name = "HAZMAT")
    private String hazmat;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPricingApiCritId() {
        return pricingApiCritId;
    }

    public void setPricingApiCritId(Long pricingApiCritId) {
        this.pricingApiCritId = pricingApiCritId;
    }

    public String getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(String commodityClass) {
        this.commodityClass = commodityClass;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDimensionUnit() {
        return dimensionUnit;
    }

    public void setDimensionUnit(String dimensionUnit) {
        this.dimensionUnit = dimensionUnit;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getHazmat() {
        return hazmat;
    }

    public void setHazmat(String hazmat) {
        this.hazmat = hazmat;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PlainModificationObject getModification() {
        return modification;
    }
}
