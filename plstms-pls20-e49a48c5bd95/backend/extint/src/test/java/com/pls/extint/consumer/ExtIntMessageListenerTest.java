package com.pls.extint.consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;

import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.extint.service.SterlingService;
import com.pls.extint.sterling.consumer.SterlingEDIOutboundJMSMessageListener;

/**
 * Test cases to check functionality of External Integration message queue listener.
 * 
 * @author Pavani Challa
 */
@RunWith(MockitoJUnitRunner.class)
public class ExtIntMessageListenerTest {

    @InjectMocks
    private SterlingEDIOutboundJMSMessageListener listener;

    @Mock
    private SterlingService service;

    @Mock
    private Session session;

    @Mock
    private ObjectMessage objMessage;

    @Mock
    private MapMessage textMessage;

    @Mock
    private IntegrationMessageBO payload;

    @Test
    public void testForObjectMessage() throws Exception {
        SterlingIntegrationMessageBO message = new SterlingIntegrationMessageBO(payload, EDIMessageType.EDI204_STERLING_MESSAGE_TYPE);
        when(objMessage.getObject()).thenReturn(message);
        listener.onMessage(objMessage, session);
        verify(session, times(0)).rollback();
        verify(service, times(1)).sendMessage(message);
    }

    @Test
    public void testForNullPayload() throws Exception {
        listener.onMessage(objMessage, session);
        verify(session, times(1)).rollback();
    }

    @Test
    public void testForWrongPayloadInstance() throws Exception {
        when(objMessage.getObject()).thenReturn(new Serializable() {
        });
        listener.onMessage(objMessage, session);
        verify(session, times(1)).rollback();
    }

    @Test
    public void testForNonObjectMessage() throws Exception {
        listener.onMessage(textMessage, session);
        verify(session, times(1)).rollback();
    }
}