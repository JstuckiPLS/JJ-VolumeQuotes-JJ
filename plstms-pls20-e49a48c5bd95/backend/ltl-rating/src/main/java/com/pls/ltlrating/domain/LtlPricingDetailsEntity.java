package com.pls.ltlrating.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.enums.DistanceUOM;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.LtlMarginType;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.WeightUOM;

/**
 * Ltl Pricing Details.
 *
 * @author Artem Arapov
 */
@Entity
@Table(name = "LTL_PRICING_DETAILS")
public class LtlPricingDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 4986724084485131078L;

    public static final String UPDATE_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlPricingDetailsEntity.UPDATE_STATUS_STATEMENT";

    public static final String INACTIVATE_BY_PROFILE_STATEMENT =
            "com.pls.ltlrating.domain.LtlPricingDetailsEntity.INACTIVATE_BY_PROFILE_STATEMENT";

    public static final String EXPIRATE_STATEMENT = "com.pls.ltlrating.domain.LtlPricingDetailsEntity.EXPIRATE_STATEMENT";

    public static final String FIND_BY_STATUS = "com.pls.ltlrating.domain.LtlPricingDetailsEntity.FIND_BY_STATUS";

    public static final String FIND_ACTIVE_FOR_PROFILE_QUERY = "com.pls.ltlrating.domain.LtlPricingDetailsEntity.FIND_ACTIVE_FOR_PROFILE_QUERY";

    public static final String FIND_PROFILE_PRICING_TYPE_QUERY = "com.pls.ltlrating.domain.LtlPricingDetailsEntity.FIND_PROFILE_PRICING_TYPE_QUERY";

    public static final String FIND_BY_COPIED_FROM_QUERY =
            "com.pls.ltlrating.domain.LtlPricingDetailsEntity.FIND_BY_COPIED_FROM_QUERY";

    public static final String EXPIRATE_CPS_BY_COPIED_FROM_STATEMENT =
            "com.pls.ltlrating.domain.LtlPricingDetailsEntity.EXPIRATE_CPS_BY_COPIED_FROM_STATEMENT";

    public static final String UPDATE_CSP_STATUS_STATEMENT =
            "com.pls.ltlrating.domain.LtlPricingDetailsEntity.UPDATE_CSP_STATUS_BY_COPIED_FROM_STATEMENT";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_PRICING_DETAILS_SEQUENCE")
    @SequenceGenerator(name = "LTL_PRICING_DETAILS_SEQUENCE", sequenceName = "LTL_PRICING_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRICING_DETAIL_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long ltlPricProfDetailId;

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
    private WeightUOM costApplWtUom = WeightUOM.LB;

    @Column(name = "COST_APPL_MIN_DIST")
    private BigDecimal costApplMinDist;

    @Column(name = "COST_APPL_MAX_DIST")
    private BigDecimal costApplMaxDist;

    @Column(name = "COST_APPL_DIST_UOM")
    @Enumerated(EnumType.STRING)
    private DistanceUOM costApplDistUom = DistanceUOM.ML;

    @Column(name = "MIN_COST")
    private BigDecimal minCost = BigDecimal.ZERO;

    @Column(name = "MARGIN_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlMarginType marginType;

    @Column(name = "UNIT_MARGIN", nullable = true)
    private BigDecimal marginAmount;

    @Column(name = "MARGIN_DOLLAR_AMT", nullable = true)
    private BigDecimal minMarginAmount;

    @Column(name = "EFF_DATE")
    private Date effDate;

    @Column(name = "EXP_DATE")
    private Date expDate;

    @Column(name = "SERVICE_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlServiceType serviceType = LtlServiceType.DIRECT;

    @Column(name = "SMC3_TARIFF")
    private String smcTariff;

    @Column(name = "MOVEMENT_TYPE")
    @Enumerated(EnumType.STRING)
    private MoveType movementType = MoveType.BOTH;

    @Column(name = "COMMODITY_CLASS")
    @Enumerated(EnumType.STRING)
    private CommodityClass commodityClass;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version = 1L;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pricingDetail")
    private Set<LtlPricingGeoServicesEntity> geoServices = new HashSet<LtlPricingGeoServicesEntity>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pricingDetail")
    private Set<LtlFakMapEntity> fakMapping = new HashSet<LtlFakMapEntity>();

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    /**
     * This entity is used for Blanket/CSP profile in order to not duplicate all information.
     */
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private LtlPricingDetailsEntity parentPricingDetails;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLtlPricProfDetailId() {
        return ltlPricProfDetailId;
    }

    public void setLtlPricProfDetailId(Long ltlPricProfDetailId) {
        this.ltlPricProfDetailId = ltlPricProfDetailId;
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

    public BigDecimal getCostApplMinDist() {
        return costApplMinDist;
    }

    public void setCostApplMinDist(BigDecimal costApplMinDist) {
        this.costApplMinDist = costApplMinDist;
    }

    public BigDecimal getCostApplMaxDist() {
        return costApplMaxDist;
    }

    public void setCostApplMaxDist(BigDecimal costApplMaxDist) {
        this.costApplMaxDist = costApplMaxDist;
    }

    public DistanceUOM getCostApplDistUom() {
        return costApplDistUom;
    }

    public void setCostApplDistUom(DistanceUOM costApplDistUom) {
        this.costApplDistUom = costApplDistUom;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }

    public LtlMarginType getMarginType() {
        return marginType;
    }

    public void setMarginType(LtlMarginType marginType) {
        this.marginType = marginType;
    }

    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    public BigDecimal getMinMarginAmount() {
        return minMarginAmount;
    }

    public void setMinMarginAmount(BigDecimal minMarginAmount) {
        this.minMarginAmount = minMarginAmount;
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

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getSmcTariff() {
        return smcTariff;
    }

    public void setSmcTariff(String smcTariff) {
        this.smcTariff = smcTariff;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<LtlPricingGeoServicesEntity> getGeoServices() {
        return geoServices;
    }

    public void setGeoServices(Set<LtlPricingGeoServicesEntity> geoServices) {
        this.geoServices = geoServices;
    }

    public Set<LtlFakMapEntity> getFakMapping() {
        return fakMapping;
    }

    public void setFakMapping(Set<LtlFakMapEntity> fakMapping) {
        this.fakMapping = fakMapping;
    }

    public Long getCopiedFrom() {
        return copiedFrom;
    }

    public void setCopiedFrom(Long copiedFrom) {
        this.copiedFrom = copiedFrom;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public LtlPricingDetailsEntity getParentPricingDetails() {
        return parentPricingDetails;
    }

    public void setParentPricingDetails(LtlPricingDetailsEntity parentPricingDetails) {
        this.parentPricingDetails = parentPricingDetails;
    }
}
