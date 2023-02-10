package com.pls.shipment.service.impl.edi.sterling.jms.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.exception.InternalJmsCommunicationException;

/**
 * Message producer for incoming EDI messages from Sterling.
 *
 * @author Pavani Challa
 */
@Repository("ediIncomingMessageProducer")
public class SterlingEDIIncomingJMSMessageProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SterlingEDIIncomingJMSMessageProducer.class);

    @Autowired
    @Qualifier("shipmentIntegrationTemplate")
    protected JmsTemplate jmsTemplate;

    /**
     * Sends a message to shipment queue.
     *
     * @param message
     *            message to be published to shipment message queue
     * @throws InternalJmsCommunicationException
     *             if there is an error sending the message.
     */
    public void publish(SterlingIntegrationMessageBO message) throws InternalJmsCommunicationException {
        Preconditions.checkNotNull(message, "The message argument is null.");
        LOG.debug("##### Enqueing message: {}", message);
        jmsTemplate.convertAndSend(message);
    }

}
