package com.pls.shipment.domain.bo;

public class AuditReasonReportBO {

    private Long loadId;
    private String description;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
