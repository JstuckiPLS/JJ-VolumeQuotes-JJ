package com.pls.invoice.domain.bo;

/**
 * BO for load id and adjustment id.
 * 
 * @author Aleksandr Leshchenko
 */
public class LoadAdjustmentIdBO {
    private Long loadId;
    private Long adjustmentId;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }
}
