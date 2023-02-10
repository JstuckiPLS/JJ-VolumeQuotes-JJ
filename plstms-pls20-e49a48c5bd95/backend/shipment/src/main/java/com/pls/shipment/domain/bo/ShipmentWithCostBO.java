package com.pls.shipment.domain.bo;

import com.pls.shipment.domain.LoadEntity;

/**
 * Information about shipment and its cost.
 * 
 * @author Gleb Zgonikov
 */
public class ShipmentWithCostBO {

    private LoadEntity shipment;

    private Number totalCost;

    /**
     * Default constructor.
     */
    public ShipmentWithCostBO() {
    }

    /**
     * Constructor.
     * 
     * @param shipment Shipment info.
     * @param totalCost Total cost.
     */
    public ShipmentWithCostBO(LoadEntity shipment, Number totalCost) {
        this.shipment = shipment;
        this.totalCost = totalCost;
    }

    public LoadEntity getShipment() {
        return shipment;
    }

    public void setShipment(LoadEntity shipment) {
        this.shipment = shipment;
    }

    public Number getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Number totalCost) {
        this.totalCost = totalCost;
    }
}
