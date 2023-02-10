package com.pls.core.util;

import javax.jms.ConnectionFactory;

import org.mockito.Mockito;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

/**
 * JMS Template implementation for testing.
 * 
 * @author Aleksandr Leshchenko
 */
public class TestJMSTemplate extends JmsTemplate {
    /**
     * Constructor.
     *
     */
    public TestJMSTemplate() {
        super(Mockito.mock(ConnectionFactory.class));
    }

    @Override
    public void convertAndSend(Object message) throws JmsException {
    }
}
