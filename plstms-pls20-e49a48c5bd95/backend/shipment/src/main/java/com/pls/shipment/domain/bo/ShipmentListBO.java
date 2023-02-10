package com.pls.shipment.domain.bo;

import java.util.List;

import com.pls.shipment.domain.LoadEntity;

/**
 * Shipment BO.
 *
 * @author Gleb Zgonikov
 */
public class ShipmentListBO {

    private int totalCount;

    private List<LoadEntity> shipments;

    /**
     * Returns total count.
     * @return total count
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Sets total count variable.
     * @param totalCount
     *        total count
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Returns shipments list.
     * @return shipments list
     */
    public List<LoadEntity> getShipments() {
        return shipments;
    }

    /**
     * Sets shipments list.
     * @param shipments
     *        shipments list
     */
    public void setShipments(List<LoadEntity> shipments) {
        this.shipments = shipments;
    }
}
