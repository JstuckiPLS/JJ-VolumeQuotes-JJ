package com.pls.shipment.domain.bo;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * BO for Pickups And Deliveries customer report.
 * 
 * @author Aleksandr Leshchenko
 */
public class LocationDetailsReportBO {
    private String type;
    private Integer days;
    private String city;
    private String state;
    private String zip;
    private Integer count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @JsonIgnore
    public int getHashCode() {
        return new HashCodeBuilder().append(type).append(days).append(city).append(state).append(zip).build();
    }
}
