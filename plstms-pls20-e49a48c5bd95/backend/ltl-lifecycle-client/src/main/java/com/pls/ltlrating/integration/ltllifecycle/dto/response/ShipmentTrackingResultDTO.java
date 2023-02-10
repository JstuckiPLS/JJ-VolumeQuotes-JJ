package com.pls.ltlrating.integration.ltllifecycle.dto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class ShipmentTrackingResultDTO {

    /** Unique id of the load dispatch, in LTL lifecycle. */
    private String loadUUID;

    public ShipmentTrackingResultDTO() {
        this.loadUUID = UUID.randomUUID().toString();
    }
}
