package com.pls.invoice.service.impl.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.pls.invoice.service.InvoiceErrorService;

/**
 * Invoice Documents Dead Letter Queue message listener.
 *
 * @author Aleksandr Leshchenko
 */
@Component("dlqInvoiceDocumentsMessageListener")
@Profile("JMSServer")
public class InvoiceDocumentsDLQJmsMessageListener implements SessionAwareMessageListener<Message> {

    @Autowired
    private InvoiceErrorService invoiceErrorService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ActiveMQObjectMessage activeMQObjectMessage = (ActiveMQObjectMessage) message;
        InvoiceJMSMessage invoiceMessage = (InvoiceJMSMessage) activeMQObjectMessage.getObject();
        String msg = "Error sending documents to finance. Maximum redelivery attempts exceeded.";
        invoiceErrorService.saveInvoiceError(invoiceMessage.getInvoiceId(), msg, null, true, true, true, false, invoiceMessage.getUserId());
    }
}