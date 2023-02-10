package com.pls.ltlrating.integration.ltllifecycle.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pls.ltlrating.integration.ltllifecycle.dto.FreightClass;
import com.pls.ltlrating.integration.ltllifecycle.dto.LtlServiceType;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;

import lombok.Data;

@Data
public class QuoteResultDTO {

    private String uuid;

    private String quoteProvider;
    
    /** The quote number provided by the quote provider. */
    private String quoteNumber;

    private Integer transitTime;
    private LocalDateTime deliveryDate;
    private String serviceLevelCode;
    private String serviceLevelDescription;
    private String notes;

    private CarrierDTO carrier;

    /** Miles from origin to destination. */
    private BigDecimal mileage;

    private LtlServiceType serviceType;

    /** The currency this quote's amounts are presented. */
    private String currencyCode;
    
    /** The total cost of shipment (including accessorials and fuel, and discount applied) */
    private BigDecimal totalCost = BigDecimal.ZERO;

    private List<ItemChargeDTO> itemCharges = new ArrayList<>();
    private BigDecimal minimumCharge;
    private BigDecimal grossFreightCharge;
    /** Initial cost (item+minimum+grossFreight) */
    private BigDecimal initialCost;

    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal discountRate;
    private BigDecimal fuelSurcharge = BigDecimal.ZERO;
    private BigDecimal fuelSurchargeRate;

    private BigDecimal accessorialTotal = BigDecimal.ZERO;

    private Set<AccessorialChargeDTO> accessorialCharges = new HashSet<>();

    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal initialRevenue = BigDecimal.ZERO;
    private BigDecimal revenueDiscount = BigDecimal.ZERO;
    private BigDecimal fuelSurchargeRevenue = BigDecimal.ZERO;
    private BigDecimal accessorialTotalRevenue = BigDecimal.ZERO;

    @Data
    public static class CarrierDTO {
        private String code;
        private String name;
    }

    @Data
    public static class AccessorialChargeDTO {
        private String accessorialCode;
        private String name;
        private BigDecimal cost;

        private BigDecimal revenue;
    }

    @Data
    public static class ItemChargeDTO {
        private BigDecimal cost;
        private BigDecimal rate;

        private FreightClass productClass;
        private BigDecimal weight;
        private Integer quantity;
    }
    
    private String customerId;
    private ShipmentType shipmentType;

}
