package com.pls.dto.adjustment;

import java.util.List;

import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.shipment.domain.bo.AdjustmentBO;
import com.pls.shipment.domain.bo.AdjustmentLoadInfoBO;

/**
 * DTO for adjustments details.
 * 
 * @author Aleksandr Leshchenko
 */
public class AdjustmentDTO {
    private List<AdjustmentBO> costItems;
    private AdjustmentLoadInfoBO loadInfo;
    private List<ShipmentMaterialDTO> products;

    public List<AdjustmentBO> getCostItems() {
        return costItems;
    }

    public void setCostItems(List<AdjustmentBO> costItems) {
        this.costItems = costItems;
    }

    public AdjustmentLoadInfoBO getLoadInfo() {
        return loadInfo;
    }

    public void setLoadInfo(AdjustmentLoadInfoBO loadInfo) {
        this.loadInfo = loadInfo;
    }

    public List<ShipmentMaterialDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ShipmentMaterialDTO> products) {
        this.products = products;
    }
}
