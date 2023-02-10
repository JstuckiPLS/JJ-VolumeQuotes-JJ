package com.pls.email.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.pls.email.EmailJmsMessageConverter;
import com.pls.email.EmailMessage;

/**
 * Message listener for sending email asynchronously.
 * 
 * @author Aleksandr Leshchenko
 */
@Component("emailMessageListener")
@Profile("JMSServer")
class EmailJmsMessageListener implements SessionAwareMessageListener<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(EmailJmsMessageListener.class);

    @Autowired
    private EmailSender emailSender;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            Thread.sleep(2000);
            ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
            EmailMessage emailMessage = EmailJmsMessageConverter.decodeFromJmsMap(activeMQMapMessage.getContentMap());
            emailSender.sendMessage(emailMessage);
            LOG.info("Email has been sent successfully");
        } catch (Exception e) {
            LOG.error("Sending email failed. " + e.getMessage(), e);
            session.rollback();
        }
    }
}