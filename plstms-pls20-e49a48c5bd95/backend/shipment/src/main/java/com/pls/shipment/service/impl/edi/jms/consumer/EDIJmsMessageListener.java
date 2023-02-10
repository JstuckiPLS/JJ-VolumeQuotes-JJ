package com.pls.shipment.service.impl.edi.jms.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.pls.shipment.domain.edi.EDIMessage;
import com.pls.shipment.service.edi.EDIWorker;

/**
 * Message listener for sending EDI asynchronously.
 *
 * @author Mikhail Boldinov, 27/02/14
 */
@Component("ediMessageListener")
@Profile("JMSServer")
public class EDIJmsMessageListener implements SessionAwareMessageListener<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EDIJmsMessageListener.class);

    @Autowired
    private EDIWorker ediWorker;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        EDIMessage ediMessage = null;
        try {
            ActiveMQObjectMessage activeMQObjectMessage = (ActiveMQObjectMessage) message;

            ediMessage = (EDIMessage) activeMQObjectMessage.getObject();
            ediWorker.writeEDI(ediMessage.getCarrierId(), ediMessage.getEntityIds(), ediMessage.getTransactionSet(),
                    ediMessage.getParams());
        } catch (Exception e) {
            LOGGER.error("Sending EDI failed for " + ToStringBuilder.reflectionToString(ediMessage) + ". " + e.getMessage(), e);
            session.rollback();
        }
    }
}
