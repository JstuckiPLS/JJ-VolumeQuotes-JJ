package com.pls.ltlrating.batch.analysis.model;

/**
 * Item for Freight Analysis processing.
 *
 * @author Aleksandr Leshchenko
 */
public class AnalysisItem {
    private Long rowId;
    private Long analysisId;
    private Long tariffId;
    private Long customerId;
    private Integer analysisSequenceNumber;
    private Long rowSequenceNumber;

    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getAnalysisSequenceNumber() {
        return analysisSequenceNumber;
    }

    public void setAnalysisSequenceNumber(Integer analysisSequenceNumber) {
        this.analysisSequenceNumber = analysisSequenceNumber;
    }

    public Long getRowSequenceNumber() {
        return rowSequenceNumber;
    }

    public void setRowSequenceNumber(Long rowSequenceNumber) {
        this.rowSequenceNumber = rowSequenceNumber;
    }
}
