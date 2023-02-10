package com.pls.ltlrating.service.analysis;

import java.util.List;

import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.analysis.FATariffsEntity;
import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;
import com.pls.ltlrating.shared.FreightAnalysisDetailsBO;
import com.pls.ltlrating.shared.FreightAnalysisReportStatusBO;

/**
 * Service that handle business logic for Freight Analysis.
 *
 * @author Brichak Aleksandr
 */

public interface ShipmentFreightAnalysisService {

    /**
     * Copy analysis job for processing.
     *
     * @param vo
     *            {@link FreightAnalysisDetailsBO}
     * @param tariffs
     *            {@link List<FATariffsEntity>}
     * @return {@link FAFinancialAnalysisEntity}
     * @throws Exception
     *             if entity not found or document doesn't saved
     */
    FAFinancialAnalysisEntity copyAnalysisJob(FreightAnalysisDetailsBO vo, List<FATariffsEntity> tariffs)
            throws Exception;

    /**
     * This method update freight analysis status.
     *
     * @param id
     *            {@link FAFinancialAnalysisEntity#id}
     *
     * @param status
     *            status of update
     */
    void updateAnalysisStatus(Long id, FinancialAnalysisStatus status);

    /**
     * Add new freight analysis job for processing.
     *
     * @param bo
     *            {@link FreightAnalysisDetailsBO}
     * @param tariffs
     *            {@link List<FATariffsEntity>}
     * @return {@link FAFinancialAnalysisEntity}
     * @throws Exception
     *             if entity not found or document doesn't saved
     */
    FAFinancialAnalysisEntity addNewPricing(FreightAnalysisDetailsBO bo, List<FATariffsEntity> tariffs)
            throws Exception;

    /**
     * Get analysis jobs except Delete status.
     *
     * @return {@link List<FreightAnalysisReportStatusVO>}.
     */
    List<FreightAnalysisReportStatusBO> getAnalysisJobs();

    /**
     * This method swaps priority for Analysis Jobs. Priority of Job is incremented by one if step is true
     * priority of Job reduced by one if step is false.
     *
     * @param analysisId
     *            {@link FAFinancialAnalysisEntity#id}
     * @param step
     *            flag indicating the direction of movement.
     */
    void swapAnalysisJobs(Long analysisId, Boolean step);

}
