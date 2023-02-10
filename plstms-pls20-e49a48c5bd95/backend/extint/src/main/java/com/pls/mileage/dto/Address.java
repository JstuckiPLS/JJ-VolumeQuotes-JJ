package com.pls.mileage.dto;

public class Address {

    private String address;
    
    private String city;
    
    private String stateCode;
    
    private String postalCode;
    
    private String countryCode;
    
    private int sequenceInRoute;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getSequenceInRoute() {
        return sequenceInRoute;
    }

    public void setSequenceInRoute(int sequenceInRoute) {
        this.sequenceInRoute = sequenceInRoute;
    }

}
