package com.pls.dtobuilder.proposal;

import java.util.Set;

import com.pls.core.shared.StatusYesNo;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.shared.FreightAnalysisReportStatusBO;

/**
 * Build {@link FreightAnalysisReportStatusBO} from {@link FAFinancialAnalysisEntity}.
 * 
 * @author Brichak Aleksandr
 */
public class FAReportStatusDTOBuilder
        extends AbstractDTOBuilder<FAFinancialAnalysisEntity, FreightAnalysisReportStatusBO> {

    @Override
    public FreightAnalysisReportStatusBO buildDTO(FAFinancialAnalysisEntity bo) {

        FreightAnalysisReportStatusBO result = new FreightAnalysisReportStatusBO();
        result.setAnalysisId(bo.getId());
        result.setCompletedFileName(bo.getOutputFileName());
        result.setUploadDate(bo.getInputFile().getModification().getCreatedDate());
        result.setUploadedFileName(bo.getInputFileName());
        result.setUploadedDocId(bo.getInputFile().getId());
        result.setSeqNumber(bo.getSeq());
        result.setStatus(bo.getStatus());
        setCompletedTariffCounts(result, bo.getInputDetails());
        return result;
    }

    private void setCompletedTariffCounts(FreightAnalysisReportStatusBO result,
            Set<FAInputDetailsEntity> inputDetails) {
        if (inputDetails != null) {
            Long completed = inputDetails.stream().filter((d) -> d.getCompleted() == StatusYesNo.YES).count();
            result.setCompletedTariffCounts(completed + " of " + inputDetails.size());
        }
    }

    @Override
    public FAFinancialAnalysisEntity buildEntity(FreightAnalysisReportStatusBO dto) {
        throw new UnsupportedOperationException("Operation is not supported");
    }
}
