package com.pls.core.domain.bo;

import com.pls.core.domain.PhoneNumber;

/**
 * BO to store phone numbers according to different country codes and area codes.<br>
 * For example, phone number +38(063)123 4567  Ext.: 123456 consists of:<br>
 * <ul>
 * <li>country code (38)</li><br>
 * <li>area code (063)</li><br>
 * <li>and phone number (1234567)</li>
 * <li>and extension (123456) (This field is optional)</li>
 * </ul>
 * 
 * @author Alexey Tarasyuk
 */
public class PhoneBO implements PhoneNumber {
    private String areaCode;
    private String countryCode;
    private String number;
    private String extension;

    @Override
    public String getAreaCode() {
        return areaCode;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    @Override
    public void setNumber(String phoneNumber) {
        number = phoneNumber;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public String toString() {
    	return countryCode + " (" + areaCode + ") " + number + ((this.extension == null) ? "" : " ext " + extension);
    }
}
