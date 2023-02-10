package com.pls.ltlrating.integration.ltllifecycle.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;

import lombok.Data;

/**
 * Request to retrieve quotes for a defined origin & destination address
 */
@Data
public class QuoteRequestDTO {

    /** The application requesting the quote. */
    private String requesterApp;
    
    /** Whether to extract alternate rate quotes */
    private boolean extractAlternates = false;

    /** The identifier of the customer we want the quote for. */
    private String customerId;

    /** The UUID of this quote request. If not provided, it will be generated. */
    private String uuid;
    
    /** List of carrier codes to request quotes for. */
    private List<String> carrierCodes;
    
    /** The currency the quotes are requested in. */
    private String currencyCode;

    private AddressDTO origin;

    private AddressDTO destination;

    private LocalDate pickupDate;

    private List<QuoteItemDTO> items;

    private List<String> additionalServices;
    
    /** Guaranteed by time in 24h format (e.g. 14:00) */
    private String guaranteedBy;

    /** Margin to apply to the returned quotes */
    private MarginDetails marginDetails;

    @Data
    public static class MarginDetails {

        private BigDecimal marginPercentage;

        private BigDecimal minimumMarginAmount;
    }
    
    private Integer timeout;

    private ShipmentType shipmentType = ShipmentType.LTL;

}
