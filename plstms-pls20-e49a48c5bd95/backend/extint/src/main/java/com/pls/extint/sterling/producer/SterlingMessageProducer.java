package com.pls.extint.sterling.producer;

import com.pls.core.exception.ExternalJmsCommunicationException;

/**
 * Interface for publishing message to JMS queues in external JMS server. Multiple queues are created in JMS server for different message types. Each
 * method in this interface publishes messages to different queue.
 * 
 * @author Jasmin Dhamelia 3/23/2015
 *
 */
public interface SterlingMessageProducer {

    /**
     * Sends all EDI messages (EDI 204) to the EDI outbound queue for carrier/customer.
     * 
     * @param xml
     *            EDI message to be published to queue
     * @param destination
     *            destination field is the queue to which message will be published.
     * @param priority
     *            priority field will the message priority.
     * @throws ExternalJmsCommunicationException
     *             thrown if the JMS provider fails to set the object due to some internal error
     */
    void publishEdiMessage(String xml, String destination, Integer priority) throws ExternalJmsCommunicationException;

    /**
     * Sends message to Finance outbound queue for finance integration.
     * 
     * @param xml
     *            finance message to be published to queue
     * @throws ExternalJmsCommunicationException
     *             thrown if the JMS provider fails to set the object due to some internal error
     */
    void publishFinanceMessage(String xml) throws ExternalJmsCommunicationException;
}
