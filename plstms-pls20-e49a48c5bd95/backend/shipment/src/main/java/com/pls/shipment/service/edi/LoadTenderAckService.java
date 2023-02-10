package com.pls.shipment.service.edi;

import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.sterling.LoadAcknowledgementJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;

/**
 * Service for processing/sending EDI 990 messages.
 * 
 * @author Pavani Challa
 *
 */
public interface LoadTenderAckService extends IntegrationService<LoadAcknowledgementJaxbBO> {

    /**
     * Sends EDI messages to carriers/customers.
     * 
     * @param load
     *            shipment data
     * @param loadTender
     *            EDI update received from customer.
     * @param accept
     *            if the EDI update was accepted by PLS PRO
     * @throws InternalJmsCommunicationException
     *             thrown when publishing to JMS queue fails.
     */
    void sendMessage(LoadEntity load, LoadMessageJaxbBO loadTender, boolean accept) throws InternalJmsCommunicationException;
}
