package com.pls.ltlrating.shared;

import java.util.Date;

import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;

/**
 * DTO Object to add new Freight Analysis data.
 *
 * @author Brichak Aleksandr
 *
 */
public class FreightAnalysisReportStatusBO {

    private Long analysisId;

    private String uploadedFileName;

    private Long uploadedDocId;

    private Date uploadDate;

    private String completedFileName;

    private FinancialAnalysisStatus status;

    private String completedTariffCounts;

    private Integer seqNumber;

    private Long completedDocId;

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public Long getUploadedDocId() {
        return uploadedDocId;
    }

    public void setUploadedDocId(Long uploadedDocId) {
        this.uploadedDocId = uploadedDocId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getCompletedFileName() {
        return completedFileName;
    }

    public void setCompletedFileName(String completedFileName) {
        this.completedFileName = completedFileName;
    }

    public FinancialAnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(FinancialAnalysisStatus financialAnalysisStatus) {
        this.status = financialAnalysisStatus;
    }

    public Integer getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(Integer seqNumber) {
        this.seqNumber = seqNumber;
    }

    public String getCompletedTariffCounts() {
        return completedTariffCounts;
    }

    public void setCompletedTariffCounts(String completedTariffCounts) {
        this.completedTariffCounts = completedTariffCounts;
    }

    public Long getCompletedDocId() {
        return completedDocId;
    }

    public void setCompletedDocId(Long completedDocId) {
        this.completedDocId = completedDocId;
    }

}
