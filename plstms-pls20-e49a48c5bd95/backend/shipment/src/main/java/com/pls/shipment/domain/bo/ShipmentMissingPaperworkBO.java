package com.pls.shipment.domain.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * Information about shipments that are missing the paperwork.
 * 
 * @author Pavani Challa
 * 
 */
public class ShipmentMissingPaperworkBO implements Serializable {

    private static final long serialVersionUID = 1456254157846955232L;

    private Long loadId;

    private String bol;

    private String carrierRefNum;

    private String shipperRefNum;

    private Long carrierOrgId;

    private Long shipperOrgId;

    private String carrierScac;

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

    public String getCarrierScac() {
        return carrierScac;
    }

    public void setCarrierScac(String carrierScac) {
        this.carrierScac = carrierScac;
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
