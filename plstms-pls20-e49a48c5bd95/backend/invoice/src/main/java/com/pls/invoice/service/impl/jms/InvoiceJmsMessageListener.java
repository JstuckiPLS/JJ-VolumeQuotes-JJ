package com.pls.invoice.service.impl.jms;

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

import com.pls.invoice.service.processing.InvoiceProcessingService;

/**
 * Message listener for processing invoices asynchronously.
 *
 * @author Aleksandr Leshchenko
 */
@Component("invoiceMessageListener")
@Profile("JMSServer")
public class InvoiceJmsMessageListener implements SessionAwareMessageListener<Message> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceJmsMessageListener.class);

    @Autowired
    private InvoiceProcessingService invoiceProcessingService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        LOGGER.info("Start async processing of invoices");
        try {
            ActiveMQObjectMessage activeMQObjectMessage = (ActiveMQObjectMessage) message;
            InvoiceJMSMessage invoiceMessage = (InvoiceJMSMessage) activeMQObjectMessage.getObject();

            LOGGER.info("Sending invoice ", ToStringBuilder.reflectionToString(invoiceMessage));

            invoiceProcessingService.processInvoices(
                    invoiceMessage.getFinanceBO(), invoiceMessage.getInvoiceId(), invoiceMessage.getUserId());
        } catch (Exception e) {
            LOGGER.error("Sending Invoices failed. " + e.getMessage(), e);
            session.rollback();
        }
        LOGGER.info("End async processing of invoices");
    }
}
