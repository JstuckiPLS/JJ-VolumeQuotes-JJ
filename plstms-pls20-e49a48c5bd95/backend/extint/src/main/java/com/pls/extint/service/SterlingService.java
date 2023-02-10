package com.pls.extint.service;

import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.exception.ApplicationException;

/**
 * This service is for sending messages to Sterling queues.
 * 
 * @author Pavani Challa
 */
public interface SterlingService {

    /**
     * Publishes the message to the outbound queues in external JMS server based on message type.
     * @param message message to be published
     * @throws ApplicationException if conversion of IntegrationMessageBO to XML string fails or publishing to queue fails.
     */
    void sendMessage(SterlingIntegrationMessageBO message) throws ApplicationException;

    /**
     * Processes the carrier invoices consumed from the inbound carrier invoice queue.
     * 
     * @param auditId for cloning the Audit object.
     * @param xml XML Message from the Sterling queue for processing invoices from carrier.
     * @throws ApplicationException if processing the message fails.
     */
    void resubmit(Long auditId, String xml) throws ApplicationException;

}
