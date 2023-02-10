package com.pls.dto.shipment;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * DTO for shipment event.
 * 
 * @author Gleb Zgonikov
 */
public class ShipmentEventDTO implements Comparable<ShipmentEventDTO> {

    private Long shipmentId;

    private String event;

    private String firstName;

    private String lastName;

    private LocalDateTime date;

    private String timezoneCode;

    private String city;

    private String stateCode;

    private String countryCode;

    private String carrierName;

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getTimezoneCode() {
        return timezoneCode;
    }

    public void setTimezoneCode(String timezoneCode) {
        this.timezoneCode = timezoneCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    /**
     * null check are present to avoid 'null' strings in grid instead of empty strings if first or last name is null.
     * @return empty string or firstName + lastName, but not null
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) {
            sb.append(firstName).append(' ');
        }

        if (lastName != null) {
            sb.append(lastName);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(ShipmentEventDTO o) {
        return new CompareToBuilder().append(this.date, o.date).toComparison();
    }
}
