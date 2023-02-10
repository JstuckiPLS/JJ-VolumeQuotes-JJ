package com.pls.shipment.domain.bo;

import java.util.Set;

import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;

/**
 * BO for costDetailItems and loadVersion.
 * 
 * @author Brichak Aleksandr
 *
 */
public class CostDetailTransfeBO {

    /**
     * Constructor for CostDetailTransfeBO.
     * 
     * @param loadVersion
     *            version of Load
     * @param activeCostDetailId
     *            id of {@link LoadCostDetailsEntity}
     * @param costDetailItems
     *            {@link CostDetailItemEntity}
     */
    public CostDetailTransfeBO(Integer loadVersion, Long activeCostDetailId,
            Set<CostDetailItemEntity> costDetailItems) {
        this.costDetailItems = costDetailItems;
        this.loadVersion = loadVersion;
        this.activeCostDetailId = activeCostDetailId;
    }

    Set<CostDetailItemEntity> costDetailItems;

    private Integer loadVersion;

    private Long activeCostDetailId;

    public Integer getLoadVersion() {
        return loadVersion;
    }

    public void setLoadVersion(Integer loadVersion) {
        this.loadVersion = loadVersion;
    }

    public Set<CostDetailItemEntity> getCostDetailItems() {
        return costDetailItems;
    }

    public void setCostDetailItems(Set<CostDetailItemEntity> costDetailItems) {
        this.costDetailItems = costDetailItems;
    }

    public Long getActiveCostDetailId() {
        return activeCostDetailId;
    }

    public void setActiveCostDetailId(Long activeCostDetailId) {
        this.activeCostDetailId = activeCostDetailId;
    }
}
