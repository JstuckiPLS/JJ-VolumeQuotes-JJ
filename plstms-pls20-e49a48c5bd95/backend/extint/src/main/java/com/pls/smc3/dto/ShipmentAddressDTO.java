package com.pls.smc3.dto;

/**
 * Address request to get the terminal details.
 * 
 * @author PAVANI CHALLA
 *
 */
public class ShipmentAddressDTO {

    private String address1;
    private String address2;
    private String city;
    private String countryCode;
    private String localeID;
    private String postalCode;
    private String postalCodePlus4;
    private String state;
    private String timeZone;

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLocaleID() {
        return localeID;
    }

    public void setLocaleID(String localeID) {
        this.localeID = localeID;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCodePlus4() {
        return postalCodePlus4;
    }

    public void setPostalCodePlus4(String postalCodePlus4) {
        this.postalCodePlus4 = postalCodePlus4;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
