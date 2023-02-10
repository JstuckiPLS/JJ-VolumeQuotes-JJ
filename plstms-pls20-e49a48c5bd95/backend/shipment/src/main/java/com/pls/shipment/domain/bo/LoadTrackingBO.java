package com.pls.shipment.domain.bo;

import java.util.Date;

/**
 * BO to retrieve Load Tracking Information from DB.
 * 
 * @author Aleksandr Leshchenko
 */
public class LoadTrackingBO {
    private Date trackingDate;
    private String timezoneCode;
    private String userFirstName;
    private String userLastName;
    private Long shipmentId;
    private String city;
    private String state;
    private String country;
    private String carrierName;
    private Long source;
    private String freeMessage;
    private String statusDescription;
    private String statusCode;
    private Date departureTime;

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

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
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

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public String getFreeMessage() {
        return freeMessage;
    }

    public void setFreeMessage(String freeMessage) {
        this.freeMessage = freeMessage;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
