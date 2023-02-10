package com.pls.ltlrating.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.enums.DistanceUOM;
import com.pls.ltlrating.domain.enums.WeightUOM;

/**
 * Ltl Guaranteed Price.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_GUARANTEED_PRICE")
public class LtlGuaranteedPriceEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 6378078215613257341L;

    public static final String FIND_BY_PROFILE_ID = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.FIND_BY_PROFILE_ID";

    public static final String FIND_BY_STATUS_AND_PROFILE_ID = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.FIND_BY_STATUS_AND_PROFILE_ID";

    public static final String FIND_ACTIVE_AND_EFFECTIVE = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.FIND_ACTIVE_AND_EFFECTIVE";

    public static final String FIND_ACTIVE_AND_EFFECTIVE_FOR_PROFILE =
            "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.FIND_ACTIVE_AND_EFFECTIVE_FOR_PROFILE";

    public static final String FIND_EXPIRED = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.FIND_EXPIRED";

    public static final String UPDATE_STATUS = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.UPDATE_STATUS";

    public static final String EXPIRE_BY_IDS = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.EXPIRE_BY_IDS";

    public static final String INACTIVATE_BY_PROFILE_STATEMENT = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.INACTIVATE_BY_PROFILE_STATEMENT";

    public static final String FIND_CSP_ENTITY_BY_COPIED_FROM = "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.FIND_CSP_ENTITY_BY_COPIED_FROM";

    public static final String EXPIRATE_CSP_BY_COPIED_FROM_STATEMENT =
            "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.EXPIRATE_CSP_BY_COPIED_FROM_STATEMENT";

    public static final String UPDATE_CSP_STATUS_STATEMENT =
            "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.UPDATE_CSP_STATUS_BY_COPIED_FROM_STATEMENT";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID =
            "com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_GUARANTEED_PRICE_SEQUENCE")
    @SequenceGenerator(name = "LTL_GUARANTEED_PRICE_SEQUENCE", sequenceName = "LTL_GUARANTEED_PRICE_SEQ", allocationSize = 1)
    @Column(name = "LTL_GUARANTEED_PRICE_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long ltlPricProfDetailId;

    @Column(name = "APPLY_BEFORE_FUEL")
    @Type(type = "yes_no")
    private Boolean applyBeforeFuel;

    @Column(name = "BOL_CARRIER_NAME")
    private String bollCarrierName;

    @Column(name = "CHARGE_RULE_TYPE")
    @Enumerated(EnumType.STRING)
    private ChargeRuleTypeEnum chargeRuleType;

    @Column(name = "UNIT_COST")
    private BigDecimal unitCost;

    @Column(name = "MIN_COST")
    private BigDecimal minCost = BigDecimal.ZERO;

    @Column(name = "MAX_COST")
    private BigDecimal maxCost;

    @Column(name = "COST_APPL_MIN_WT", nullable = true)
    private Long costApplMinWt;

    @Column(name = "COST_APPL_MAX_WT", nullable = true)
    private Long costApplMaxWt;

    @Column(name = "COST_APPL_WT_UOM", nullable = true)
    @Enumerated(EnumType.STRING)
    private WeightUOM costApplWtUom = WeightUOM.LB;

    @Column(name = "COST_APPL_MIN_DIST", nullable = true)
    private Long costApplMinDist;

    @Column(name = "COST_APPL_MAX_DIST", nullable = true)
    private Long costApplMaxDist;

    @Column(name = "COST_APPL_DIST_UOM", nullable = true)
    @Enumerated(EnumType.STRING)
    private DistanceUOM costApplDistUom = DistanceUOM.ML;

    @Column(name = "UNIT_MARGIN")
    private BigDecimal unitMargin;

    @Column(name = "MIN_MARGIN")
    private BigDecimal minMargin;

    @Column(name = "TIME")
    private Long time;

    @Column(name = "EFF_DATE")
    private Date effDate;

    @Column(name = "EXP_DATE")
    private Date expDate;

    @Column(name = "MOVEMENT_TYPE")
    private String movementType = "BOTH";

    @Column(name = "SERVICE_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlServiceType serviceType = LtlServiceType.DIRECT;

    @Column(name = "EXT_NOTES")
    private String extNotes;

    @Column(name = "INT_NOTES")
    private String intNotes;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LTL_GUARANTEED_PRICE_ID", nullable = false)
    private List<LtlGuaranteedBlockDestEntity> guaranteedBlockDestinations = new ArrayList<LtlGuaranteedBlockDestEntity>();

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    @Version
    private Long version = 1L;

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

    public Boolean getApplyBeforeFuel() {
        return applyBeforeFuel;
    }

    public void setApplyBeforeFuel(Boolean applyBeforeFuel) {
        this.applyBeforeFuel = applyBeforeFuel;
    }

    public String getBollCarrierName() {
        return bollCarrierName;
    }

    public void setBollCarrierName(String bollCarrierName) {
        this.bollCarrierName = bollCarrierName;
    }

    public ChargeRuleTypeEnum getChargeRuleType() {
        return chargeRuleType;
    }

    public void setChargeRuleType(ChargeRuleTypeEnum chargeRuleType) {
        this.chargeRuleType = chargeRuleType;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }

    public BigDecimal getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(BigDecimal maxCost) {
        this.maxCost = maxCost;
    }

    public Long getCostApplMinWt() {
        return costApplMinWt;
    }

    public void setCostApplMinWt(Long pCostApplMinWt) {
        this.costApplMinWt = pCostApplMinWt;
    }

    public Long getCostApplMaxWt() {
        return costApplMaxWt;
    }

    public void setCostApplMaxWt(Long pCostApplMaxWt) {
        this.costApplMaxWt = pCostApplMaxWt;
    }

    public WeightUOM getCostApplWtUom() {
        return costApplWtUom;
    }

    public void setCostApplWtUom(WeightUOM pCostApplWtUom) {
        this.costApplWtUom = pCostApplWtUom;
    }

    public Long getCostApplMinDist() {
        return costApplMinDist;
    }

    public void setCostApplMinDist(Long pCostApplMinDist) {
        this.costApplMinDist = pCostApplMinDist;
    }

    public Long getCostApplMaxDist() {
        return costApplMaxDist;
    }

    public void setCostApplMaxDist(Long pCostApplMaxDist) {
        this.costApplMaxDist = pCostApplMaxDist;
    }

    public DistanceUOM getCostApplDistUom() {
        return costApplDistUom;
    }

    public void setCostApplDistUom(DistanceUOM pCostApplDistUom) {
        this.costApplDistUom = pCostApplDistUom;
    }

    public BigDecimal getUnitMargin() {
        return unitMargin;
    }

    public void setUnitMargin(BigDecimal unitMargin) {
        this.unitMargin = unitMargin;
    }

    public BigDecimal getMinMargin() {
        return minMargin;
    }

    public void setMinMargin(BigDecimal minMargin) {
        this.minMargin = minMargin;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String pMovementType) {
        this.movementType = pMovementType;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
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

    public List<LtlGuaranteedBlockDestEntity> getGuaranteedBlockDestinations() {
        return guaranteedBlockDestinations;
    }

    public Long getCopiedFrom() {
        return copiedFrom;
    }

    public void setCopiedFrom(Long copiedFrom) {
        this.copiedFrom = copiedFrom;
    }

    public String getExtNotes() {
        return extNotes;
    }

    public void setExtNotes(String extNotes) {
        this.extNotes = extNotes;
    }

    public String getIntNotes() {
        return intNotes;
    }

    public void setIntNotes(String intNotes) {
        this.intNotes = intNotes;
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

    /**
     * Enumeration for chargeRuleType property.
     * */
    public enum ChargeRuleTypeEnum {
        FL, FT, PC, PT;
    }

    /**
     * Default constructor.
     */
    public LtlGuaranteedPriceEntity() {
    }

    /**
     * Copy constructor that returns a copy of given clone.
     *
     * @param clone
     *            entity to copy.
     */
    public LtlGuaranteedPriceEntity(LtlGuaranteedPriceEntity clone) {
        checkNotNull(clone, "LtlGuaranteedPriceEntity object is required");

        this.applyBeforeFuel = clone.getApplyBeforeFuel();
        this.bollCarrierName = clone.getBollCarrierName();
        this.chargeRuleType = clone.getChargeRuleType();
        this.effDate = clone.getEffDate();
        this.expDate = clone.getExpDate();
        this.costApplMinWt = clone.getCostApplMinWt();
        this.costApplMaxWt = clone.getCostApplMaxWt();
        this.costApplWtUom = clone.getCostApplWtUom();
        this.costApplMinDist = clone.getCostApplMinDist();
        this.costApplMaxDist = clone.getCostApplMaxDist();
        this.costApplDistUom = clone.getCostApplDistUom();
        this.minCost = clone.getMinCost();
        this.maxCost = clone.getMaxCost();
        this.minMargin = clone.getMinMargin();
        this.status = Status.ACTIVE;
        this.time = clone.getTime();
        this.unitCost = clone.getUnitCost();
        this.unitMargin = clone.getUnitMargin();
        this.movementType = clone.getMovementType();
        this.serviceType = clone.getServiceType();
        this.copiedFrom = clone.getId();
        this.extNotes = clone.getExtNotes();
        this.intNotes = clone.getIntNotes();

        List<LtlGuaranteedBlockDestEntity> cloneBlockEntity = new ArrayList<LtlGuaranteedBlockDestEntity>(clone.getGuaranteedBlockDestinations()
                .size());

        for (LtlGuaranteedBlockDestEntity item : clone.getGuaranteedBlockDestinations()) {
            item.setGuaranteedPriceId(clone.getId());
            cloneBlockEntity.add(new LtlGuaranteedBlockDestEntity(item));
        }

        this.guaranteedBlockDestinations = cloneBlockEntity;
    }
}
