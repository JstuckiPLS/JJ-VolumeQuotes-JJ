package com.pls.shipment.service.impl.edi.jms.producer;

import javax.jms.JMSException;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

import com.pls.shipment.domain.edi.EDIMessage;
import com.pls.shipment.service.edi.sterling.jms.producer.EDIOutboundMessageProducer;

/**
 * Base class for EDI senders.
 *
 * @author Mikhail Boldinov, 27/02/14
 */
@Repository
public class EDIJmsMessageProducer implements EDIOutboundMessageProducer {

    private static final long DELAY = 5 * 1000; //Delay the message for 5 seconds to ensure the session has already been flushed

    @Autowired
    @Qualifier("ediTemplate")
    protected JmsTemplate jmsTemplate;

    @Override
    public void sendMessage(EDIMessage ediMessage) throws JMSException {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setObject(ediMessage);
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, DELAY);
        jmsTemplate.convertAndSend(message);
    }
}
