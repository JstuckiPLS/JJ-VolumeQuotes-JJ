package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

/**
 * This class is used for get "Quoted" fields.
 * 
 * @author Brichak Aleksandr
 * 
 */
public class QuotedBO {

    /**
     * Constructor.
     */
    public QuotedBO() {
    }

    /**
     * Constructor.
     * 
     * @param totalRevenue
     *            total revenue amount.
     * @param totalCost
     *            total cost amount.
     */
    public QuotedBO(BigDecimal totalRevenue, BigDecimal totalCost) {
        this.totalRevenue = totalRevenue;
        this.totalCost = totalCost;
    }

    private BigDecimal totalRevenue;

    private BigDecimal totalCost;

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

}
