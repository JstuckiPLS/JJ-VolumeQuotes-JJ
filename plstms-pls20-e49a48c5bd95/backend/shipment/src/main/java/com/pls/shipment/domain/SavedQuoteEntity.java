package com.pls.shipment.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;

/**
 * Saved Quote entity.
 *
 * @author Mikhail Boldinov, 18/03/13
 */
@Entity
@Table(name = "SAVED_QUOTES")
public class SavedQuoteEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -5611823531434962212L;

    public static final String Q_UPDATE_STATUS = "com.pls.shipment.domain.SavedQuoteEntity.Q_UPDATE_STATUS";
    public static final String Q_UPDATE_REF_NUM = "com.pls.shipment.domain.SavedQuoteEntity.Q_UPDATE_REF_NUM";
    public static final String Q_FIND_SAVED_QUOTES = "com.pls.shipment.domain.SavedQuoteEntity.Q_FIND_SAVED_QUOTES";
    public static final String Q_FIND_LOAD_ID_LIST_FOR_SAVED_QUOTE = "com.pls.shipment.domain.SavedQuoteEntity.Q_FIND_LOAD_ID_LIST_FOR_SAVED_QUOTE";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saved_quotes_sequence")
    @SequenceGenerator(name = "saved_quotes_sequence", sequenceName = "SAVED_QUOTES_SEQ", allocationSize = 1)
    @Column(name = "QUOTE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ORG_ID", nullable = false)
    private CarrierEntity carrier;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ROUTE_ID", nullable = false)
    private RouteEntity route;

    @Column(name = "MILEAGE")
    private Integer mileage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PICKUP", nullable = false)
    private Date pickupDate;

    @OneToMany(mappedBy = "savedQuote", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedQuoteAccessorialEntity> accessorials;

    @OneToMany(mappedBy = "quote", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedQuoteMaterialEntity> materials;

    @OneToOne(mappedBy = "quote", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SavedQuoteCostDetailsEntity costDetails;

    @Column(name = "QUOTE_REFERENCE_NUMBER")
    private String  quoteReferenceNumber;

    @Column(name = "SPECIAL_MESSAGE")
    private String specialMessage;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "CARRIER_REFERENCE_NUMBER")
    private String carrierReferenceNumber;

    @Column(name = "PO_NUM")
    private String poNum;

    @Column(name = "PICKUP_NUM")
    private String pickupNum;

    @Column(name = "BOL")
    private String bol;

    @Column(name = "GL_NUMBER")
    private String glNumber;

    @Column(name = "SO_NUMBER")
    private String soNumber;

    @Column(name = "VOLUME_QUOTE_ID", nullable = true)
    private String volumeQuoteId;

    @Column(name = "TRAILER")
    private String trailer;

    @Column(name = "COST_OVERRIDE")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo costOverride = StatusYesNo.NO;

    @Column(name = "REVENUE_OVERRIDE")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo revenueOverride = StatusYesNo.NO;

    @OneToMany(mappedBy = "savedQuote", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<SavedQuotePricDtlsEntity> savedQuotePricDtls;

    @Column(name = "blocked_from_booking")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo blockedFromBooking = StatusYesNo.NO;

    @OneToMany(mappedBy = "savedQuote", fetch = FetchType.LAZY)
    private Set<LoadEntity> loads;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;
    
    @Column(name = "EXTERNAL_UUID")
    private String externalUuid;
    
    @Column(name = "CARRIER_QUOTE_NUMBER")
    private String carrierQuoteNumber;
    
    @Column(name = "SERVICE_LEVEL_CODE")
    private String serviceLevelCode;
    
    @Column(name = "SERVICE_LEVEL_DESC")
    private String serviceLevelDescription;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public RouteEntity getRoute() {
        return route;
    }

    public void setRoute(RouteEntity route) {
        this.route = route;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Set<SavedQuoteAccessorialEntity> getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(Set<SavedQuoteAccessorialEntity> accessorials) {
        this.accessorials = accessorials;
    }

    public Set<SavedQuoteMaterialEntity> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<SavedQuoteMaterialEntity> materials) {
        this.materials = materials;
    }

    public SavedQuoteCostDetailsEntity getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(SavedQuoteCostDetailsEntity costDetails) {
        this.costDetails = costDetails;
    }

    public String getQuoteReferenceNumber() {
        return quoteReferenceNumber;
    }

    public void setQuoteReferenceNumber(String quoteReferenceNumber) {
        this.quoteReferenceNumber = quoteReferenceNumber;
    }

    public String getSpecialMessage() {
        return specialMessage;
    }

    public void setSpecialMessage(String specialMessage) {
        this.specialMessage = specialMessage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCarrierReferenceNumber() {
        return carrierReferenceNumber;
    }

    public void setCarrierReferenceNumber(String carrierReferenceNumber) {
        this.carrierReferenceNumber = carrierReferenceNumber;
    }

    public String getPoNum() {
        return poNum;
    }

    public void setPoNum(String poNum) {
        this.poNum = poNum;
    }

    public String getPickupNum() {
        return pickupNum;
    }

    public void setPickupNum(String pickupNum) {
        this.pickupNum = pickupNum;
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

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getVolumeQuoteId() {
        return volumeQuoteId;
    }

    public void setVolumeQuoteId(String volumeQuoteId) {
        this.volumeQuoteId = volumeQuoteId;
    }

    public StatusYesNo getRevenueOverride() {
        return revenueOverride;
    }

    public void setRevenueOverride(StatusYesNo revenueOverride) {
        this.revenueOverride = revenueOverride;
    }

    public StatusYesNo getCostOverride() {
        return costOverride;
    }

    public void setCostOverride(StatusYesNo costOverride) {
        this.costOverride = costOverride;
    }

    public Set<SavedQuotePricDtlsEntity> getSavedQuotePricDtls() {
        return savedQuotePricDtls;
    }

    public void setSavedQuotePricDtls(Set<SavedQuotePricDtlsEntity> savedQuotePricDtls) {
        this.savedQuotePricDtls = savedQuotePricDtls;
    }

    public StatusYesNo getBlockedFromBooking() {
        return blockedFromBooking;
    }

    public void setBlockedFromBooking(StatusYesNo blockedFromBooking) {
        this.blockedFromBooking = blockedFromBooking;
    }

    public Set<LoadEntity> getLoads() {
        return loads;
    }

    public void setLoads(Set<LoadEntity> loads) {
        this.loads = loads;
    }

    public String getExternalUuid() {
        return externalUuid;
    }

    public void setExternalUuid(String externalUuid) {
        this.externalUuid = externalUuid;
    }

    public String getCarrierQuoteNumber() {
        return carrierQuoteNumber;
    }

    public void setCarrierQuoteNumber(String carrierQuoteNumber) {
        this.carrierQuoteNumber = carrierQuoteNumber;
    }

    public String getServiceLevelCode() {
        return serviceLevelCode;
    }

    public void setServiceLevelCode(String serviceLevelCode) {
        this.serviceLevelCode = serviceLevelCode;
    }

    public String getServiceLevelDescription() {
        return serviceLevelDescription;
    }

    public void setServiceLevelDescription(String serviceLevelDescription) {
        this.serviceLevelDescription = serviceLevelDescription;
    }
}
