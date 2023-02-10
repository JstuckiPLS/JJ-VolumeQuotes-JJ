package com.pls.core.shared;

import java.io.Serializable;

/**
 * Address VO.
 *
 * @author Hima Chala Bindu
 */
public class AddressVO implements Serializable {

    private static final long serialVersionUID = 3212341223845938377L;

    private String address1;

    private String address2;

    private String city;

    private String stateCode;

    private String postalCode;

    private String formattedPostalCode;

    private String countryCode = "USA";

    /**
     * No argument Constructor.
     */
    public AddressVO() { }

    /**
     * Constructor that accepts Address VO as argument.
     * @param address - Address VO.
     */
    public AddressVO(AddressVO address) {
        this.address1 = address.getAddress1();
        this.address2 = address.getAddress2();
        this.city = address.getCity();
        this.stateCode = address.getStateCode();
        this.postalCode = address.getPostalCode();
        this.countryCode = address.getCountryCode();
        this.formattedPostalCode = address.getFormattedPostalCode();
    }

    /**
     * Method returns address1.
     * @return the address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Method sets address1.
     * @param address1 the address1 to set
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Method returns address2.
     * @return the address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Method sets address2.
     * @param address2 the address2 to set
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Method returns city.
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Method sets city.
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Method return state code.
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * Method sets state code.
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * Method returns postal code.
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Method sets postal code.
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Method returns formatted postal code(ASCII equivalent of the postal code for Canadian postal codes).
     * @return the formatted postalCode
     */
    public String getFormattedPostalCode() {
        return formattedPostalCode;
    }

    /**
     * Method sets formatted postal code(ASCII equivalent of the postal code for Canadian postal codes).
     * @param formattedPostalCode - the formatted postalCode
     */
    public void setFormattedPostalCode(String formattedPostalCode) {
        this.formattedPostalCode = formattedPostalCode;
    }

    /**
     * Method returns country code.
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Method sets country code.
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


}
