package com.pls.ltlrating.domain;

import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Ltl pricing profile entity.
 *
 * @author Mikhail Boldinov, 22/02/13
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "LTL_PRICING_PROFILE")
public class LtlPricingProfileEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -4313513250960207520L;

    public static final String Q_BLANKET_PROFILE_BY_CARRIER = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_BLANKET_PROFILE_BY_CARRIER";
    public static final String Q_MARGIN_PROFILE_BY_ORG_ID = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_MARGIN_PROFILE_BY_ORG_ID";
    public static final String Q_MARGIN_PROFILE_DETAIL_ID_BY_ORG_ID =
            "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_MARGIN_PROFILE_DETAIL_ID_BY_ORG_ID";
    public static final String UPDATE_STATUSES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.UPDATE_STATUSES";

    public static final String FIND_PRICING_TYPE_BY_ID_QUERY =
            "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.FIND_PRICING_TYPE_BY_ID_QUERY";

    public static final String FIND_PRICING_TYPE_BY_DETAIL_QUERY =
            "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.FIND_PRICING_TYPE_BY_DETAIL_QUERY";

    public static final String FIND_CHILD_PROFILE_BY_DETAILS_ID_QUERY =
            "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.FIND_CHILD_PROFILE_BY_DETAILS_ID_QUERY";

    public static final String Q_FIND_CSP_BY_PARENT_ID =
            "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_FIND_CSP_BY_PARENT_ID";

    public static final String Q_FIND_CSP_DETAILS_BY_PARENT_DETAIL_ID =
            "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_FIND_CSP_DETAILS_BY_PARENT_DETAIL_ID";

    public static final String Q_GET_PRICES_FOR_EXPORT = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_GET_PRICES_FOR_EXPORT";
    public static final String Q_GET_ACCESSORIALS_FOR_EXPORT
            = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_GET_ACCESSORIALS_FOR_EXPORT";
    public static final String Q_GET_FUEL_FOR_EXPORT = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_GET_FUEL_FOR_EXPORT";
    public static final String Q_GET_PALLET_PRICES_FOR_EXPORT
            = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_GET_PALLET_PRICES_FOR_EXPORT";

    public static final String FIND_TARIFFS = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.FIND_TARIFFS";

    public static final String GET_CARRIER_RATES_WITH_USE_BLANKET
            = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_CARRIER_RATES_WITH_USE_BLANKET";
    public static final String GET_CUSTOMER_PRICING_PROFILE =
            "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_CUSTOMER_PRICING_PROFILE";
    public static final String GET_BENCHMARK_RATES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_BENCHMARK_RATES";
    public static final String GET_CARRIER_RATES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_CARRIER_RATES";
    public static final String GET_FS_TRIGGERS = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_FS_TRIGGERS";
    public static final String GET_GUARANTEED_RATES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_GUARANTEED_RATES";
    public static final String GET_ACCESSORIAL_RATES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_ACCESSORIAL_RATES";
    public static final String GET_PALLET_RATES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_PALLET_RATES";
    public static final String GET_DUPLICATE_PROFILES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_DUPLICATE_PROFILES";
    public static final String GET_CUSTOMER_PROFILES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_CUSTOMER_PROFILES";
    public static final String Q_GET_BLANKET_IDS_BY_CARRIERS_IDS
            = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.Q_GET_BLANKET_IDS_BY_CARRIERS_IDS";
    public static final String GET_BLANKET_API_PROFILES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_BLANKET_API_PROFILES";
    public static final String GET_LTLLC_PROFILES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_LTLLC_PROFILES";
    public static final String GET_LTLLC_PROFILE_SUMMARIES = "com.pls.ltlrating.domain.profile.LtlPricingProfileEntity.GET_LTLLC_PROFILE_SUMMARIES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_PRIC_PROF_SEQUENCE")
    @SequenceGenerator(name = "LTL_PRIC_PROF_SEQUENCE", sequenceName = "LTL_PRIC_PROF_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRICING_PROFILE_ID")
    private Long id;

    @Column(name = "RATE_NAME", unique = true, nullable = false)
    private String rateName;

    //This field can be nullable for Benchmark pricing
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARRIER_ORG_ID")
    private SimpleOrganizationEntity carrierOrganization;

    //This property is used to store the actual SCAC of a carrier for which a
    //pricing profile is created with dummy SCAC.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "act_carrier_org_id")
    private SimpleOrganizationEntity actCarrierOrganization;

    //This field can be nullable for Non-Benchmark pricing
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHIPPER_ORG_ID")
    private SimpleOrganizationEntity shipperOrganization;

    //This field can be nullable for Benchmark pricing
    @Column(name = "CARRIER_CODE", unique = true)
    private String carrierCode;

    //This field can be nullable for Non-Benchmark pricing
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LTL_PRICING_TYPE", insertable = false, updatable = false)
    private LtlPricingTypesEntity pricingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "LTL_PRICING_TYPE")
    private PricingType ltlPricingType;

    @Column(name = "EFF_DATE")
    private Date effDate;

    @Column(name = "EXP_DATE")
    private Date expDate;

    @Column(name = "BLOCKED")
    private String blocked;

    @Column(name = "ALIAS_NAME")
    private String aliasName;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.INACTIVE;

    @Column(name = "CARRIER_WEBSITE")
    private String carrierWebsite;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "PROHIBITED_COMMODITIES")
    private String prohibitedCommodities;

    @Column(name = "EXT_NOTES")
    private String extNotes;

    @Column(name = "INT_NOTES")
    private String intNotes;

    @Transient
    private Long copiedFromProfileId;

    @Transient
    private String copiedFromRateName;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LTL_PRICING_PROFILE_ID", nullable = false)
    private Set<LtlPricingApplicableCustomersEntity> applicableCustomers;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LTL_PRICING_PROFILE_ID", nullable = false)
    private Set<LtlPricingProfileDetailsEntity> profileDetails;

    @Column(name = "blocked_from_booking")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo blockedFromBooking = StatusYesNo.NO;
    
    @Column(name = "display_quote_number_on_bol", nullable = true)
    private Boolean displayQuoteNumberOnBol;
    
    @Column(name = "track_with_ltllc", nullable = true)
    private Boolean trackWithLTLLC;
    
    @Column(name = "dispatch_with_ltllc", nullable = true)
    private Boolean dispatchWithLTLLC;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public SimpleOrganizationEntity getCarrierOrganization() {
        return carrierOrganization;
    }

    public void setCarrierOrganization(SimpleOrganizationEntity carrierOrganization) {
        this.carrierOrganization = carrierOrganization;
    }

    public SimpleOrganizationEntity getShipperOrganization() {
        return shipperOrganization;
    }

    public void setShipperOrganization(SimpleOrganizationEntity shipperOrganization) {
        this.shipperOrganization = shipperOrganization;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public LtlPricingTypesEntity getPricingType() {
        return pricingType;
    }

    public void setPricingType(LtlPricingTypesEntity pricingType) {
        this.pricingType = pricingType;
    }

    public PricingType getLtlPricingType() {
        return ltlPricingType;
    }

    public void setLtlPricingType(PricingType ltlPricingType) {
        this.ltlPricingType = ltlPricingType;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effectiveDate) {
        this.effDate = effectiveDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expirationDate) {
        this.expDate = expirationDate;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCarrierWebsite() {
        return carrierWebsite;
    }

    public void setCarrierWebsite(String carrierWebsite) {
        this.carrierWebsite = carrierWebsite;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }

    public Long getCopiedFromProfileId() {
        return copiedFromProfileId;
    }

    public void setCopiedFromProfileId(Long copiedFromProfileId) {
        this.copiedFromProfileId = copiedFromProfileId;
    }

    public String getCopiedFromRateName() {
        return copiedFromRateName;
    }

    public void setCopiedFromRateName(String copiedFromRateName) {
        this.copiedFromRateName = copiedFromRateName;
    }

    public Set<LtlPricingProfileDetailsEntity> getProfileDetails() {
        return profileDetails;
    }

    public void setProfileDetails(Set<LtlPricingProfileDetailsEntity> profileDetails) {
        this.profileDetails = profileDetails;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<LtlPricingApplicableCustomersEntity> getApplicableCustomers() {
        return applicableCustomers;
    }

    public void setApplicableCustomers(Set<LtlPricingApplicableCustomersEntity> applicableCustomers) {
        this.applicableCustomers = applicableCustomers;
    }

    public SimpleOrganizationEntity getActCarrierOrganization() {
        return actCarrierOrganization;
    }

    public void setActCarrierOrganization(SimpleOrganizationEntity actCarrierOrganization) {
        this.actCarrierOrganization = actCarrierOrganization;
    }

    public StatusYesNo getBlockedFromBooking() {
        return blockedFromBooking;
    }

    public void setBlockedFromBooking(StatusYesNo blockedFromBooking) {
        this.blockedFromBooking = blockedFromBooking;
    }

    public Boolean getDisplayQuoteNumberOnBol() {
        return displayQuoteNumberOnBol;
    }

    public void setDisplayQuoteNumberOnBol(Boolean displayQuoteNumberOnBol) {
        this.displayQuoteNumberOnBol = displayQuoteNumberOnBol;
    }

    public Boolean getTrackWithLTLLC() {
        return trackWithLTLLC;
    }

    public void setTrackWithLTLLC(Boolean trackWithLTLLC) {
        this.trackWithLTLLC = trackWithLTLLC;
    }

    public Boolean getDispatchWithLTLLC() {
        return dispatchWithLTLLC;
    }

    public void setDispatchWithLTLLC(Boolean dispatchWithLTLLC) {
        this.dispatchWithLTLLC = dispatchWithLTLLC;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.rateName).append(this.carrierCode)
                .append(this.pricingType).append(this.blocked).append(this.status).append(this.note)
                .append(this.extNotes).append(this.intNotes).append(this.blockedFromBooking).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlPricingProfileEntity other = (LtlPricingProfileEntity) obj;
        return new EqualsBuilder().append(this.rateName, other.rateName).append(this.carrierOrganization,
                other.carrierOrganization).append(this.shipperOrganization, other.shipperOrganization).append(this.carrierCode,
                other.carrierCode).append(this.pricingType, other.pricingType).append(this.blocked,
                other.blocked).append(this.status, other.status).append(this.intNotes, other.intNotes)
                .append(this.actCarrierOrganization, other.actCarrierOrganization)
                .append(this.blockedFromBooking, other.blockedFromBooking)
                .append(this.extNotes, other.extNotes).isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("rateName", getRateName())
                .append("carrierOrganization", getCarrierOrganization())
                .append("shipperOrganization", getShipperOrganization())
                .append("carrierCode", getCarrierCode())
                .append("pricingType", getPricingType())
                .append("effDate", getEffDate())
                .append("expDate", getExpDate())
                .append("blocked", getBlocked())
                .append("aliasName", getAliasName())
                .append("status", getStatus())
                .append("carrierWebsite", getCarrierWebsite())
                .append("note", getNote())
                .append("extNotes", getExtNotes())
                .append("intNotes", getIntNotes())
                .append("actCarrierOrganization", getActCarrierOrganization())
                .append("blockedFromBooking", getBlockedFromBooking())
                .append("prohibitedCommodities", getProhibitedCommodities())
                .append("copiedFromProfileId", getCopiedFromProfileId())
                .append("modification", getModification());

        return builder.toString();
    }
}
