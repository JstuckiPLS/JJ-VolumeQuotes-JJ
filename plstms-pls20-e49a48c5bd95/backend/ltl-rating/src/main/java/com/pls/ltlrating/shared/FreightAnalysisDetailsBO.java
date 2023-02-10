package com.pls.ltlrating.shared;

import java.util.List;

/**
 * DTO Object to add new Freight Analysis data.
 *
 * @author Brichak Aleksandr
 *
 */
public class FreightAnalysisDetailsBO {

    private List<FreightAnalysisTariffBO> tariffs;

    private Long uploadedDocId;

    private Long analysisId;

    private String uploadedFileName;

    private String completedFileName;

    private Boolean blockIndirectServiceType;

    public List<FreightAnalysisTariffBO> getTariffs() {
        return tariffs;
    }

    public void setTariffs(List<FreightAnalysisTariffBO> tariffs) {
        this.tariffs = tariffs;
    }

    public Long getUploadedDocId() {
        return uploadedDocId;
    }

    public void setUploadedDocId(Long uploadedDocId) {
        this.uploadedDocId = uploadedDocId;
    }

    public String getCompletedFileName() {
        return completedFileName;
    }

    public void setCompletedFileName(String completedFileName) {
        this.completedFileName = completedFileName;
    }

    public Boolean getBlockIndirectServiceType() {
        return blockIndirectServiceType;
    }

    public void setBlockIndirectServiceType(Boolean blockIndirectServiceType) {
        this.blockIndirectServiceType = blockIndirectServiceType;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }

}
