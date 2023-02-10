package com.pls.dto.address;

/**
 * Plain Address DTO.
 * 
 * @author Artem Arapov
 * 
 */
public class PlainAddressDTO {

    private Long id;

    private String address1;

    private String address2;

    private String name;

    private CountryDTO country;

    private ZipDTO zip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public ZipDTO getZip() {
        return zip;
    }

    public void setZip(ZipDTO zip) {
        this.zip = zip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
