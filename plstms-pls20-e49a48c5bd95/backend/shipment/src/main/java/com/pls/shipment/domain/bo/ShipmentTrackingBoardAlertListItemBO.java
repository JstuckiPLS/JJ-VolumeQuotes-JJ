package com.pls.shipment.domain.bo;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.ShipmentStatus;

/**
 * BO represents single line in table of shipment alerts.
 * 
 * @author Sergey Vovchuk
 */
public class ShipmentTrackingBoardAlertListItemBO {

    private Long id;

    private String bol;

    private String pro;

    private String customerName;

    private String carrierName;

    private ZonedDateTime pickupDate;

    private Date pickupWindowStart;

    private ZonedDateTime pickupWindowEnd;

    private String originTimezone;

    private String alertTypes;

    private boolean newAlert;

    private ShipmentStatus status;

    private Date estimatedDelivery;

    private String shipper;

    private String consignee;

    private ZonedDateTime createdDateTime;

    private String createdBy;

    private CityStateZip originAddress;

    private CityStateZip destinationAddress;

    private CarrierIntegrationType integrationType;

    private String indicators;

    private ZonedDateTime dispatchedDate;

    private String network;

    private String accountExecutive;

    private Integer pieces;

    private Integer weight;

    private ShipmentStatus shipmentStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public ZonedDateTime getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = ZonedDateTime.ofInstant(pickupDate.toInstant(), ZoneOffset.UTC);
    }

    public Date getPickupWindowStart() {
        return pickupWindowStart;
    }

    public void setPickupWindowStart(Date pickupWindowStart) {
        this.pickupWindowStart = pickupWindowStart;
    }

    public ZonedDateTime getPickupWindowEnd() {
        return pickupWindowEnd;
    }

    public void setPickupWindowEnd(Date pickupWindowEnd) {
        this.pickupWindowEnd = pickupWindowEnd != null
                ? ZonedDateTime.ofInstant(pickupWindowEnd.toInstant(), ZoneOffset.UTC) : null;
    }

    public String getOriginTimezone() {
        return originTimezone;
    }

    public void setOriginTimezone(String originTimezone) {
        this.originTimezone = originTimezone;
    }

    public String getAlertTypes() {
        return alertTypes;
    }

    public void setAlertTypes(String alertTypes) {
        this.alertTypes = alertTypes;
    }

    /**
     * Method appends alert type to existing set of alert types.
     * @param alertType - alert type
     */
    public void appendAlertType(String alertType) {
        if (StringUtils.isNotBlank(alertTypes)) {
            alertTypes = StringUtils.join(new String[] {alertTypes, alertType}, ", ");
        } else {
            alertTypes = alertType;
        }
    }

    public boolean isNewAlert() {
        return newAlert;
    }

    public void setNewAlert(boolean newAlert) {
        this.newAlert = newAlert;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Date getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(Date estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
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

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = ZonedDateTime.ofInstant(createdDateTime.toInstant(), ZoneOffset.UTC);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public CityStateZip getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(CityStateZip originAddress) {
        this.originAddress = originAddress;
    }

    public CityStateZip getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(CityStateZip destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public CarrierIntegrationType getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(CarrierIntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    public String getIndicators() {
        return indicators;
    }

    public void setIndicators(String indicators) {
        this.indicators = indicators;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public ZonedDateTime getDispatchedDate() {
        return dispatchedDate;
    }

    public void setDispatchedDate(Date dispatchedDate) {
        this.dispatchedDate = dispatchedDate == null ? null
                : ZonedDateTime.ofInstant(dispatchedDate.toInstant(), ZoneOffset.UTC);
    }

    public String getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(String accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }
}
