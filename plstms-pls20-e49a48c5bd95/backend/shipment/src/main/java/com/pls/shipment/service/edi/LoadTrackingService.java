package com.pls.shipment.service.edi;

import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.shipment.domain.LoadEntity;


/**
 * Service for processing/sending EDI 214 messages.
 *
 * @author Yasaman Palumbo
 *
 */
public interface LoadTrackingService {
    /**
     * Sends EDI messages to carriers/customers.
     *
     * @param load shipment data
     * @throws InternalJmsCommunicationException
     *             thrown when publishing to JMS queue fails.
     */
    void sendMessage(LoadEntity load) throws InternalJmsCommunicationException;
}
