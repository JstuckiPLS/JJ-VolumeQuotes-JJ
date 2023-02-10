package com.pls.invoice.service.impl.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.pls.invoice.service.processing.InvoiceProcessingService;

/**
 * Message listener for processing invoice documents asynchronously.
 *
 * @author Aleksandr Leshchenko
 */
@Component("invoiceDocumentsMessageListener")
@Profile("JMSServer")
public class InvoiceDocumentsJmsMessageListener implements SessionAwareMessageListener<Message> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDocumentsJmsMessageListener.class);

    @Autowired
    private InvoiceProcessingService invoiceProcessingService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        LOGGER.info("Start async processing of invoice documents");
        try {
            ActiveMQObjectMessage activeMQObjectMessage = (ActiveMQObjectMessage) message;
            InvoiceJMSMessage invoiceMessage = (InvoiceJMSMessage) activeMQObjectMessage.getObject();
            invoiceProcessingService.processInvoiceDocuments(invoiceMessage.getFinanceBO(), invoiceMessage.getInvoiceId());
        } catch (Exception e) {
            LOGGER.error("Sending invoice documents failed. " + e.getMessage(), e);
            session.rollback();
        }
        LOGGER.info("End async processing of invoice documents");
    }
}
