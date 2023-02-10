package com.pls.ltlrating.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.enums.FuelWeekDays;
import com.pls.ltlrating.domain.enums.UpchargeType;

/**
 * LtlFuelEntity.
 *
 * @author Stas Norochevskiy
 *
 */
@Entity
@Table(name = "LTL_FUEL")
public class LtlFuelEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 4795701831094096725L;

    public static final String ALL_FUEL_TRIGGERS_BY_PROFILE_DETAILS = "com.pls.domain.pricing.profile.ALL_FUEL_TRIGGERS_BY_PROFILE_DETAILS";

    public static final String ALL_FUEL_TRIGGERS = "com.pls.domain.pricing.profile.ALL_FUEL_TRIGGERS_BY_PROFILE_DETAILS_AND_STATUS";

    public static final String FUEL_TRIGGERS_BY_PROFILE_DETAIL = "com.pls.domain.pricing.profile.ACTIVE_EFFECTIVE_FUEL_TRIGGERS_BY_PROFILE_DETAIL";

    public static final String FUEL_TRIGGERS_FOR_PROFILE = "com.pls.domain.pricing.profile.ACTIVE_EFFECTIVE_FUEL_TRIGGERS_FOR_PROFILE";

    public static final String EXPIRED_FUEL_TRIGGERS = "com.pls.domain.pricing.profile.EXPIRED_FUEL_TRIGGERS_BY_DATE_AND_PROFILE_DETAILS";

    public static final String UPDATE_STATUS = "com.pls.domain.pricing.profile.UPDATE_STATUS";

    public static final String INACTIVATE_BY_PROFILE_STATEMENT = "com.pls.ltlrating.domain.LtlFuelEntity.INACTIVATE_BY_PROFILE_STATEMENT";

    public static final String EXPIRE_BY_IDS = "com.pls.ltlrating.domain.LtlFuelEntity.EXPIRE_BY_IDS";

    public static final String FIND_CSP_FUEL_COPIED_FROM = "com.pls.ltlrating.domain.LtlFuelEntity.FIND_CSP_FUEL_COPIED_FROM";

    public static final String EXPIRATE_CSP_BY_COPIED_FROM_STATEMENT =
            "com.pls.ltlrating.domain.LtlFuelEntity.EXPIRATE_CSP_BY_COPIED_FROM_STATEMENT";

    public static final String UPDATE_CSP_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlFuelEntity.UPDATE_CSP_STATUS_STATEMENT";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID = "com.pls.ltlrating.domain.LtlFuelEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_fuel_seq")
    @SequenceGenerator(name = "ltl_fuel_seq",
                       sequenceName = "LTL_FUEL_SEQ", allocationSize = 1)
    @Column(name = "LTL_FUEL_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long ltlPricingProfileId;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DOT_REGION_ID")
    private DotRegionEntity dotRegion;

    @Column(name = "DOT_REGION_ID", updatable = false, insertable = false)
    private Long dotRegionId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LTL_FUEL_ID", nullable = false)
    private List<LtlFuelGeoServicesEntity> ltlFuelGeoServices = new ArrayList<LtlFuelGeoServicesEntity>();

    //This field can be nullable for margin set up.
    @Column(name = "EFF_DAY")
    @Enumerated(EnumType.STRING)
    private FuelWeekDays effectiveDay;

    /**
     * Values will be FL/PC. FL = Flat and PC = Percent.
     * These are the standard values in other tables to represent Flat/Percent types.
     * User can enter and save both Upcharge Flat and Upcharge Percent rates
     * but only one is effective at any point of time.
     * Hence this new column is added to set what type of upcharge is effective.
     */
    @Column(name = "UPCHARGE_TYPE")
    @Enumerated(EnumType.STRING)
    private UpchargeType upchargeType;

    @Column(name = "UPCHARGE_FLAT")
    private BigDecimal upchargeFlat;

    @Column(name = "UPCHARGE_PERCENT")
    private BigDecimal upchargePercent;

    @Column(name = "EXP_DATE")
    private Date expirationDate;

    @Column(name = "EFF_DATE")
    private Date effectiveDate;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    /**
     * Default Constructor.
     */
    public LtlFuelEntity() {
    }

    /**
     * Copying constructor.
     *
     * @param source
     *          Entity to clone.
     */
    public LtlFuelEntity(LtlFuelEntity source) {
        this.dotRegion = source.getDotRegion();
        this.effectiveDate = source.getEffectiveDate();
        this.expirationDate = source.getExpirationDate();
        this.effectiveDay = source.getEffectiveDay();
        this.status = source.getStatus();
        this.upchargeType = source.getUpchargeType();
        this.upchargeFlat = source.getUpchargeFlat();
        this.upchargePercent = source.getUpchargePercent();
        this.copiedFrom = source.getId();

        for (LtlFuelGeoServicesEntity geoEntity : source.getLtlFuelGeoServices()) {
            this.ltlFuelGeoServices.add(new LtlFuelGeoServicesEntity(geoEntity));
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLtlPricingProfileId() {
        return ltlPricingProfileId;
    }

    public void setLtlPricingProfileId(Long id) {
        this.ltlPricingProfileId = id;
    }

    public DotRegionEntity getDotRegion() {
        return dotRegion;
    }

    /**
     * Set dotRegionFuel and dotRegionFuelId.
     *
     * @param dotRegion
     *            new DotRegionEntity object
     */
    public void setDotRegion(DotRegionEntity dotRegion) {
        this.dotRegion = dotRegion;
        this.dotRegionId = (dotRegion != null) ? dotRegion.getId() : null;
    }

    public Long getDotRegionId() {
        return dotRegionId;
    }

    public List<LtlFuelGeoServicesEntity> getLtlFuelGeoServices() {
        return ltlFuelGeoServices;
    }

    public void setLtlFuelGeoServices(List<LtlFuelGeoServicesEntity> ltlFuelGeoServices) {
        this.ltlFuelGeoServices = ltlFuelGeoServices;
    }

    public FuelWeekDays getEffectiveDay() {
        return effectiveDay;
    }

    public void setEffectiveDay(FuelWeekDays effectiveDay) {
        this.effectiveDay = effectiveDay;
    }

    public UpchargeType getUpchargeType() {
        return upchargeType;
    }

    public void setUpchargeType(UpchargeType upchargeType) {
        this.upchargeType = upchargeType;
    }

    public BigDecimal getUpchargeFlat() {
        return upchargeFlat;
    }

    public void setUpchargeFlat(BigDecimal upchargeFlat) {
        this.upchargeFlat = upchargeFlat;
    }

    public BigDecimal getUpchargePercent() {
        return upchargePercent;
    }

    public void setUpchargePercent(BigDecimal upchargePercent) {
        this.upchargePercent = upchargePercent;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.ltlPricingProfileId).
                append(this.upchargeType).
                append(this.upchargeFlat).
                append(this.upchargePercent).
                append(this.expirationDate).
                append(this.effectiveDate).
                append(this.status).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlFuelEntity other = (LtlFuelEntity) obj;
        return new EqualsBuilder().
                append(this.ltlPricingProfileId, other.ltlPricingProfileId).
                append(this.upchargeType, other.upchargeType).
                append(this.upchargeFlat, other.upchargeFlat).
                append(this.upchargePercent, other.upchargePercent).
                append(this.expirationDate, other.expirationDate).
                append(this.effectiveDate, other.effectiveDate).
                append(this.status, other.status).
                isEquals();
    }

}
