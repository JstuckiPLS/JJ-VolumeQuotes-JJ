package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;

/**
 * Freight spend analysis report.
 * 
 * @author Dmitriy Nefedchenko
 */
public class FreightSpendAnalysisReportBO {
    private Long orgId;
    private BigDecimal classCode;
    private Long loadCount;
    private BigDecimal summaryCost;
    private BigDecimal avarageCost;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public BigDecimal getClassCode() {
        return classCode;
    }

    public void setClassCode(BigDecimal classCode) {
        this.classCode = classCode;
    }

    public Long getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(Long loadCount) {
        this.loadCount = loadCount;
    }

    public BigDecimal getSummaryCost() {
        return summaryCost;
    }

    public void setSummaryCost(BigDecimal summaryCost) {
        this.summaryCost = summaryCost;
    }

    public BigDecimal getAvarageCost() {
        return avarageCost;
    }

    public void setAvarageCost(BigDecimal avarageCost) {
        this.avarageCost = avarageCost;
    }
}
