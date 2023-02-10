package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;

/**
 * LTL product for specified {@link com.pls.core.domain.organization.OrganizationEntity}.
 *
 * @author Maxim Medvedev
 */
@Entity
@Table(name = "LTL_PRODUCT")
public class LtlProductEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String Q_ARCHIVE_PRODUCT = "com.pls.shipment.domain.LtlProductEntity.Q_ARCHIVE_PRODUCT";
    public static final String Q_COUNT_PRODUCTS_BY_ID = "com.pls.shipment.domain.LtlProductEntity.Q_COUNT_PRODUCTS_BY_ID";
    public static final String Q_GET_PRODUCT_LIST = "com.pls.shipment.domain.LtlProductEntity.Q_GET_PRODUCT_LIST";
    public static final String Q_GET_RECENT_PRODUCTS = "com.pls.shipment.domain.LtlProductEntity.Q_GET_RECENT_PRODUCTS";
    public static final String Q_GET_PRODUCTS_BY_FILTER = "com.pls.shipment.domain.LtlProductEntity.Q_GET_PRODUCTS_BY_FILTER";
    public static final String Q_IS_PRODUCT_UNIQUE = "com.pls.shipment.domain.LtlProductEntity.Q_IS_PRODUCT_UNIQUE";
    public static final String Q_GET_PRODUCT_BY_CODE_AND_NAME = "com.pls.shipment.domain.LtlProductEntity.Q_GET_PRODUCT_BY_CODE_AND_NAME";
    public static final String Q_GET_PRODUCT_BY_INFO = "com.pls.shipment.domain.LtlProductEntity.Q_GET_PRODUCT_BY_INFO";
    public static final String Q_GET_PRODUCT_BY_CLASS_AND_SKU = "com.pls.shipment.domain.LtlProductEntity.Q_GET_PRODUCT_BY_CLASS_AND_SKU";

    public static final int DESCRIPTION_LENGTH = 1000;

    public static final int NMFC_NUM_LENGHT = 30;

    private static final int NMFC_SUB_NUM_LENGHT = 30;

    public static final int PRODUCT_CODE_LENGHT = 30;

    private static final long serialVersionUID = -8726996968459383777L;

    @Column(name = "COMMODITY_CLASS_CODE", nullable = true)
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass commodityClass;

    @Column(name = "ORG_ID", nullable = false)
    private Long customerId;

    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomerEntity customer;

    @Column(name = "DESCRIPTION", nullable = false, length = DESCRIPTION_LENGTH)
    private String description = StringUtils.EMPTY;

    @Column(name = "HAZMAT_FLAG", nullable = false)
    @Type(type = "yes_no")
    private boolean hazmat;

    @Embedded
    private LtlProductHazmatInfo hazmatInfo;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_PRODUCT_SEQ")
    @SequenceGenerator(name = "LTL_PRODUCT_SEQ", sequenceName = "LTL_PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRODUCT_ID")
    private Long id;

    @Column(name = "LOCATION_ID", nullable = false)
    private Long locationId = 1L;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "NMFC_NUM", nullable = true, length = NMFC_NUM_LENGHT)
    private String nmfcNum;

    @Column(name = "NMFC_SUB_NUM", nullable = true, length = NMFC_SUB_NUM_LENGHT)
    private String nmfcSubNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PACKAGE_TYPE")
    @NotFound(action = NotFoundAction.IGNORE)
    private PackageTypeEntity packageType;

    @Column(name = "PIECES", nullable = true)
    private Long pieces;

    @Column(name = "PRODUCT_CODE", nullable = true, length = PRODUCT_CODE_LENGHT)
    private String productCode;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "LTL_PRODUCT_TRACKING_ID", nullable = false)
    private Long trackingId = 1L;

    @Column(name = "PERSON_ID", nullable = true)
    private Long personId;

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

    @Column(name = "WEIGHT", nullable = true)
    private BigDecimal weight;

    @Transient
    private boolean shared;

    /**
     * These loadMaterials are added for informational purposes only!
     * We are using them to find recently used products.
     */
    @OneToMany(mappedBy = "referencedProduct", fetch = FetchType.LAZY)
    private Set<LoadMaterialEntity> loadMaterials;

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LtlProductEntity) {
            if (obj == this) {
                result = true;
            } else {
                LtlProductEntity rhs = (LtlProductEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();

                builder.append(getTrackingId(), rhs.getTrackingId()).append(getVersion(), rhs.getVersion());

                builder.append(getCommodityClass(), rhs.getCommodityClass())
                        .append(getDescription(), rhs.getDescription()).append(getHazmatInfo(), rhs.getHazmatInfo())
                        .append(getPieces(), rhs.getPieces()).append(getProductCode(), rhs.getProductCode())
                        .append(getPackageType(), rhs.getPackageType()).append(getNmfcNum(), rhs.getNmfcNum())
                        .append(getNmfcSubNum(), rhs.getNmfcSubNum()).append(getWeight(), rhs.getWeight());

                builder.append(getLocationId(), rhs.getLocationId()).append(getCustomerId(), rhs.getCustomerId());

                builder.append(getModification(), rhs.getModification());

                builder.append(isShared(), rhs.isShared());
                result = builder.isEquals();
            }
        }
        return result;
    }

    /**
     * Get commodityClass value.
     *
     * @return the commodityClass.
     */
    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    /**
     * Get customerId value.
     *
     * @return the customerId.
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * Get description value.
     *
     * @return Not <code>null</code> {@link String}.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get hazmatInfo value.
     *
     * @return Not <code>null</code> {@link LtlProductHazmatInfo} instance if it is hazmat product. Otherwise
     *         returns <code>null</code>.
     */
    public LtlProductHazmatInfo getHazmatInfo() {
        return hazmatInfo;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Get locationId value.
     *
     * @return the locationId.
     */
    public Long getLocationId() {
        return locationId;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    /**
     * Get nmfcNum value.
     *
     * @return the nmfcNum.
     */
    public String getNmfcNum() {
        return nmfcNum;
    }

    /**
     * Get nmfcSubNum value.
     *
     * @return the nmfcSubNum.
     */
    public String getNmfcSubNum() {
        return nmfcSubNum;
    }

    /**
     * Get packageType value.
     *
     * @return the packageType.
     */
    public PackageTypeEntity getPackageType() {
        return packageType;
    }

    /**
     * Get pieces value.
     *
     * @return the pieces.
     */
    public Long getPieces() {
        return pieces;
    }

    /**
     * Get productCode value.
     *
     * @return the productCode.
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * Get trackingId value.
     *
     * @return the trackingId.
     */
    public Long getTrackingId() {
        return trackingId;
    }

    /**
     * Get version value.
     *
     * @return the version.
     */
    public long getVersion() {
        return version;
    }

    /**
     * Get weight value.
     *
     * @return the weight.
     */
    public BigDecimal getWeight() {
        return weight;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getTrackingId()).append(getVersion());

        builder.append(getCommodityClass()).append(getDescription()).append(getHazmatInfo()).append(getPieces())
                .append(getProductCode()).append(getPackageType()).append(getNmfcNum()).append(getNmfcSubNum())
                .append(getWeight());

        builder.append(getLocationId()).append(getCustomerId());

        builder.append(getModification());
        builder.append(isShared());

        return builder.toHashCode();
    }

    /**
     * Is this product hazmat.
     *
     * @return <code>true</code> if this is hazmat product.
     */
    public boolean isHazmat() {
        return hazmatInfo != null && !hazmatInfo.equals(new LtlProductHazmatInfo());
    }

    /**
     * Set commodityClass value.
     *
     * @param commodityClass
     *            value to set.
     */
    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
    }

    /**
     * Set customerId value.
     *
     * @param customerId
     *            value to set.
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    /**
     * Set description value.
     *
     * @param description
     *            value to set.
     */
    public void setDescription(String description) {
        this.description = StringUtils.trimToEmpty(description);
    }

    /**
     * Set hazmatInfo value.
     *
     * @param hazmatInfo
     *            Not <code>null</code> {@link LtlProductHazmatInfo} instance if this is hazmat product. Itf
     *            it is regular product you should set <code>null</code> value here..
     */
    public void setHazmatInfo(LtlProductHazmatInfo hazmatInfo) {
        this.hazmatInfo = hazmatInfo;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set locationId value.
     *
     * @param locationId
     *            value to set.
     */
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    /**
     * Set nmfcNum value.
     *
     * @param nmfcNum
     *            value to set.
     */
    public void setNmfcNum(String nmfcNum) {
        this.nmfcNum = nmfcNum;
    }

    /**
     * Set nmfcSubNum value.
     *
     * @param nmfcSubNum
     *            value to set.
     */
    public void setNmfcSubNum(String nmfcSubNum) {
        this.nmfcSubNum = nmfcSubNum;
    }

    /**
     * Set packageType value.
     *
     * @param packageType
     *            value to set.
     */
    public void setPackageType(PackageTypeEntity packageType) {
        this.packageType = packageType;
    }

    /**
     * Set pieces value.
     *
     * @param pieces
     *            value to set.
     */
    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }

    /**
     * Set productCode value.
     *
     * @param productCode
     *            value to set.
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * Set trackingId value.
     *
     * @param trackingId
     *            value to set.
     */
    public void setTrackingId(Long trackingId) {
        this.trackingId = trackingId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /**
     * Set version value.
     *
     * @param version
     *            value to set.
     */
    public void setVersion(long version) {
        this.version = version;
    }

    /**
     * Set weight value.
     *
     * @param weight
     *            value to set.
     */
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<LoadMaterialEntity> getLoadMaterials() {
        return loadMaterials;
    }

    public void setLoadMaterials(Set<LoadMaterialEntity> loadMaterials) {
        this.loadMaterials = loadMaterials;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", getId()).append("trackingId", getTrackingId()).append("version", getVersion());

        builder.append("commodityClass", getCommodityClass()).append("description", getDescription())
                .append("hazmatInfo", getHazmatInfo()).append("pieces", getPieces())
                .append("productCode", getProductCode()).append("packageType", getPackageType())
                .append("nmfcNum", getNmfcNum() + "/" + getNmfcSubNum()).append("weight", getWeight());

        builder.append("locationId", getLocationId()).append("customerId", getCustomerId());

        builder.append("modification", getModification());

        return builder.toString();
    }

    /**
     * Correct initial state after entity loading.
     */
    @PostLoad
    protected void correctAfterLoading() {
        if (!hazmat) {
            hazmatInfo = null;
        }
    }

    /**
     * Correct initial state before data saving.
     */
    @PrePersist
    @PreUpdate
    protected void correctBeforeInsert() {
        hazmat = isHazmat();
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean isShared) {
        this.shared = isShared;
    }

}
