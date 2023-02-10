package com.pls.core.domain;

/**
 * Interface for object with phone, FAX and similar numbers..
 * 
 * @author Denis Zhupinsky
 */
public interface PhoneNumber {
    // TODO remove unused related XXXObject, DTO and DTO builders

    /**
     * Get area code.
     * 
     * @return May be <code>null</code> or empty {@link String}.
     */
    String getAreaCode();

    /**
     * Get country code.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    String getCountryCode();

    /**
     * Number value.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    String getNumber();

    /**
     * Extension value.
     * 
     * @return May be <code>null</code> or empty {@link String}.
     */
    String getExtension();

    /**
     * Area code.
     * 
     * @param areaCode
     *            May be <code>null</code> or empty {@link String}.
     */
    void setAreaCode(String areaCode);

    /**
     * Set country code.
     * 
     * @param countryCode
     *            Should be not <code>null</code> {@link String} value.
     */
    void setCountryCode(String countryCode);

    /**
     * Number string.
     * 
     * @param number
     *            Should be not <code>null</code> {@link String} value.
     */
    void setNumber(String number);

    /**
     * Extension string.
     * 
     * @param extension
     *            May be <code>null</code> or empty {@link String}.
     */
    void setExtension(String extension);
}
