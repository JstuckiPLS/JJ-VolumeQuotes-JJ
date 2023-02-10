package com.pls.extint.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.extint.domain.enums.ApiRequestStatus;

/**
 * Entity class for API_LOG. This class stores the request and response for all the API calls. Only for document API, responses are not saved.
 * 
 * @author Pavani Challa
 * 
 */
@Entity
@Table(name = "API_LOG")
public class ApiLogEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -8997544149966657004L;

    private static final int NUMBER_121 = 121;

    private static final int NUMBER_109 = 109;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "API_LOG_SEQUENCE")
    @SequenceGenerator(name = "API_LOG_SEQUENCE", sequenceName = "API_LOG_SEQ", allocationSize = 1)
    @Column(name = "API_LOG_ID")
    private Long id;

    @Column(name = "API_TYPE_ID", nullable = false)
    private Long apiTypeId;

    @Column(name = "LOAD_ID", nullable = true)
    private Long loadId;

    @Column(name = "BOL", nullable = true)
    private String bol;

    @Column(name = "CARRIER_REFERENCE_NUMBER", nullable = true)
    private String carrierReferenceNumber;

    @Column(name = "SHIPPER_REFERENCE_NUMBER", nullable = true)
    private String shipperReferenceNumber;

    @Column(name = "REQUEST", nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private String request;

    @Column(name = "RESPONSE", nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private String response;

    @Column(name = "ERROR", nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private String error;

    @Column(name = "ERROR_MESSAGE", nullable = true)
    private String errorMsg;

    @Enumerated(EnumType.STRING)
    private ApiRequestStatus status = ApiRequestStatus.IN_PROGRESS;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    @Column(name = "TRACKING_STATUS", nullable = true)
    private String trackingNote;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        this.id = pId;
    }

    public Long getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(Long apiTypeId) {
        this.apiTypeId = apiTypeId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getCarrierReferenceNumber() {
        return carrierReferenceNumber;
    }

    public void setCarrierReferenceNumber(String carrierReferenceNumber) {
        this.carrierReferenceNumber = carrierReferenceNumber;
    }

    public String getShipperReferenceNumber() {
        return shipperReferenceNumber;
    }

    public void setShipperReferenceNumber(String shipperReferenceNumber) {
        this.shipperReferenceNumber = shipperReferenceNumber;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String exception) {
        this.error = exception;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ApiRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ApiRequestStatus status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public String getTrackingNote() {
        return trackingNote;
    }

    public void setTrackingNote(String recentStatus) {
        this.trackingNote = recentStatus;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(NUMBER_109, NUMBER_121).append(getApiTypeId()).append(getBol()).append(getCarrierReferenceNumber())
                .append(getLoadId()).append(getShipperReferenceNumber()).append(getErrorMsg()).append(getStatus())
                .append(getTrackingNote()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof ApiLogEntity) {
            if (obj == this) {
                result = true;
            } else {
                ApiLogEntity other = (ApiLogEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getApiTypeId(), other.getApiTypeId()).append(getBol(), other.getBol())
                        .append(getCarrierReferenceNumber(), other.getCarrierReferenceNumber()).append(getLoadId(), other.getLoadId())
                        .append(getShipperReferenceNumber(), other.getShipperReferenceNumber()).append(getErrorMsg(), other.getErrorMsg())
                        .append(getStatus(), other.getStatus()).append(getModification(), other.getModification())
                        .append(getTrackingNote(), other.getTrackingNote());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("apiTypeId", getApiTypeId()).append("loadId", getLoadId()).append("bol", getBol())
                .append("carrierReferenceNumber", getCarrierReferenceNumber()).append("shipperReferenceNumber", getShipperReferenceNumber())
                .append("errorMsg", getErrorMsg()).append("status", getStatus()).append("modification", getModification())
                .append("recentStatus", getTrackingNote());

        return builder.toString();
    }

}
