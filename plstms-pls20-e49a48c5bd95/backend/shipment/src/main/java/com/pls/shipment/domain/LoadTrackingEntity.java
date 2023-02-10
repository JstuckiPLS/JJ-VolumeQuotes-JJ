package com.pls.shipment.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.user.UserEntity;

/**
 * Load tracking entity.
 * <p/>
 * Load Tracking contains the load status provided by carrier, not user operation => Ex: In transit.
 *
 * @author Mikhail Boldinov, 05/03/14
 */
@Entity
@Table(name = "LOAD_TRACKING")
public class LoadTrackingEntity implements Identifiable<Long> {

    public static final String Q_GET_BY_LOAD_ID = "com.pls.shipment.domain.LoadTrackingEntity.Q_GET_BY_LOAD_ID";
    public static final String Q_GET_AUDIT_BY_LOAD_ID = "com.pls.shipment.domain.LoadTrackingEntity.Q_GET_AUDIT_BY_LOAD_ID";

    private static final long serialVersionUID = -3227049905824869382L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOAD_TRACKING_SEQUENCE")
    @SequenceGenerator(name = "LOAD_TRACKING_SEQUENCE", sequenceName = "LOAD_TRACKING_SEQ", allocationSize = 1)
    @Column(name = "TRACKING_ID")
    private Long id;

    @Transient
    private String scac;

    @Transient
    private String bol;

    @Transient
    private String pro;

    @Column(name = "LOAD_ID", nullable = false, updatable = false)
    private Long loadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", insertable = false, updatable = false)
    private LoadEntity load;

    @Column(name = "TRACK_STATUS_CODE")
    private String statusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "TRACK_STATUS_CODE", referencedColumnName = "TRACK_STATUS_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "SOURCE", referencedColumnName = "SOURCE", insertable = false, updatable = false)
    })
    private LoadTrackingStatusEntity status;

    @Column(name = "STATUS_REASON_CODE")
    private String statusReasonCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_REASON_CODE", insertable = false, updatable = false)
    private LoadReasonTrackingStatusEntity statusReason;

    @Column(name = "TRACK_DATE_TIME")
    private Date trackingDate;

    @Column(name = "TRACK_TIMEZONE")
    private String timezoneCode;

    @Column(name = "TRACK_POSTAL_CODE")
    private String postalCode;

    @Column(name = "TRACK_CITY")
    private String city;

    @Column(name = "TRACK_STATE")
    private String state;

    @Column(name = "TRACK_COUNTRY")
    private String country;

    @Column(name = "ADDL_MSG")
    private String freeMessage;

    @Column(name = "SOURCE")
    private Long source;

    @Column(name = "EDI_ACCOUNT")
    private String ediAccount;

    @Column(name = "DEPARTURE_DATE_TIME")
    private Date departureTime;

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ID")
    private CarrierEntity carrier;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = false, insertable = false, updatable = false)
    private UserEntity createdByUser;

    @Column(name = "CARRIER_PICKUP_CONFIRMATION")
    private String pickupConfirmation;

    public String getPickupConfirmation() {
        return pickupConfirmation;
    }

    public void setPickupConfirmation(String pickupConfirmation) {
        this.pickupConfirmation = pickupConfirmation;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public LoadEntity getLoad() {
        return load;
    }

    /**
     * Sets the load and load Id.
     * @param load the load entity to be set
     */
    public void setLoad(LoadEntity load) {
        this.load = load;
        this.loadId = load.getId();
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public LoadTrackingStatusEntity getStatus() {
        return status;
    }

    public void setStatus(LoadTrackingStatusEntity status) {
        this.status = status;
    }

    public String getStatusReasonCode() {
        return statusReasonCode;
    }

    public void setStatusReasonCode(String statusReasonCode) {
        this.statusReasonCode = statusReasonCode;
    }

    public Date getTrackingDate() {
        return trackingDate;
    }

    public void setTrackingDate(Date trackingDate) {
        this.trackingDate = trackingDate;
    }

    public String getTimezoneCode() {
        return timezoneCode;
    }

    public void setTimezoneCode(String timezoneCode) {
        this.timezoneCode = timezoneCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFreeMessage() {
        return freeMessage;
    }

    public void setFreeMessage(String freeMessage) {
        this.freeMessage = freeMessage;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public UserEntity getCreatedByUser() {
        return createdByUser;
    }

    public LoadReasonTrackingStatusEntity getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(LoadReasonTrackingStatusEntity statusReason) {
        this.statusReason = statusReason;
    }

    public String getEdiAccount() {
        return ediAccount;
    }

    public void setEdiAccount(String ediAccount) {
        this.ediAccount = ediAccount;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getBol()).append(getScac()).append(getPro()).append(getCity())
                .append(getState()).append(getCountry()).append(getPostalCode()).append(getSource())
                .append(getEdiAccount()).append(getDepartureTime()).append(getTrackingDate()).append(getTimezoneCode())
                .append(getStatusCode()).append(getStatusReason()).append(getFreeMessage()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LoadTrackingEntity) {
            if (obj == this) {
                result = true;
            } else {
                final LoadTrackingEntity other = (LoadTrackingEntity) obj;
                result = new EqualsBuilder().append(getBol(), other.getBol())
                        .append(getScac(), other.getScac()).append(getPro(), other.getPro()).append(getPostalCode(), other.getPostalCode())
                        .append(getCity(), other.getCity()).append(getCountry(), other.getCountry())
                        .append(getDepartureTime(), other.getDepartureTime()).append(getSource(), other.getSource())
                        .append(getState(), other.getState()).append(getTrackingDate(), other.getTrackingDate())
                        .append(getTimezoneCode(), other.getTimezoneCode()).append(getStatusCode(), other.getStatusCode())
                        .append(getStatusReason(), other.getStatusReason()).append(getFreeMessage(), other.getFreeMessage())
                        .append(getEdiAccount(), other.getEdiAccount()).isEquals();
            }
        }
        return result;
    }
}
