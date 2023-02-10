package com.pls.extint.shared;

import java.io.Serializable;
import java.util.Date;

import com.pls.extint.domain.enums.ApiRequestStatus;

/**
 * VO to hold the Api Log information when returning the data for search.
 * 
 * @author Pavani Challa
 *
 */
public class ApiLogVO implements Serializable {
    private static final long serialVersionUID = 4463614622572231651L;

    private Long id;

    private Long apiTypeId;

    private Long loadId;

    private String carrierReferenceNumber;

    private String shipperReferenceNumber;

    private String bol;

    private String errorMsg;

    private ApiRequestStatus status = ApiRequestStatus.IN_PROGRESS;

    private Date createdDate;

    private Long createdBy;

    private Date modifiedDate;

    private Long modifiedBy;

    private Integer version;

    private String trackingStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date dateCreated) {
        this.createdDate = dateCreated;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date dateModified) {
        this.modifiedDate = dateModified;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(String trackingStatus) {
        this.trackingStatus = trackingStatus;
    }
}
