package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

import com.pls.core.shared.DisputeCost;
import com.pls.core.shared.UpdateRevenueOption;

/**
 * BO to retrieve audit shipment costs details.
 * 
 * @author Brichak Aleksandr
 */
public class AuditShipmentCostsBO {
    private Long id;
    private UpdateRevenueOption updateRevenue;
    private BigDecimal updateRevenueValue;
    private DisputeCost disputeCost;
    private Boolean requestPaperwork;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UpdateRevenueOption getUpdateRevenue() {
        return updateRevenue;
    }

    public void setUpdateRevenue(UpdateRevenueOption updateRevenue) {
        this.updateRevenue = updateRevenue;
    }

    public BigDecimal getUpdateRevenueValue() {
        return updateRevenueValue;
    }

    public void setUpdateRevenueValue(BigDecimal updateRevenueValue) {
        this.updateRevenueValue = updateRevenueValue;
    }

    public DisputeCost getDisputeCost() {
        return disputeCost;
    }

    public void setDisputeCost(DisputeCost disputeCost) {
        this.disputeCost = disputeCost;
    }

    public Boolean getRequestPaperwork() {
        return requestPaperwork;
    }

    public void setRequestPaperwork(Boolean requestPaperwork) {
        this.requestPaperwork = requestPaperwork;
    }
}
