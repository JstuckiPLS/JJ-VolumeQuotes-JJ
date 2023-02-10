package com.pls.ltlrating.integration.ltllifecycle.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ShipmentDispatchResultDTO {

    /** Unique id of the load dispatch, in LTL lifecycle. */
    private String loadUUID;

    /** The BOL, if provided. */
    private String bol;

    /** The PRO number, if provided. */
    private String pro;

    /** The Pickup number, if provided. */
    private String pu;

    private LocalDateTime pickupDateTime;

    private String pickupNote;

}
