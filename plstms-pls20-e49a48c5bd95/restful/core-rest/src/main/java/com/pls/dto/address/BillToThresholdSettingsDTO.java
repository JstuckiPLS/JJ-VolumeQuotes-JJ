package com.pls.dto.address;

import java.math.BigDecimal;

/**
 * DTO for BillToThresholdSettingsEntity.
 * 
 * @author Brichak Aleksandr
 *
 */
public class BillToThresholdSettingsDTO {

    private BigDecimal costDifference;

    private BigDecimal totalRevenue;

    private BigDecimal margin;

    private Long id;

    public BigDecimal getCostDifference() {
        return costDifference;
    }

    public void setCostDifference(BigDecimal costDifference) {
        this.costDifference = costDifference;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
