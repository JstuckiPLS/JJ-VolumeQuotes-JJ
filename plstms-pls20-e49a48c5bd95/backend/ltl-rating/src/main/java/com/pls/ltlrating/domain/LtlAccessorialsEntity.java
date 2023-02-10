package com.pls.ltlrating.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.enums.DistanceUOM;
import com.pls.ltlrating.domain.enums.LengthUOM;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.WeightUOM;

/**
 * LTL Accessorials (LTL_ACCESSORIALS) entity. This table is used to set up the rates for LTL accessorials and
 * based on the accessorials set up here and the accessorials required for load, respective carrier is picked
 * to move the load.
 *
 * @author Hima Bindu Challa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "LTL_ACCESSORIALS")
public class LtlAccessorialsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 3214244568459383777L;

    public static final String Q_ALL_ACTIVE_BY_PROFILE_ID =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.Q_ALL_ACTIVE_BY_PROFILE_ID";

    public static final String Q_ALL_ACTIVE_AND_EFFECTIVE_BY_PROFILE_ID =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.Q_ALL_ACTIVE_AND_EFFECTIVE_BY_PROFILE_ID";

    public static final String Q_ALL_ACTIVE_AND_EFFECTIVE_FOR_PROFILE =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.Q_ALL_ACTIVE_AND_EFFECTIVE_FOR_PROFILE";

    public static final String Q_FIND_BY_STATUS_AND_PROFILE_ID =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.Q_FIND_BY_STATUS_AND_PROFILE_ID";

    public static final String INACTIVATE_ACTIVE_EFF_BY_PROFILE_ID =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.INACTIVATE_ACTIVE_EFF_BY_PROFILE_ID";

    public static final String FIND_CSP_ACC_BY_COPIED_FROM = "com.pls.ltlrating.domain.LtlAccessorialsEntity.FIND_CSP_ACC_BY_COPIED_FROM";

    public static final String EXPIRE_BY_IDS = "com.pls.ltlrating.domain.LtlAccessorialsEntity.EXPIRE_BY_IDS";

    public static final String EXPIRATE_CPS_BY_COPIED_FROM_STATEMENT =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.EXPIRATE_CPS_BY_COPIED_FROM_STATEMENT";

    public static final String UPDATE_CSP_STATUS_STATEMENT =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.UPDATE_CSP_STATUS_STATEMENT";

    public static final String UPDATE_STATUSES_STATEMENT =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.UPDATE_STATUSES_STATEMENT";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID =
            "com.pls.ltlrating.domain.LtlAccessorialsEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_ACCESSORIALS_SEQUENCE")
    @SequenceGenerator(name = "LTL_ACCESSORIALS_SEQUENCE", sequenceName = "LTL_ACCESSORIALS_SEQ", allocationSize = 1)
    @Column(name = "LTL_ACCESSORIAL_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID", nullable = false)
    private Long ltlPricProfDetailId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ACCESSORIAL_TYPE", insertable = false, updatable = false)
    private AccessorialTypeEntity accessorialTypeEntity;

    @Column(name = "ACCESSORIAL_TYPE", nullable = false)
    private String accessorialType;

    @Column(name = "BLOCKED", nullable = false)
    private String blocked = "N";

    @Column(name = "COST_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlCostType costType;

    @Column(name = "UNIT_COST", nullable = true)
    private BigDecimal unitCost;

    @Column(name = "COST_APPL_MAX_WT", nullable = true)
    private Long costApplMaxWt;

    @Column(name = "COST_APPL_MIN_WT", nullable = true)
    private Long costApplMinWt;

    @Column(name = "COST_APPL_MIN_DIST", nullable = true)
    private Long costApplMinDist;

    @Column(name = "COST_APPL_WT_UOM", nullable = true)
    @Enumerated(EnumType.STRING)
    private WeightUOM costApplWtUom = WeightUOM.LB;

    @Column(name = "COST_APPL_DIST_UOM", nullable = true)
    @Enumerated(EnumType.STRING)
    private DistanceUOM costApplDistUom = DistanceUOM.ML;

    @Column(name = "COST_APPL_MAX_DIST", nullable = true)
    private Long costApplMaxDist;

    @Column(name = "MARGIN_TYPE", nullable = true)
    private String marginType;

    @Column(name = "UNIT_MARGIN", nullable = true)
    private BigDecimal unitMargin;

    @Column(name = "MARGIN_PERCENT", nullable = true)
    private BigDecimal marginPercent;

    @Column(name = "MARGIN_DOLLAR_AMT", nullable = true)
    private BigDecimal marginDollarAmt;

    @Column(name = "EFF_DATE")
    private Date effDate;

    @Column(name = "EXP_DATE")
    private Date expDate;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "MOVEMENT_TYPE")
    @Enumerated(EnumType.STRING)
    private MoveType movementType = MoveType.BOTH;

    @Column(name = "SERVICE_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlServiceType serviceType = LtlServiceType.DIRECT;

    @Column(name = "EXT_NOTES")
    private String extNotes;

    @Column(name = "INT_NOTES")
    private String intNotes;

    @Column(name = "MIN_COST", nullable = true)
    private BigDecimal minCost;

    @Column(name = "MAX_COST", nullable = true)
    private BigDecimal maxCost;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    private PlainModificationObject modification = new PlainModificationObject();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "LTL_ACCESSORIAL_ID", nullable = false)
    private List<LtlAccGeoServicesEntity> ltlAccGeoServicesEntities = new ArrayList<LtlAccGeoServicesEntity>();

    @Column(name = "COST_APPL_MIN_LENGTH", nullable = true)
    private BigDecimal costApplMinLength;

    @Column(name = "COST_APPL_MAX_LENGTH", nullable = true)
    private BigDecimal costApplMaxLength;

    @Column(name = "COST_APPL_LENGTH_UOM", nullable = true)
    @Enumerated(EnumType.STRING)
    private LengthUOM costApplLengthUom = LengthUOM.IN;

    @Column(name = "APPLY_BEFORE_FUEL")
    @Type(type = "yes_no")
    private Boolean applyBeforeFuel;

    /**
     * Default constructor.
     * */
    public LtlAccessorialsEntity() {
    }

    /**
     * Copying constructor.
     *
     * @param source -
     *                  {@link LtlAccessorialsEntity} source entity to be copied.
     * */
    public LtlAccessorialsEntity(LtlAccessorialsEntity source) {
        this.accessorialType = source.getAccessorialType();
        this.blocked = source.getBlocked();
        this.costType = source.getCostType();
        this.unitCost = source.getUnitCost();
        this.costApplMinWt = source.getCostApplMinWt();
        this.costApplMaxWt = source.getCostApplMaxWt();
        this.costApplWtUom = source.getCostApplWtUom();
        this.costApplMinDist = source.getCostApplMinDist();
        this.costApplMaxDist = source.getCostApplMaxDist();
        this.costApplDistUom = source.getCostApplDistUom();
        this.costApplMinLength = source.getCostApplMinLength();
        this.costApplMaxLength = source.getCostApplMaxLength();
        this.costApplLengthUom = source.getCostApplLengthUom();
        this.minCost = source.getMinCost();
        this.maxCost = source.getMaxCost();
        this.marginType = source.getMarginType();
        this.unitMargin = source.getUnitMargin();
        this.marginPercent = source.getMarginPercent();
        this.marginDollarAmt = source.getMarginDollarAmt();
        this.effDate = source.getEffDate();
        this.expDate = source.getExpDate();
        this.notes = source.getNotes();
        this.movementType = source.getMovementType();
        this.serviceType = source.getServiceType();
        this.status = source.getStatus();
        this.copiedFrom = source.getId();
        this.extNotes = source.getExtNotes();
        this.intNotes = source.getIntNotes();
        for (LtlAccGeoServicesEntity item : source.getLtlAccGeoServicesEntities()) {
            this.ltlAccGeoServicesEntities.add(new LtlAccGeoServicesEntity(item));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        this.id = pId;
    }

    public Long getLtlPricProfDetailId() {
        return ltlPricProfDetailId;
    }

    public void setLtlPricProfDetailId(Long pLtlPricProfDetailId) {
        this.ltlPricProfDetailId = pLtlPricProfDetailId;
    }

    public String getAccessorialType() {
        return accessorialType;
    }

    public void setAccessorialType(String pAccessorialType) {
        this.accessorialType = pAccessorialType;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String pBlocked) {
        this.blocked = pBlocked;
    }

    public LtlCostType getCostType() {
        return costType;
    }

    public void setCostType(LtlCostType pCostType) {
        this.costType = pCostType;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal pUnitCost) {
        this.unitCost = pUnitCost;
    }

    public void setCostApplMinWt(Long pCostApplMinWt) {
        this.costApplMinWt = pCostApplMinWt;
    }

    public Long getCostApplMinWt() {
        return costApplMinWt;
    }

    public void setCostApplMaxWt(Long pCostApplMaxWt) {
        this.costApplMaxWt = pCostApplMaxWt;
    }

    public Long getCostApplMaxWt() {
        return costApplMaxWt;
    }

    public void setCostApplWtUom(WeightUOM pCostApplWtUom) {
        this.costApplWtUom = pCostApplWtUom;
    }

    public WeightUOM getCostApplWtUom() {
        return costApplWtUom;
    }

    public void setCostApplMinDist(Long pCostApplMinDist) {
        this.costApplMinDist = pCostApplMinDist;
    }

    public Long getCostApplMinDist() {
        return costApplMinDist;
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

    public BigDecimal getCostApplMinLength() {
        return costApplMinLength;
    }

    public void setCostApplMinLength(BigDecimal costApplMinLength) {
        this.costApplMinLength = costApplMinLength;
    }

    public BigDecimal getCostApplMaxLength() {
        return costApplMaxLength;
    }

    public void setCostApplMaxLength(BigDecimal costApplMaxLength) {
        this.costApplMaxLength = costApplMaxLength;
    }

    public LengthUOM getCostApplLengthUom() {
        return costApplLengthUom;
    }

    public void setCostApplLengthUom(LengthUOM costApplLengthUom) {
        this.costApplLengthUom = costApplLengthUom;
    }

    public String getMarginType() {
        return marginType;
    }

    public void setMarginType(String pMarginType) {
        this.marginType = pMarginType;
    }

    public BigDecimal getUnitMargin() {
        return unitMargin;
    }

    public void setUnitMargin(BigDecimal pUnitMargin) {
        this.unitMargin = pUnitMargin;
    }

    public BigDecimal getMarginPercent() {
        return marginPercent;
    }

    public void setMarginPercent(BigDecimal pMarginPercent) {
        this.marginPercent = pMarginPercent;
    }

    public BigDecimal getMarginDollarAmt() {
        return marginDollarAmt;
    }

    public void setMarginDollarAmt(BigDecimal pMarginDollarAmt) {
        this.marginDollarAmt = pMarginDollarAmt;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date pEffDate) {
        this.effDate = DateUtility.truncateMilliseconds(pEffDate);
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date pExpDate) {
        this.expDate = DateUtility.truncateMilliseconds(pExpDate);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String pNotes) {
        this.notes = pNotes;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType pMovementType) {
        this.movementType = pMovementType;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        this.status = pStatus;
    }

    public Long getCopiedFrom() {
        return copiedFrom;
    }

    public void setCopiedFrom(Long copiedFrom) {
        this.copiedFrom = copiedFrom;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal pMinCost) {
        this.minCost = pMinCost;
    }

    public BigDecimal getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(BigDecimal pMaxCost) {
        this.maxCost = pMaxCost;
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

    public AccessorialTypeEntity getAccessorialTypeEntity() {
        return accessorialTypeEntity;
    }

    public void setAccessorialTypeEntity(AccessorialTypeEntity accessorialTypeEntity) {
        this.accessorialTypeEntity = accessorialTypeEntity;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer pVersion) {
        this.version = pVersion;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject pModification) {
        this.modification = pModification;
    }

    public List<LtlAccGeoServicesEntity> getLtlAccGeoServicesEntities() {
        return ltlAccGeoServicesEntities;
    }

    public void setLtlAccGeoServicesEntities(List<LtlAccGeoServicesEntity> pLtlAccGeoServicesEntities) {
        this.ltlAccGeoServicesEntities = pLtlAccGeoServicesEntities;
    }

    public Boolean getApplyBeforeFuel() {
        return applyBeforeFuel;
    }

    public void setApplyBeforeFuel(Boolean applyBeforeFuel) {
        this.applyBeforeFuel = applyBeforeFuel;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(getLtlPricProfDetailId())
                .append(getAccessorialType())
                .append(getBlocked())
                .append(getCostType())
                .append(getUnitCost())
                .append(getCostApplMinWt())
                .append(getCostApplMaxWt())
                .append(getCostApplWtUom())
                .append(getCostApplMinDist())
                .append(getCostApplMaxDist())
                .append(getCostApplDistUom())
                .append(getMinCost())
                .append(getMaxCost())
                .append(getMarginType())
                .append(getUnitMargin())
                .append(getMarginPercent())
                .append(getMarginDollarAmt())
                .append(getEffDate())
                .append(getExpDate())
                .append(getNotes())
                .append(getExtNotes())
                .append(getIntNotes())
                .append(getMovementType())
                .append(getServiceType())
                .append(getStatus())
                .append(getCostApplMinLength())
                .append(getCostApplMaxLength())
                .append(getCostApplLengthUom());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LtlAccessorialsEntity) {
            if (obj == this) {
                result = true;
            } else {
                LtlAccessorialsEntity other = (LtlAccessorialsEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getLtlPricProfDetailId(), other.getLtlPricProfDetailId())
                        .append(getAccessorialType(), other.getAccessorialType())
                        .append(getBlocked(), other.getBlocked())
                        .append(getCostType(), other.getCostType())
                        .append(getUnitCost(), other.getUnitCost())
                        .append(getCostApplMinWt(), other.getCostApplMinWt())
                        .append(getCostApplMaxWt(), other.getCostApplMaxWt())
                        .append(getCostApplWtUom(), other.getCostApplWtUom())
                        .append(getCostApplMinDist(), other.getCostApplMinDist())
                        .append(getCostApplMaxDist(), other.getCostApplMaxDist())
                        .append(getCostApplDistUom(), other.getCostApplDistUom())
                        .append(getMinCost(), other.getMinCost())
                        .append(getMaxCost(), other.getMaxCost())
                        .append(getMarginType(), other.getMarginType())
                        .append(getUnitMargin(), other.getUnitMargin())
                        .append(getMarginPercent(), other.getMarginPercent())
                        .append(getMarginDollarAmt(), other.getMarginDollarAmt())
                        .append(getEffDate(), other.getEffDate())
                        .append(getExpDate(), other.getExpDate())
                        .append(getNotes(), other.getNotes())
                        .append(getMovementType(), other.getMovementType())
                        .append(getServiceType(), other.getServiceType())
                        .append(getExtNotes(), other.getExtNotes())
                        .append(getIntNotes(), other.getIntNotes())
                        .append(getStatus(), other.getStatus())
                        .append(getCostApplMinLength(), other.getCostApplMinLength())
                        .append(getCostApplMaxLength(), other.getCostApplMaxLength())
                        .append(getCostApplLengthUom(), other.getCostApplLengthUom());

                //builder.append(getLtlAccGeoServicesEntities(), other.getLtlAccGeoServicesEntities());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("ltlPricProfDetailId", getLtlPricProfDetailId())
                .append("accessorialType", getAccessorialType())
                .append("blocked", getBlocked())
                .append("costType", getCostType())
                .append("unitCost", getUnitCost())
                .append("costApplMinWt", getCostApplMinWt())
                .append("costApplMaxWt", getCostApplMaxWt())
                .append("costApplWtUom", getCostApplWtUom())
                .append("costApplMinDist", getCostApplMinDist())
                .append("costApplMaxDist", getCostApplMaxDist())
                .append("costApplDistUom", getCostApplDistUom())
                .append("minCost", getMinCost())
                .append("maxCost", getMaxCost())
                .append("marginType", getMarginType())
                .append("unitMargin", getUnitMargin())
                .append("marginPercent", getMarginPercent())
                .append("marginDollarAmt", getMarginDollarAmt())
                .append("effDate", getEffDate())
                .append("expDate", getExpDate())
                .append("notes", getNotes())
                .append("movementType", getMovementType())
                .append("serviceType", getServiceType())
                .append("extNotes", getExtNotes())
                .append("intNotes", getIntNotes())
                .append("status", getStatus())
                .append("costApplMinLength", getCostApplMinLength())
                .append("costApplMaxLength", getCostApplMaxLength())
                .append("costApplLengthUom", getCostApplLengthUom())
                .append("modification", getModification());

        return builder.toString();
    }
}
