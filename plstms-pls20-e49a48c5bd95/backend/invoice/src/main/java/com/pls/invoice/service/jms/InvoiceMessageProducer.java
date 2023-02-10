package com.pls.invoice.service.jms;

import javax.jms.JMSException;

import com.pls.invoice.service.impl.jms.InvoiceJMSMessage;

/**
 * JMS producer for invoices.
 * 
 * @author Aleksandr Leshchenko
 */
public interface InvoiceMessageProducer {

    /**
     * Send message to Invoice queue to process invoices asynchronously.
     * 
     * @param message
     *            message to send
     * @throws JMSException
     *             thrown if the JMS provider fails to set the object due to
     *             some internal error
     */
    void sendMessage(InvoiceJMSMessage message) throws JMSException;

    /**
     * Send message to Invoice Documents queue to send documents to shared drive asynchronously.
     * 
     * @param message
     *            message to send
     * @throws JMSException
     *             thrown if the JMS provider fails to set the object due to some internal error
     */
    void sendDocumentsMessage(InvoiceJMSMessage message) throws JMSException;

}
