package com.pls.ltlrating.batch.analysis;

/**
 * Job operator for scheduling Freight Analysis jobs.
 *
 * @author Aleksandr Leshchenko
 */
public interface AnalysisJobOperator {

    /**
     * Start Freight Analysis jobs.
     *
     * @throws Exception
     *             in case of job launching failure
     */
    void startAnalysis() throws Exception;

}
