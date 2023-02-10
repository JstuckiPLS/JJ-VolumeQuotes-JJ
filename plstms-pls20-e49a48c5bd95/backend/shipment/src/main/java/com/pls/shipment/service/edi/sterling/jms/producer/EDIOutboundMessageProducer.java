package com.pls.shipment.service.edi.sterling.jms.producer;

import javax.jms.JMSException;

import com.pls.shipment.domain.edi.EDIMessage;

/**
 * Interface for EDI produce. It declares method for enqueue EDI messages for sending.
 *
 * @author Mikhail Boldinov, 27/02/14
 */
public interface EDIOutboundMessageProducer {

    /**
     * Adds new JMS message into EDI message queue.
     *
     * @param ediMessage edi message to send
     * @throws JMSException thrown if the JMS provider fails to set the object due to some internal error
     */
    void sendMessage(EDIMessage ediMessage) throws JMSException;
}
