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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;

/**
 * Load details entity.
 * 
 * Database table comments:
 * <p>
 * LOAD_DATE - NOT USED
 * </p>
 * <p>
 * For Point Type = 'O' (Origin) <br/>
 * EARLY_SCHEDULED_ARRIVAL = Pick up no earlier than (PNET) <br/>
 * SCHEDULED_ARRIVAL = Pick up no later than (PNLT), <br/>
 * ARRIVAL = Gate Arrival date (Date/Time at which truck arrived the specified origin dock gate). <br/>
 * DEPARTURE = When truck loaded goods into it and left the dock. This is Confirm Pickup date. <br/>
 * </p>
 * <p>
 * For Point Type = 'D' (Destination) <br/>
 * EARLY_SCHEDULED_ARRIVAL = Delivery up no earlier than(DNET) <br/>
 * SCHEDULED_ARRIVAL = Delivery no later than(DNLT) <br/>
 * ARRIVAL = Gate Arrival date (Date/Time at which truck arrived the specified destination dock gate). <br/>
 * DEPARTURE = When truck unloaded goods into the dock. This is Confirm Delivery date. <br/>
 * </p>
 * <p>
 * <b>Note:</b> In PLS 2.0 we don't user PNLT and DNLT, only early arrival dates. So, SCHEDULED_ARRIVAL should
 * not be mapped!
 * </p>
 * 
 * @author Denis Zhupinsky
 * @author Vicheslav Krot
 */
@Entity
@Table(name = "LOAD_DETAILS")
public class LoadDetailsEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -7188321048844728492L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ARRIVAL")
    private Date arrival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARRIVAL_TZ")
    private TimeZoneEntity arrivalTimeZone;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ARRIVAL_WINDOW_END")
    private Date arrivalWindowEnd;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ARRIVAL_WINDOW_START")
    private Date arrivalWindowStart;

    @Column(name = "LOCATION_TYPE")
    private String locationType;

    @Column(name = "CONTACT")
    private String contact;

    @Column(name = "ADDRESS_CODE")
    private String addressCode;

    @Column(name = "CONTACT_EMAIL")
    private String contactEmail;

    @Column(name = "CONTACT_FAX")
    private String contactFax;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @Column(name = "CONTACT_PHONE")
    private String contactPhone;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEPARTURE")
    private Date departure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTURE_TZ")
    private TimeZoneEntity departureTimeZone;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EARLY_SCHEDULED_ARRIVAL")
    private Date earlyScheduledArrival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EARLY_SCHEDULED_ARRIVAL_TZ")
    private TimeZoneEntity earlyScheduledArrivalTimeZone;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_details_sequence")
    @SequenceGenerator(name = "load_details_sequence", sequenceName = "LOAD_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "LOAD_DETAIL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @Column(name = "LOAD_ACTION", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.LoadAction"),
            @Parameter(name = "identifierMethod", value = "getLoadAction"),
            @Parameter(name = "valueOfMethod", value = "getLoadActionBy") })
    private final LoadAction loadAction;

    @OneToMany(mappedBy = "loadDetail", fetch = FetchType.LAZY, orphanRemoval = true,
            cascade = CascadeType.ALL)
    private Set<LoadMaterialEntity> loadMaterials;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "POINT_TYPE", columnDefinition = "char(1)", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.PointType"),
            @Parameter(name = "identifierMethod", value = "getPointType"),
            @Parameter(name = "valueOfMethod", value = "getPointTypeBy") })
    private final PointType pointType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SCHEDULED_ARRIVAL")
    private Date scheduledArrival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULED_ARRIVAL_TZ")
    private TimeZoneEntity scheduledArrivalTimeZone;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "INT_NOTES")
    private String internalNotes;

    @Column(name = "SEQ_IN_ROUTE", nullable = false)
    private int seqInRoute;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    /**
     * Constructor to instantiate {@link LoadDetailsEntity} by manual.
     * 
     * @param loadAction
     *            Not <code>null</code> {@link LoadAction}.
     * @param pointType
     *            Not <code>null</code> {@link PointType}.
     */
    public LoadDetailsEntity(LoadAction loadAction, PointType pointType) {
        this.loadAction = loadAction;
        this.pointType = pointType;
        if (this.pointType == PointType.ORIGIN) {
            seqInRoute = 0;
        } else if (this.pointType == PointType.DESTINATION) {
            seqInRoute = 1;
        }
    }

    /**
     * Constructor for hibernate engine.
     */
    protected LoadDetailsEntity() {
        // Actually, after loading hibernate set valid data into this field. So in normal situation these
        // fields should be not nullable.
        loadAction = null;
        pointType = null;
    }

    public AddressEntity getAddress() {
        return address;
    }

    /**
     * Get gate arrival date. Goods are not loaded yet.
     * 
     * @return gate arrival date. Goods are not loaded yet.
     */
    public Date getArrival() {
        return arrival;
    }

    /**
     * Get gate arrival timezone.
     * 
     * @return gate arrival timezone.
     */
    public TimeZoneEntity getArrivalTimeZone() {
        return arrivalTimeZone;
    }

    /**
     * Get date and time until that carrier should arrive to the gate for pickup/delivery.
     * 
     * @return date and time until that carrier should arrive to the gate. End of pickup/delivery time
     *         "window".
     */
    public Date getArrivalWindowEnd() {
        return arrivalWindowEnd;
    }

    /**
     * Get date and time after that carrier should arrive to the gate for pickup/delivery.
     * 
     * @return date and time after that carrier should arrive to the gate. Start of pickup/delivery time
     *         "window".
     */
    public Date getArrivalWindowStart() {
        return arrivalWindowStart;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getContact() {
        return contact;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactFax() {
        return contactFax;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * Get date when goods are loaded and trucks leaves the gate. It is <code>Confirmed Pickup</code> for
     * Origin and <code>Confirmed Delivery</code> for destination.
     * 
     * @return date when goods are loaded and trucks leaves the gate
     */
    public Date getDeparture() {
        return departure;
    }

    /**
     * Get timezone when goods are loaded and trucks leaves the gate.
     * 
     * @return timezone when goods are loaded and trucks leaves the gate.
     */
    public TimeZoneEntity getDepartureTimeZone() {
        return departureTimeZone;
    }

    public Date getEarlyScheduledArrival() {
        return earlyScheduledArrival;
    }

    public TimeZoneEntity getEarlyScheduledArrivalTimeZone() {
        return earlyScheduledArrivalTimeZone;
    }

    @Override
    public Long getId() {
        return id;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public LoadAction getLoadAction() {
        return loadAction;
    }

    public Set<LoadMaterialEntity> getLoadMaterials() {
        return loadMaterials;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    /**
     * Get type of this details record.
     * 
     * @return type of this details record.
     */
    public PointType getPointType() {
        return pointType;
    }

    /**
     * Get scheduled arrival date. It is <code>Scheduled Pickup</code> for Origin and
     * <code>Scheduled Delivery</code> for destination.
     * 
     * @return scheduled arrival date
     */
    public Date getScheduledArrival() {
        return scheduledArrival;
    }

    /**
     * Get scheduled arrival timezone.
     * 
     * @return arrival timezone.
     */
    public TimeZoneEntity getScheduledArrivalTimeZone() {
        return scheduledArrivalTimeZone;
    }

    public int getSeqInRoute() {
        return seqInRoute;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    /**
     * Set gate arrival date. Goods are not loaded yet.
     * 
     * @param arrival
     *            gate arrival date. Goods are not loaded yet.
     */
    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    /**
     * Set gate arrival timezone.
     * 
     * @param arrivalTimeZone
     *            gate arrival timezone.
     */
    public void setArrivalTimeZone(TimeZoneEntity arrivalTimeZone) {
        this.arrivalTimeZone = arrivalTimeZone;
    }

    /**
     * Set arrival window end, see {@link LoadDetailsEntity#getArrivalWindowEnd()} comment.
     * 
     * @param arrivalWindowEnd
     *            arrival window end.
     */
    public void setArrivalWindowEnd(Date arrivalWindowEnd) {
        this.arrivalWindowEnd = arrivalWindowEnd;
    }

    /**
     * Set arrival window start, see {@link LoadDetailsEntity#getArrivalWindowEnd()} comment.
     * 
     * @param arrivalWindowStart
     *            arrival window start.
     */
    public void setArrivalWindowStart(Date arrivalWindowStart) {
        this.arrivalWindowStart = arrivalWindowStart;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactFax(String contactFax) {
        this.contactFax = contactFax;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * See {@link com.pls.shipment.domain.LoadDetailsEntity#getDeparture()} comment.
     * 
     * @param departure
     *            departure time.
     */
    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    /**
     * Set timezone when goods are loaded and trucks leaves the gate.
     * 
     * @param departureTimeZone
     *            timezone when goods are loaded and trucks leaves the gate.
     */
    public void setDepartureTimeZone(TimeZoneEntity departureTimeZone) {
        this.departureTimeZone = departureTimeZone;
    }

    public void setEarlyScheduledArrival(Date earlyScheduledArrival) {
        this.earlyScheduledArrival = earlyScheduledArrival;
    }

    public void setEarlyScheduledArrivalTimeZone(TimeZoneEntity earlyScheduledArrivalTimeZone) {
        this.earlyScheduledArrivalTimeZone = earlyScheduledArrivalTimeZone;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public void setLoadMaterials(Set<LoadMaterialEntity> loadMaterials) {
        this.loadMaterials = loadMaterials;
    }


    /**
     * Set scheduled arrival date. See {@link com.pls.shipment.domain.LoadDetailsEntity#getScheduledArrival()}
     * comment.
     * 
     * @param scheduledArrival
     *            scheduled arrival date
     */
    public void setScheduledArrival(Date scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    /**
     * Set scheduled arrival timezone.
     * 
     * @param scheduledArrivalTimeZone
     *            scheduled arrival timezone.
     */
    public void setScheduledArrivalTimeZone(TimeZoneEntity scheduledArrivalTimeZone) {
        this.scheduledArrivalTimeZone = scheduledArrivalTimeZone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setSeqInRoute(int seqInRoute) {
        this.seqInRoute = seqInRoute;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }
}
