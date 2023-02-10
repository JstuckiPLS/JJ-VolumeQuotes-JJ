package com.pls.shipment.domain.sterling;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.sterling.utils.DateTimeAdapter;

/**
 * Load Tracking status class. Class designed to store status information for EDI 214 inbound communication.
 * 
 * @author Jasmin Dhamelia
 */
@XmlRootElement(name = "TrackingStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrackingStatusJaxbBO implements Serializable {

    private static final long serialVersionUID = -672823283694905546L;

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "StatusReason")
    private String statusReason;

    @XmlElement(name = "LoadStatus")
    private String loadStatus;

    @XmlElement(name = "Notes")
    private String notes;

    @XmlElement(name = "TransactionDate")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date transactionDate;

    @XmlElement(name = "TransactionDateTz")
    private String transactionDateTz;

    @XmlElement(name = "TrackingStatusAddress")
    private TrackingStatusAddressJaxbBO trackingStatusAddress;

    public String getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(String loadStatus) {
        this.loadStatus = loadStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionDateTz() {
        return transactionDateTz;
    }

    public void setTransactionDateTz(String transactionDateTz) {
        this.transactionDateTz = transactionDateTz;
    }

    public TrackingStatusAddressJaxbBO getTrackingStatusAddress() {
        return trackingStatusAddress;
    }

    public void setTrackingStatusAddress(TrackingStatusAddressJaxbBO trackingStatusAddress) {
        this.trackingStatusAddress = trackingStatusAddress;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TrackingStatusJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                TrackingStatusJaxbBO other = (TrackingStatusJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getStatus(), other.getStatus());
                builder.append(getLoadStatus(), other.getLoadStatus());
                builder.append(getStatusReason(), other.getStatusReason());
                builder.append(getNotes(), other.getNotes());
                builder.append(getTransactionDate(), other.getTransactionDate());
                builder.append(getTransactionDateTz(), other.getTransactionDateTz());
                builder.append(getTrackingStatusAddress(), other.getTrackingStatusAddress());
                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getStatus());
        builder.append(getLoadStatus());
        builder.append(getStatusReason());
        builder.append(getNotes());
        builder.append(getTransactionDate());
        builder.append(getTransactionDateTz());
        builder.append(getTrackingStatusAddress());
        return builder.toHashCode();
    }

}
