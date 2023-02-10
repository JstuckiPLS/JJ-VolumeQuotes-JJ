package com.pls.ltlrating.integration.ltllifecycle.dto.message;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import com.pls.ltlrating.integration.ltllifecycle.dto.request.AddressDTO;

import lombok.Data;

/**
 * Tracking update about a shipment. An update can contain multiple shipment
 * updates denoting each change of the tracked shipment.
 */
@Data
public class ShipmentUpdateMessage {

    private String loadUUID;
    private String bolNumber;
    private String proNumber;
    private String puNumber;
    private String carrierCode;

    private ZonedDateTime lastReportedPickupDateTime;
    private ZonedDateTime lastReportedEstimatedDeliveryDate;
    private ZonedDateTime lastReportedDeliveryDateTime;

    private List<ShipmentUpdate> shipmentUpdates;

    @Data
    public static class ShipmentUpdate {
        
        /** The time when we received & processed this message. */
        private LocalDateTime messageReceivedTime;

        /** The tracking update status, describing the update. */
        private UpdateStatus status;

        /** The effect of the status update on the load status (DELIVERED, IN_TRANSIT, OUT_FOR_DELIVERY, ...) */
        private ShipmentStatus loadStatus;

        /** The time when this update happened on the carrier side. */
        private ZonedDateTime transactionDate;

        /** Free text note about the update. */
        private String notes;

        /** Location address of the update. */
        private AddressDTO address;
    }
}
