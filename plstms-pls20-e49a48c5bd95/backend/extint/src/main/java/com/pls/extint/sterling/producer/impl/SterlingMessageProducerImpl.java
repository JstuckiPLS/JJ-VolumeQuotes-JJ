package com.pls.extint.sterling.producer.impl;

import javax.jms.JMSException;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.pls.core.exception.ExternalJmsCommunicationException;
import com.pls.extint.sterling.producer.SterlingMessageProducer;

/**
 * JMS message producer for publishing the messages to the queues in external JMS server.
 *
 * @author Jasmin Dhamelia
 *
 */
@Component
public class SterlingMessageProducerImpl implements SterlingMessageProducer {

    private static final long DELAY = 5 * 1000; // Delay the message for 5 seconds to ensure the session has already been flushed

    @Autowired
    @Qualifier("sterlingMessageJMSTemplate")
    protected JmsTemplate jmsTemplate;

    @Value("${sterling.finance.outboundQueue}")
    private String financeOutboundQueue;

    @Override
    public void publishEdiMessage(String xml, String destination, Integer priority) throws ExternalJmsCommunicationException {
        publishMessage(xml, destination, priority);
    }

    @Override
    public void publishFinanceMessage(String xml) throws ExternalJmsCommunicationException {
        publishMessage(xml, financeOutboundQueue, null);
    }

    private void publishMessage(String xml, String destination, Integer priority) throws ExternalJmsCommunicationException {
        try {
            ActiveMQObjectMessage message = new ActiveMQObjectMessage();
            message.setObject(xml);
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, DELAY);
            if (priority != null) {
                message.setJMSPriority(priority);
            }
            jmsTemplate.convertAndSend(destination, message);
        } catch (JMSException ex) {
            throw new ExternalJmsCommunicationException("Exception occurred while publishing to external JMS server", ex);
        }
    }
}
