package com.pls.ltlrating.integration.ltllifecycle.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class ShipmentTrackRequestDTO {

    /** The application requesting the shipment. */
    private String requesterApp;

    /** The identifier of the customer we want the shipment for. */
    private String customerId;

    /** bill of lading assigned to load on our side */
    private String bol;

    /** pro assigned to load on our side */
    private String pro;

    private String carrierCode;

    private AddressDTO origin;

    private ContactDTO originContact;

    private AddressDTO destination;

    private ContactDTO destinationContact;

    private LocalDate pickupDate;

    private LocalTime pickupStartTime;

    private LocalTime pickupEndTime;

    private LocalDate preferredDeliveryDate;

    private LocalTime deliveryStartTime;

    private LocalTime deliveryEndTime;

}
