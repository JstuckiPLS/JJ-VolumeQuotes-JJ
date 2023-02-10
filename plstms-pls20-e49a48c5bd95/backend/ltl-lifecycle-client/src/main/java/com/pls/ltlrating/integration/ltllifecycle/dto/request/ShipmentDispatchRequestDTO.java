package com.pls.ltlrating.integration.ltllifecycle.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;

import lombok.Data;

@Data
public class ShipmentDispatchRequestDTO {

    /** The application requesting the shipment. */

    private String requesterApp;

    /** The identifier of the customer we want the shipment for. */
    private String customerId;

    /** bill of lading assigned to load on our side */

    private String bol;

    /** pro assigned to load on our side */
    private String pro;

    private String carrierCode;

    /**
     * The quote uuid to use to request shipment. (If provided, the details of that quote will be used to do
     * the shipment.)
     */
    private String quoteUuid;
    
    /** The quote number provided by the quote provider, if we want to indicate this information on dispatch.
     * Note, if this is provided, we have to display the same number on the BOL document! */
    private String quoteNumber;
    
    /** In case of alternate quotes we need to specify the service level to use (e.g. ACCELERATED, EXPEDITE, CAPLOAD) */ 
    private String serviceLevelCode;
    
    /** The currency we expect to be invoiced in by the carrier for this dispatch. */
    private String currencyCode;

    private AddressDTO origin;

    private ContactDTO originContact;

    private AddressDTO destination;

    private ContactDTO destinationContact;

    private AddressDTO requesterAddress;

    private ContactDTO requesterContact;

    private LocalDate pickupDate;

    private LocalTime pickupStartTime;

    private LocalTime pickupEndTime;

    private LocalDate preferredDeliveryDate;

    private LocalTime deliveryStartTime;

    private LocalTime deliveryEndTime;

    private String pickupNotes;

    private String deliveryNotes;

    private List<DispatchItemDTO> items;

    private List<String> additionalServices;

    private ShipmentType shipmentType = ShipmentType.LTL;

}
