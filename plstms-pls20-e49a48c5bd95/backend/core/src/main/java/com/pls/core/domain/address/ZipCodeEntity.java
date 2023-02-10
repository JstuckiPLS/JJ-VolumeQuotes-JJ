package com.pls.core.domain.address;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;

/**
 * Zip codes entity.
 *
 * @author Sergey Kirichenko
 */
@Entity
@Table(name = "ZIPCODES")
public class ZipCodeEntity implements Identifiable<ZipCodePK>, Comparable<ZipCodeEntity> {
    private static final long serialVersionUID = 6127018348623894983L;

    public static final String Q_SEARCH = "com.pls.core.domain.address.ZipCodeEntity.Q_SEARCH";
    public static final String Q_IS_ZIP_CODE_EXIST = "com.pls.core.domain.address.ZipCodeEntity.Q_IS_ZIP_CODE_EXIST";
    public static final String Q_IS_STATE_CODE_EXIST = "com.pls.core.domain.address.ZipCodeEntity.Q_IS_STATE_CODE_EXIST";
    public static final String Q_IS_CITY_EXIST = "com.pls.core.domain.address.ZipCodeEntity.Q_IS_CITY_EXIST";
    public static final String Q_IS_COUNTRY_EXIST = "com.pls.core.domain.address.ZipCodeEntity.Q_IS_COUNTRY_EXIST";
    public static final String Q_GET_ZIP_CODES_FOR_CACHE = "com.pls.core.domain.address.ZipCodeEntity.Q_GET_ZIP_CODES_FOR_CACHE";
    public static final String Q_GET_DEFAULT_BY_ZIP_AND_COUNTRY = "com.pls.core.domain.address.ZipCodeEntity.Q_GET_DEFAULT_BY_ZIP_AND_COUNTRY";
    
    @EmbeddedId
    private ZipCodePK id;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "STATE_CODE")
    private String stateCode;

    @Column(name = "CITY")
    private String city;

    @Column(name = "COUNTRY_CODE", insertable = false, updatable = false)
    private String countryCode;

    @Column(name = "PREF_LAST_LINE_NAME")
    private String prefCity;

    @Column(name =  "LATITUDE")
    private BigDecimal latitude;

    @Column(name =  "LONGITUDE")
    private BigDecimal longitude;

    @Column(name = "TIME_ZONE")
    private String timeZone;

    @Column(name = "DAY_LIGHT_SAVING")
    @Type(type = "yes_no")
    private Boolean dayLightSaving;

    @Formula("UPPER(CITY || ', ' || STATE_CODE || ', ' || ZIP_CODE)")
    private String fullAddress;

    @Column(name = "WARNING_FLAG")
    @Type(type = "yes_no")
    private Boolean warning;

    @Override
    public ZipCodePK getId() {
        return id;
    }

    @Override
    public void setId(ZipCodePK id) {
        this.id = id;
    }

    /**
     * Getter for the field zipCode.
     *
     * @return the value for the field zipCode.
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Setter for the field zipCode.
     *
     * @param zipCode the value to set for the field.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Getter for the field stateCode.
     *
     * @return the value for the field stateCode.
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * Setter for the field stateCode.
     *
     * @param stateCode the value to set for the field.
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * Getter for the field city.
     *
     * @return the value for the field city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter for the field city.
     *
     * @param city the value to set for the field.
     */
    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPrefCity() {
        return prefCity;
    }

    public void setPrefCity(String prefCity) {
        this.prefCity = prefCity;
    }

    /**
     * Getter for the field latitude.
     *
     * @return the value for the field latitude.
     */
    public BigDecimal getLatitude() {
        return latitude;
    }

    /**
     * Setter for the field latitude.
     *
     * @param latitude the value to set for the field.
     */
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter for the field longitude.
     *
     * @return the value for the field longitude.
     */
    public BigDecimal getLongitude() {
        return longitude;
    }

    /**
     * Setter for the field longitude.
     *
     * @param longitude the value to set for the field.
     */
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for the field timeZone.
     *
     * @return the value for the field timeZone.
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Setter for the field timeZone.
     *
     * @param timeZone the value to set for the field.
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Getter for the field dayLightSaving.
     *
     * @return the value for the field dayLightSaving.
     */
    public Boolean getDayLightSaving() {
        return dayLightSaving;
    }

    /**
     * Setter for the field dayLightSaving.
     *
     * @param dayLightSaving the value to set for the field.
     */
    public void setDayLightSaving(Boolean dayLightSaving) {
        this.dayLightSaving = dayLightSaving;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("COUNTRY_CODE", getCountryCode())
                .append("STATE_CODE", getStateCode()).append("CITY", getCity())
                .append("ZIP_CODE", getZipCode()).toString();
    }

    @Override
    public int compareTo(ZipCodeEntity o) {
        return fullAddress.compareToIgnoreCase(o.getFullAddress());
    }
}
