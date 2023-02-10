package com.pls.dto.address;

/**
 * Zip DTO.
 *
 * @author Gleb Zgonikov
 */
public class ZipDTO {
    private String zip;

    private CountryDTO country;

    private String state;

    private String city;

    private String prefCity;

    private Boolean warning;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrefCity() {
        return prefCity;
    }

    public void setPrefCity(String prefCity) {
        this.prefCity = prefCity;
    }

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }
}
