package com.pls.smc3.dto;

import java.util.Date;
import java.util.Set;

import com.pls.core.shared.AddressVO;

/**
 *
 * DTO object for the transit request.
 *
 * @author Pavani Challa
 *
 */
public class TransitRequestDTO {

    private AddressVO destination;
    private AddressVO origin;
    private String shipmentId;
    private Set<ScacRequest> scacRequests;
    private Date pickupDate;

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Set<ScacRequest> getScacRequests() {
        return scacRequests;
    }

    public void setScacRequests(Set<ScacRequest> scacRequests) {
        this.scacRequests = scacRequests;
    }

    public AddressVO getDestination() {
        return destination;
    }

    public void setDestination(AddressVO destination) {
        this.destination = destination;
    }

    public AddressVO getOrigin() {
        return origin;
    }

    public void setOrigin(AddressVO origin) {
        this.origin = origin;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }
}
