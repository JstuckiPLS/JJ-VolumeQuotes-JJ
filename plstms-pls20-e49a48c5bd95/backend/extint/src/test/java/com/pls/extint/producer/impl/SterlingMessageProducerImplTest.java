package com.pls.extint.producer.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.pls.extint.sterling.producer.impl.SterlingMessageProducerImpl;

/**
 * Test cases to check functionality of Sterling message queue sender.
 * 
 * @author Pavani Challa
 */
@RunWith(MockitoJUnitRunner.class)
public class SterlingMessageProducerImplTest {

    @InjectMocks
    private SterlingMessageProducerImpl producer;

    private static final String EDI_OUTBOUND_QUEUE = "EDI20OutboundQueue1";

    private static final Integer PRIORITY = 3;

    private static final String FINANCE_OUTBOUND_QUEUE = "FinanceOutboundQueue";

    @Mock
    private JmsTemplate jmsTemplate;

    private static final String XML_MESSAGE = "XML Message sent to Queue";

    @Before
    public void init() {
        ReflectionTestUtils.setField(producer, "financeOutboundQueue", FINANCE_OUTBOUND_QUEUE);
    }

    @Test
    public void testPublishEDIMessage() throws Exception {
        producer.publishEdiMessage(XML_MESSAGE, EDI_OUTBOUND_QUEUE, PRIORITY);

        verify(jmsTemplate, times(1)).convertAndSend(eq(EDI_OUTBOUND_QUEUE), any(ActiveMQObjectMessage.class));
    }

    @Test
    public void testPublishFinanceMessage() throws Exception {
        producer.publishFinanceMessage(XML_MESSAGE);

        verify(jmsTemplate, times(1)).convertAndSend(eq(FINANCE_OUTBOUND_QUEUE), any(ActiveMQObjectMessage.class));
    }
}
