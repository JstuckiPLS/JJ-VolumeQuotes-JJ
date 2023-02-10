package com.pls.email.consumer;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;

import com.pls.email.EmailMessage;

/**
 * Email sender class.<br/>
 * Don't use it directly from the code. Do it asynchronously with email producer.
 * 
 * @author Aleksandr Leshchenko
 */
public interface EmailSender extends JavaMailSender {

    /**
     * Send email.
     * 
     * @param emailMessage
     *            email message to send
     * @throws Exception
     *             exception
     */
    void sendMessage(EmailMessage emailMessage) throws Exception;

    /**
     * Get properties. @see JavaMailSenderImpl#getJavaMailProperties() for more details.
     * 
     * @return {@link Properties}
     */
    Properties getJavaMailProperties();

    /**
     * Get properties. @see JavaMailSenderImpl#setJavaMailProperties() for more details.
     * 
     * @param testMailProperties
     *            properties to set
     */
    void setJavaMailProperties(Properties testMailProperties);

    /**
     * Set port.
     * 
     * @param port
     *            port
     */
    void setPort(int port);

    /**
     * Set host.
     * 
     * @param host
     *            host
     */
    void setHost(String host);
}
