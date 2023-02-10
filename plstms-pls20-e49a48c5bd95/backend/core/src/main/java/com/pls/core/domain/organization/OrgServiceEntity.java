package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CarrierIntegrationType;

/**
 * Table Stores the organization services(API/EDI) for each categories (Tracking, Pickup, Rating, Imaging, Invoice).
 * 
 * @author Pavani Challa
 * 
 */
@Entity
@Table(name = "ORG_SERVICES")
public class OrgServiceEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 3878941985847582923L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORG_SERVICES_SEQ")
    @SequenceGenerator(name = "ORG_SERVICES_SEQ", sequenceName = "ORG_SERVICES_SEQ", allocationSize = 1)
    @Column(name = "ORG_SERVICE_ID")
    private Long id;

    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "TRACKING")
    @Enumerated(EnumType.STRING)
    private CarrierIntegrationType tracking = CarrierIntegrationType.EDI;

    @Column(name = "PICKUP")
    @Enumerated(EnumType.STRING)
    private CarrierIntegrationType pickup = CarrierIntegrationType.EDI;

    @Column(name = "RATING")
    private String rating;

    @Column(name = "INVOICE")
    @Enumerated(EnumType.STRING)
    private CarrierIntegrationType invoice = CarrierIntegrationType.EDI;

    @Column(name = "IMAGING")
    @Enumerated(EnumType.STRING)
    private CarrierIntegrationType imaging = CarrierIntegrationType.EDI;

    @OneToOne
    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Column(name = "MANUAL_TYPE_EMAIL")
    private String manualTypeEmail;
    
    @Column(name = "EDI_SEND_CURRENCY_ON_DISPATCH")
    private Boolean ediSendCurrencyOnDispatch;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public CarrierIntegrationType getTracking() {
        return tracking;
    }

    public void setTracking(CarrierIntegrationType tracking) {
        this.tracking = tracking;
    }

    public CarrierIntegrationType getPickup() {
        return pickup;
    }

    public void setPickup(CarrierIntegrationType pickup) {
        this.pickup = pickup;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public CarrierIntegrationType getInvoice() {
        return invoice;
    }

    public void setInvoice(CarrierIntegrationType invoice) {
        this.invoice = invoice;
    }

    public CarrierIntegrationType getImaging() {
        return imaging;
    }

    public void setImaging(CarrierIntegrationType imaging) {
        this.imaging = imaging;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public String getManualTypeEmail() {
        return manualTypeEmail;
    }

    public void setManualTypeEmail(String manualTypeEmail) {
        this.manualTypeEmail = manualTypeEmail;
    }

    public Boolean getEdiSendCurrencyOnDispatch() {
        return ediSendCurrencyOnDispatch;
    }

    public void setEdiSendCurrencyOnDispatch(Boolean ediSendCurrencyOnDispatch) {
        this.ediSendCurrencyOnDispatch = ediSendCurrencyOnDispatch;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getTracking()).append(getPickup()).append(getRating()).append(getImaging()).append(getInvoice())
                .append(getOrgId()).toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("tracking", getTracking()).append("pickup", getPickup()).append("rating", getRating())
                .append("imaging", getImaging()).append("invoice", getInvoice()).append("orgId", getOrgId())
                .append("modification", getModification());
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof OrgServiceEntity) {
            if (obj == this) {
                result = true;
            } else {
                OrgServiceEntity other = (OrgServiceEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getOrgId(), other.getOrgId()).append(getTracking(), other.getTracking()).append(getRating(), other.getRating())
                        .append(getPickup(), other.getPickup()).append(getImaging(), other.getImaging()).append(getInvoice(), other.getInvoice())
                        .append(getModification(), other.getModification());
                result = builder.isEquals();
            }
        }
        return result;
    }
}
