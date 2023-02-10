package com.pls.invoice.service.impl.jms;

import javax.jms.JMSException;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

import com.pls.invoice.service.jms.InvoiceMessageProducer;

/**
 * Base class for Invoice senders.
 *
 * @author Aleksandr Leshchenko
 */
@Repository
public class InvoiceJmsMessageProducer implements InvoiceMessageProducer {

    private static final long DELAY = 2 * 1000; //Delay the message for 2 seconds to ensure the session has already been flushed

    @Autowired
    @Qualifier("invoiceTemplate")
    protected JmsTemplate invoiceJmsTemplate;

    @Autowired
    @Qualifier("invoiceDocumentsTemplate")
    protected JmsTemplate invoiceDocumentsJmsTemplate;

    @Override
    public void sendMessage(InvoiceJMSMessage invoiceMessage) throws JMSException {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setObject(invoiceMessage);
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, DELAY);
        invoiceJmsTemplate.convertAndSend(message);
    }

    @Override
    public void sendDocumentsMessage(InvoiceJMSMessage invoiceMessage) throws JMSException {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setObject(invoiceMessage);
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, DELAY);
        invoiceDocumentsJmsTemplate.convertAndSend(message);
    }
}
