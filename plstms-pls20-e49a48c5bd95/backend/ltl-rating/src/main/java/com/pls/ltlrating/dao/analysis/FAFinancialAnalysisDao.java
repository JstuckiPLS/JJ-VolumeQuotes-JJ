package com.pls.ltlrating.dao.analysis;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.batch.analysis.model.AnalysisItem;
import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;
import com.pls.ltlrating.shared.FreightAnalysisReportStatusBO;

/**
 * DAO object for {@link FAFinancialAnalysisEntity}.
 *
 * @author Brichak Aleksandr
 *
 */
public interface FAFinancialAnalysisDao extends AbstractDao<FAFinancialAnalysisEntity, Long> {

    /**
     * Update status for entity by specified ID.
     *
     * @param id
     *            {@link FAFinancialAnalysisEntity#getId()}
     * @param status
     *            {@link FAFinancialAnalysisEntity#getStatus()}
     */
    void updateAnalysisStatus(Long id, FinancialAnalysisStatus status);

    /**
     * Get status for entity by specified ID.
     * 
     * @param id
     *            {@link FAFinancialAnalysisEntity#getId()}
     * @return {@link FAFinancialAnalysisEntity#getStatus()}
     */
    FinancialAnalysisStatus getAnalysisStatus(Long id);

    /**
     * Get list of all analysis items that should be processed. The result is ordered based on the specified
     * priority.
     * 
     * @param maxCount
     *            maximum number of items which need to be returned.
     *
     * @return list of all analysis items that should be processed.
     */
    List<AnalysisItem> getNextAnalysisForProcessing(int maxCount);

    /**
     * Marks input detail row as completed if it has appropriate output details for all tariffs.
     *
     * @param detailId
     *            input detail ID (Row ID)
     * @return <code>true</code> if row was marked as completed.
     */
    boolean markInputDetailAsCompleted(Long detailId);

    /**
     * Marks whole analysis as completed if it has all input details completed.
     *
     * @param analysisId
     *            Analysis ID
     * @return <code>true</code> if analysis was marked as completed.
     */
    boolean markAnalysisAsCompleted(Long analysisId);

    /**
     * Get all analysis items for current person.
     *
     * @return {@link List<FreightAnalysisReportStatusBO>}.
     */
    List<FreightAnalysisReportStatusBO> getAnalysisJobs();

    /**
     * Get next analysis item depending on value of step.
     *
     * @param seqNumber
     *            sequence number
     * @param isStepUP
     *            flag indicating the direction of movement
     * @return {@link List<FAFinancialAnalysisEntity>}.
     */
    List<FAFinancialAnalysisEntity> getNextFreightAnalysisBySeqNumber(Integer seqNumber, Boolean isStepUP);

    /**
     * Get next sequence number.
     *
     * @return sequence number.
     */
    Integer getNextSeqNumber();

    /**
     * Save document reference for specified freight analysis job.
     * 
     * @param analysisId
     *            {@link FAFinancialAnalysisEntity#getId()}
     * @param documentId
     *            saved document ID
     */
    void updateAnalysisWithResultFile(Long analysisId, Long documentId);

    /**
     * Get {@link FAFinancialAnalysisEntity} by ID with all dependencies fetched.
     * 
     * @param analysisId
     *            {@link FAFinancialAnalysisEntity#getId()}
     * @return {@link FAFinancialAnalysisEntity}
     */
    FAFinancialAnalysisEntity getWithDependencies(Long analysisId);

    /**
     * Delete output details for rows which were not marked as completed.
     */
    void deleteIncorrectOutputDetails();
}
