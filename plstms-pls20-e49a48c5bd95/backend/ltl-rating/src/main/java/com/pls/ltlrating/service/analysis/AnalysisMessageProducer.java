package com.pls.ltlrating.service.analysis;

/**
 * Interface for Analysis requests. It declares methods for enqueue Analysis messages for sending.
 *
 * @author Aleksandr Leshchenko
 */
public interface AnalysisMessageProducer {

    /**
     * Put new JMS message for starting analysis.
     */
    void startAnalysis();
}
