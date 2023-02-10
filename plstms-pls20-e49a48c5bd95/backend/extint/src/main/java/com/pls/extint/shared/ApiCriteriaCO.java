package com.pls.extint.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * Criteria object for querying API_LOG and API_EXCEPTIONS tables.
 * 
 * @author Pavani Challa
 * 
 */
public class ApiCriteriaCO implements Serializable {

    private static final long serialVersionUID = -3393246295090047004L;

    private Long loadId;

    private String bol;

    private String shipperReferenceNumber;

    private String carrierReferenceNumber;

    private Date fromDate;

    private Date toDate;

    private String category;

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

    public String getShipperReferenceNumber() {
        return shipperReferenceNumber;
    }

    public void setShipperReferenceNumber(String shipperReferenceNumber) {
        this.shipperReferenceNumber = shipperReferenceNumber;
    }

    public String getCarrierReferenceNumber() {
        return carrierReferenceNumber;
    }

    public void setCarrierReferenceNumber(String carrierReferenceNumber) {
        this.carrierReferenceNumber = carrierReferenceNumber;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
