package com.pls.ltlrating.batch.analysis.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.pls.ltlrating.batch.analysis.AnalysisJobOperator;
import com.pls.ltlrating.service.analysis.impl.AnalysisJmsMessageProducer;

/**
 * Message listener for operating analysis background job.
 *
 * @author Aleksandr Leshchenko
 */
@Component("analysisMessageListener")
@Profile("JMSServer")
class AnalysisJmsMessageListener implements SessionAwareMessageListener<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(AnalysisJmsMessageListener.class);

    @Autowired
    private AnalysisJobOperator operator;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
            String operation = getStringFromMap(activeMQMapMessage.getContentMap(), AnalysisJmsMessageProducer.OPERATION);
            if (AnalysisJmsMessageProducer.START.equals(operation)) {
                LOG.info("Received Start Analysis operation over JMS");
                operator.startAnalysis();
            } else {
                LOG.warn("Received Unknown Analysis operation over JMS");
            }
        } catch (Exception e) {
            LOG.error("Processing Analysis message failed. " + e.getMessage(), e);
            session.rollback();
        }
    }

    private String getStringFromMap(Map<String, Object> properties, String key) {
        return StringUtils.trimToNull(ObjectUtils.defaultIfNull((UTF8Buffer) properties.get(key), new UTF8Buffer("")).toString());
    }
}