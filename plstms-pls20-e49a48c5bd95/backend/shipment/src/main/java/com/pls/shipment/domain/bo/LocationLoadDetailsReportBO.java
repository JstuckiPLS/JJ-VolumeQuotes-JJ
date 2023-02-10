package com.pls.shipment.domain.bo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.domain.enums.ShipmentStatus;

/**
 * Load Details BO for Pickups And Deliveries customer report.
 * 
 * @author Aleksandr Leshchenko
 */
public class LocationLoadDetailsReportBO {
    private Long loadId;
    private String bolNumber;
    private String refNumber;
    private String poNumber;
    private ShipmentStatus status;
    private Date pickupDate;
    private Date estDeliveryDate;
    private String carrierName;
    private String shipper;
    private String consignee;
    @JsonIgnore
    private String origCity;
    @JsonIgnore
    private String origState;
    @JsonIgnore
    private String origZip;
    @JsonIgnore
    private String destCity;
    @JsonIgnore
    private String destState;
    @JsonIgnore
    private String destZip;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Date getEstDeliveryDate() {
        return estDeliveryDate;
    }

    public void setEstDeliveryDate(Date estDeliveryDate) {
        this.estDeliveryDate = estDeliveryDate;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getOrigCity() {
        return origCity;
    }

    public void setOrigCity(String origCity) {
        this.origCity = origCity;
    }

    public String getOrigState() {
        return origState;
    }

    public void setOrigState(String origState) {
        this.origState = origState;
    }

    public String getOrigZip() {
        return origZip;
    }

    public void setOrigZip(String origZip) {
        this.origZip = origZip;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getDestZip() {
        return destZip;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }

    /**
     * Get origin zip.
     * 
     * @return origin zip
     */
    public ZipBO getOrigin() {
        return new ZipBO(origZip, origState, origCity);
    }

    /**
     * Get destination zip.
     * 
     * @return destination zip
     */
    public ZipBO getDestination() {
        return new ZipBO(destZip, destState, destCity);
    }
}
