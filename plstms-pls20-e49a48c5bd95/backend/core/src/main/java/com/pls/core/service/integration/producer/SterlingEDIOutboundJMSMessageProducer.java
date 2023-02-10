package com.pls.core.service.integration.producer;

import javax.jms.JMSException;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;

/**
 * This is the JMS message producer that will convert the OutboundMessage to an ObjectMessage and send it to
 * queue.
 *
 * @author Jasmin Dhamelia
 * @author Aleksandr Leshchenko
 */
@Component
public class SterlingEDIOutboundJMSMessageProducer {

    private static final long DELAY = 5 * 1000; //Delay the message for 5 seconds to ensure the session has already been flushed

    @Autowired
    @Qualifier("externalIntegrationTemplate")
    protected JmsTemplate jmsTemplate;


    /**
     * Sends an sterling message to a JMS queue.
     * 
     * @param sterlingMessage An instance of SterlingIntegrationMessageBO.
     * @throws JMSException thrown if the JMS provider fails to set the object due to some internal error
     */
    public void publishMessage(SterlingIntegrationMessageBO sterlingMessage) throws JMSException {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setObject(sterlingMessage);
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, DELAY);
        jmsTemplate.convertAndSend(message);
    }

}
