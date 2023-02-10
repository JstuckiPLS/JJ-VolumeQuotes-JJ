package com.pls.shipment.domain.bo;

/**
 * Zip DTO.
 *
 * @author Aleksandr Leshchenko
 */
public class ZipBO {
    private String zip;

    private String state;

    private String city;

    /**
     * Constructor.
     */
    public ZipBO() {
    }

    /**
     * Constructor.
     * 
     * @param zip
     *            field
     * @param state
     *            field
     * @param city
     *            field
     */
    public ZipBO(String zip, String state, String city) {
        this.zip = zip;
        this.state = state;
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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
}
