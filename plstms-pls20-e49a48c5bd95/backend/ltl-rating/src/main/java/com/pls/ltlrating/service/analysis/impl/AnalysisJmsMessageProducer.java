package com.pls.ltlrating.service.analysis.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.pls.ltlrating.service.analysis.AnalysisMessageProducer;

/**
 * Base class for email senders.<br>
 * Using {@link Async} annotations because Active MQ supports only JMS 1.0 which doesn't provide functionality
 * to delay JMS message.
 *
 * @author Aleksandr Leshchenko
 */
@Repository
public class AnalysisJmsMessageProducer implements AnalysisMessageProducer {
    public static final String OPERATION = "OPERATION";
    public static final String START = "START";

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisJmsMessageProducer.class);

    @Autowired
    @Qualifier("analysisTemplate")
    protected JmsTemplate jmsTemplate;

    @Override
    public void startAnalysis() {
        LOG.info("Sending Start Analysis operation to JMS");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(OPERATION, START);
        jmsTemplate.convertAndSend(map);
    }
}
