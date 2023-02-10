package com.pls.shipment.domain.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * Shipments to be tracked with carrier using API.
 * 
 * @author Pavani Challa
 * 
 */
public class ShipmentTrackingBO implements Serializable {

    private static final long serialVersionUID = 1456254157846955232L;

    private Long loadId;

    private String bol;

    private Long carrierOrgId;

    private Long shipperOrgId;

    private String carrierRefNum;

    private String shipperRefNum;

    private String carrierScac;

    private Long weight;

    private Integer pieces;

    private String loadStatus;

    private Date pickupDate;

    private String originZip;

    private String destZip;

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

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public String getCarrierRefNum() {
        return carrierRefNum;
    }

    public void setCarrierRefNum(String carrierRefNum) {
        this.carrierRefNum = carrierRefNum;
    }

    public String getShipperRefNum() {
        return shipperRefNum;
    }

    public void setShipperRefNum(String shipperRefNum) {
        this.shipperRefNum = shipperRefNum;
    }

    public String getCarrierScac() {
        return carrierScac;
    }

    public void setCarrierScac(String carrierScac) {
        this.carrierScac = carrierScac;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public String getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(String loadStatus) {
        this.loadStatus = loadStatus;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getDestZip() {
        return destZip;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }
}
