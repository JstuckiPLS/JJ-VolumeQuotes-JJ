package com.pls.dto;

/**
 * DTO for address details.
 * 
 * @author Aleksandr Leshchenko
 */
public class PricingAddressDTO {
    private Long id;
    private String origin;
    private String destination;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
