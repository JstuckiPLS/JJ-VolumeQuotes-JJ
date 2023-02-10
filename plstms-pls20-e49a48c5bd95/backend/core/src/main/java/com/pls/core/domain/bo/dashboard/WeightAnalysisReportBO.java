package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;

/**
 * Weight analysis report description.
 * 
 * @author Dmitriy Nefedchenko
 */
public class WeightAnalysisReportBO {
    private Long loadId;
    private BigDecimal weight;

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getLoadId() {
        return this.loadId;
    }
}
