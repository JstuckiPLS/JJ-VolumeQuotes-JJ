package com.pls.smc3.dto;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.shared.AddressVO;

/**
 * Transit response DTO. Returns Destination, origin and scac response(carrier name, service type etc).
 * 
 * @author Pavani Challa
 * 
 */
public class TransitResponseDTO {

    private List<ScacResponse> scacResponses;
    private AddressVO destination;
    private AddressVO origin;
    private String shipmentId;

    public List<ScacResponse> getScacResponses() {
        return scacResponses;
    }

    public void setScacResponses(List<ScacResponse> scacResponses) {
        this.scacResponses = scacResponses;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
