package com.pls.core.domain.bo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * BO for billing audit reasons information, changing/editing one or multiple records at a time.
 * 
 * @author Sahil Thakkar
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditRecordsBO implements Serializable {

    private static final long serialVersionUID = 4226788837937884753L;

    private Long adjustmentId;

    private Long loadId;

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }
}