package com.pls.dto.shipment;

import java.math.BigDecimal;
import java.util.List;

import com.pls.dto.address.AddressBookEntryDTO;

/**
 * DTO object for tooltip on shipment grids.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class ShipmentGridTooltipDTO {
    private String carrier;

    private List<String> products;

    private String logoPath;

    private String customerName;

    private AddressBookEntryDTO origin;

    private AddressBookEntryDTO destination;

    private BigDecimal totalCost;

    private BigDecimal totalRevenue;

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public AddressBookEntryDTO getOrigin() {
        return origin;
    }

    public void setOrigin(AddressBookEntryDTO origin) {
        this.origin = origin;
    }

    public AddressBookEntryDTO getDestination() {
        return destination;
    }

    public void setDestination(AddressBookEntryDTO destination) {
        this.destination = destination;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
