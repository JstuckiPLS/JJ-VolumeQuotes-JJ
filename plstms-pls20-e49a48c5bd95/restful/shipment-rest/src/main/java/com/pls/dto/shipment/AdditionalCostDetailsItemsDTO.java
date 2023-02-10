package com.pls.dto.shipment;

import java.util.List;

import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.shipment.domain.LoadCostDetailsEntity;

/**
 * DTO for CarrierInvoiceDetailsItems.
 * 
 * @author Brichak Aleksandr
 *
 */
public class AdditionalCostDetailsItemsDTO {

    /**
     * Constructor for CarrierInvoiceDetailsItemsDTO.
     * 
     * @param costDetailItems
     *            {@link CostDetailItemBO}
     * @param loadVersion
     *            version of load
     * @param activeCostDetailId
     *            id of {@link LoadCostDetailsEntity}
     */
    public AdditionalCostDetailsItemsDTO(List<CostDetailItemBO> costDetailItems, Integer loadVersion,
            Long activeCostDetailId) {
        this.costDetailItems = costDetailItems;
        this.loadVersion = loadVersion;
        this.activeCostDetailId = activeCostDetailId;
    }

    /**
     * Constructor for CarrierInvoiceDetailsItemsDTO.
     * 
     * @param costDetailId
     *            id of {@link LoadCostDetailsEntity#getId()}
     * @param loadVersion
     *            version of load
     */
    public AdditionalCostDetailsItemsDTO(Integer loadVersion, Long costDetailId) {
        this.loadVersion = loadVersion;
        this.activeCostDetailId = costDetailId;
    }

    List<CostDetailItemBO> costDetailItems;

    private Integer loadVersion;

    private Long activeCostDetailId;

    public List<CostDetailItemBO> getCostDetailItems() {
        return costDetailItems;
    }

    public void setCostDetailItems(List<CostDetailItemBO> costDetailItems) {
        this.costDetailItems = costDetailItems;
    }

    public Integer getLoadVersion() {
        return loadVersion;
    }

    public void setLoadVersion(Integer loadVersion) {
        this.loadVersion = loadVersion;
    }

    public Long getActiveCostDetailId() {
        return activeCostDetailId;
    }

    public void setActiveCostDetailId(Long activeCostDetailId) {
        this.activeCostDetailId = activeCostDetailId;
    }
}
