package com.pls.dto.quote;

import com.pls.dto.enums.CustomerStatusReason;
import com.pls.dto.enums.SavedQuoteExpirationResolution;
import com.pls.dto.shipment.ShipmentDTO;

/**
 * DTO which contains SavedShipmentDTO + resolution about saved shipment age.
 * Resolution can be only 2 types: expired (older than 7 days old) or normal (younger than 7 days old).
 * Normal resolution means that ShipmentDTO should be used on CreateQuote page of QuoteRater wizard.
 *
 * @author Alexey Tarasyuk
 */
public class SavedShipmentLoadDTO {

    private SavedQuoteExpirationResolution resolution;

    private ShipmentDTO shipmentDTO;

    private CustomerStatusReason customerStatusReason;

    public SavedQuoteExpirationResolution getResolution() {
        return resolution;
    }

    public void setResolution(SavedQuoteExpirationResolution resolution) {
        this.resolution = resolution;
    }

    public ShipmentDTO getShipmentDTO() {
        return shipmentDTO;
    }

    public void setShipmentDTO(ShipmentDTO shipmentDTO) {
        this.shipmentDTO = shipmentDTO;
    }

    public CustomerStatusReason getCustomerStatus() {

        return customerStatusReason;
    }

    public void setCustomerStatusReason(String customerStatus) {
        this.customerStatusReason = CustomerStatusReason.getByValue(customerStatus);
    }
}
