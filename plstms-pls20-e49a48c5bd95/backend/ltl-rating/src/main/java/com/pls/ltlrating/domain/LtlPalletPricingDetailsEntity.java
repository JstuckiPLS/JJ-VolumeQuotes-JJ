package com.pls.ltlrating.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.WeightUOM;

/**
 * Ltl Pallet Pricing Details Entity.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(name = "LTL_PALLET_PRIC_DETAILS")
public class LtlPalletPricingDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 8018849500636461561L;

    public static final String UPDATE_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity.UPDATE_STATUS_STATEMENT";

    public static final String INACTIVATE_BY_PROFILE_STATEMENT =
            "com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity.INACTIVATE_BY_PROFILE_STATEMENT";

    public static final String FIND_CSP_ENTITY_BY_COPIED_FROM =
            "com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity.FIND_CSP_ENTITY_BY_COPIED_FROM";

    public static final String UPDATE_CSP_STATUS_STATEMENT =
            "com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity.UPDATE_CSP_STATUS_BY_COPIED_FROM_STATEMENT";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_PALLET_PRICING_DETAILS_SEQUENCE")
    @SequenceGenerator(name = "LTL_PALLET_PRICING_DETAILS_SEQUENCE", sequenceName = "LTL_PALLET_PRICE_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "LTL_PALLET_PRIC_DET_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long profileDetailId;

    @Column(name = "MIN_QTY")
    private Long minQuantity;

    @Column(name = "MAX_QTY")
    private Long maxQuantity;

    @Column(name = "COST_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlCostType costType;

    @Column(name = "UNIT_COST")
    private BigDecimal unitCost;

    @Column(name = "COST_APPL_MIN_WT")
    private BigDecimal costApplMinWt;

    @Column(name = "COST_APPL_MAX_WT")
    private BigDecimal costApplMaxWt;

    @Column(name = "COST_APPL_WT_UOM")
    @Enumerated(EnumType.STRING)
    private WeightUOM costApplWtUom;

    @Column(name = "MARGIN_PERCENT", nullable = true)
    private BigDecimal marginPercent;

    @Column(name = "EFF_DATE")
    private Date effDate;

    @Column(name = "EXP_DATE")
    private Date expDate;

    @Column(name = "TRANSIT_TIME")
    private Long transitTime;

    @Column(name = "SERVICE_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlServiceType serviceType;

    @Column(name = "MOVEMENT_TYPE")
    @Enumerated(EnumType.STRING)
    private MoveType movementType = MoveType.BOTH;

    @Column(name = "ZONE_TO")
    private Long zoneTo;

    @Column(name = "ZONE_FROM")
    private Long zoneFrom;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Column(name = "EXCLUDE_FUEL")
    @Type(type = "yes_no")
    private Boolean isExcludeFuel;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version = 1L;

    /**
     * Default constructor.
     */
    public LtlPalletPricingDetailsEntity() {

    }

    /**
     * Copying constructor.
     *
     * @param clone
     *            Not <code>null</code> instance of {@link LtlPalletPricingDetailsEntity} which should be
     *            cloned.
     */
    public LtlPalletPricingDetailsEntity(LtlPalletPricingDetailsEntity clone) {
        if (clone == null) {
            throw new IllegalArgumentException("Argument can't be null");
        }

        this.costApplMaxWt = clone.getCostApplMaxWt();
        this.costApplMinWt = clone.getCostApplMinWt();
        this.costApplWtUom = clone.getCostApplWtUom();
        this.costType = clone.getCostType();
        this.effDate = clone.getEffDate();
        this.expDate = clone.getExpDate();
        this.marginPercent = clone.getMarginPercent();
        this.maxQuantity = clone.getMaxQuantity();
        this.minQuantity = clone.getMinQuantity();
        this.serviceType = clone.getServiceType();
        this.movementType = clone.getMovementType();
        this.status = clone.getStatus();
        this.transitTime = clone.getTransitTime();
        this.unitCost = clone.getUnitCost();
        this.zoneFrom = clone.getZoneFrom();
        this.zoneTo = clone.getZoneTo();
        this.copiedFrom = clone.getId();
        this.isExcludeFuel = clone.getIsExcludeFuel();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileDetailId() {
        return profileDetailId;
    }

    public void setProfileDetailId(Long profileDetailId) {
        this.profileDetailId = profileDetailId;
    }

    public LtlCostType getCostType() {
        return costType;
    }

    public void setCostType(LtlCostType costType) {
        this.costType = costType;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public Long getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Long minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public BigDecimal getCostApplMinWt() {
        return costApplMinWt;
    }

    public void setCostApplMinWt(BigDecimal costApplMinWt) {
        this.costApplMinWt = costApplMinWt;
    }

    public BigDecimal getCostApplMaxWt() {
        return costApplMaxWt;
    }

    public void setCostApplMaxWt(BigDecimal costApplMaxWt) {
        this.costApplMaxWt = costApplMaxWt;
    }

    public WeightUOM getCostApplWtUom() {
        return costApplWtUom;
    }

    public void setCostApplWtUom(WeightUOM costApplWtUom) {
        this.costApplWtUom = costApplWtUom;
    }

    public BigDecimal getMarginPercent() {
        return marginPercent;
    }

    public void setMarginPercent(BigDecimal marginPercent) {
        this.marginPercent = marginPercent;
    }

    public Long getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(Long transitTime) {
        this.transitTime = transitTime;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Long getZoneTo() {
        return zoneTo;
    }

    public void setZoneTo(Long zoneTo) {
        this.zoneTo = zoneTo;
    }

    public Long getZoneFrom() {
        return zoneFrom;
    }

    public void setZoneFrom(Long zoneFrom) {
        this.zoneFrom = zoneFrom;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCopiedFrom() {
        return copiedFrom;
    }

    public void setCopiedFrom(Long copiedFrom) {
        this.copiedFrom = copiedFrom;
    }

    public Boolean getIsExcludeFuel() {
        return isExcludeFuel;
    }

    public void setIsExcludeFuel(Boolean isExcludeFuel) {
        this.isExcludeFuel = isExcludeFuel;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof LtlPalletPricingDetailsEntity) {
            if (object == this) {
                return true;
            } else {
                LtlPalletPricingDetailsEntity rhs = (LtlPalletPricingDetailsEntity) object;

                EqualsBuilder builder = new EqualsBuilder();
                builder.append(this.getCostApplMaxWt(), rhs.getCostApplMaxWt())
                        .append(this.getCostApplMinWt(), rhs.getCostApplMinWt())
                        .append(this.getCostApplWtUom(), rhs.getCostApplWtUom())
                        .append(this.getCostType(), rhs.getCostType()).append(this.getEffDate(), rhs.getEffDate())
                        .append(this.getExpDate(), rhs.getExpDate())
                        .append(this.getMarginPercent(), rhs.getMarginPercent())
                        .append(this.getMaxQuantity(), rhs.getMaxQuantity())
                        .append(this.getMinQuantity(), rhs.getMinQuantity())
                        .append(this.getProfileDetailId(), rhs.getProfileDetailId())
                        .append(this.getServiceType(), rhs.getServiceType()).append(this.getStatus(), rhs.getStatus())
                        .append(this.getTransitTime(), rhs.getTransitTime())
                        .append(this.getUnitCost(), rhs.getUnitCost()).append(this.getZoneFrom(), rhs.getZoneFrom())
                        .append(this.getZoneTo(), rhs.getZoneTo())
                        .append(this.getMovementType(), rhs.getMovementType())
                        .append(this.getIsExcludeFuel(), rhs.getIsExcludeFuel());

                return builder.isEquals();
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(this.getCostApplMaxWt()).append(this.getCostApplMinWt()).append(this.getCostApplWtUom())
                .append(this.getCostType()).append(this.getExpDate()).append(this.getEffDate())
                .append(this.getMarginPercent()).append(this.getMaxQuantity()).append(this.getMinQuantity())
                .append(this.getMaxQuantity()).append(this.getMinQuantity()).append(this.getProfileDetailId())
                .append(this.getServiceType()).append(this.getStatus()).append(this.getTransitTime())
                .append(this.getUnitCost()).append(this.getZoneFrom()).append(this.getZoneTo())
                .append(this.getIsExcludeFuel());

        return builder.toHashCode();
    }
}
