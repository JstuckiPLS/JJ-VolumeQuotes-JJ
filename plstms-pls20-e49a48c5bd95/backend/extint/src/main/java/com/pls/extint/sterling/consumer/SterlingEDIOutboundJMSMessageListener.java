package com.pls.extint.sterling.consumer;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.extint.service.SterlingService;

/**
 * Listens for SterlingIntegrationMessageBO objects on the external integration message queue and then delegates the
 * message to Sterling Service for processing the message based on message type.
 *
 * @author Jasmin Dhamelia
 *
 */
@Component("externalIntegrationListener")
@Profile("JMSServer")
public class SterlingEDIOutboundJMSMessageListener implements SessionAwareMessageListener<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(SterlingEDIOutboundJMSMessageListener.class);

    @Autowired
    private SterlingService service;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            if (!(message instanceof ObjectMessage)) {
                throw new Exception("The message type is required to be an instance of ObjectMessage not: "
                        + message.getClass().getName());
            }

            service.sendMessage(extractMessage((ObjectMessage) message));
        } catch (Exception e) {
            LOG.error("Sending data to Sterling service failed: " + e.getMessage(), e);
            session.rollback();
        }
    }

    private SterlingIntegrationMessageBO extractMessage(ObjectMessage om) throws Exception {
        Serializable serializableObj = om.getObject();
        if (serializableObj == null) {
            throw new Exception("The ObjectMessage payload is null.");
        }
        if (!(serializableObj instanceof SterlingIntegrationMessageBO)) {
            throw new Exception(
                    "The object that the ObjectMessage is carrying is required to be an OutboundMessage not a: "
                            + serializableObj.getClass().getName());
        }
        return (SterlingIntegrationMessageBO) serializableObj;
    }

}
