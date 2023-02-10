package com.pls.shipment.service.impl.edi.jms.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.pls.shipment.domain.edi.EDIMessage;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * EDI Dead Letter Queue message listener.
 *
 * @author Aleksandr Leshchenko
 */
@Component("dlqMessageListener")
@Profile("JMSServer")
public class EDIDLQJmsMessageListener implements SessionAwareMessageListener<Message> {

    @Autowired
    private EDIEmailSender ediEmailSender;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ActiveMQObjectMessage activeMQObjectMessage = (ActiveMQObjectMessage) message;
        EDIMessage ediMessage = (EDIMessage) activeMQObjectMessage.getObject();
        if (ediMessage.getTransactionSet() == EDITransactionSet._204) {
            ediEmailSender.loadTenderFailed(ediMessage.getCarrierId(), ediMessage.getEntityIds());
        }
    }
}