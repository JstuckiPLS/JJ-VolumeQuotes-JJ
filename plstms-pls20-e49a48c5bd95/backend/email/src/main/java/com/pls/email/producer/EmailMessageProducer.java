package com.pls.email.producer;

import java.util.Collection;
import java.util.List;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;

/**
 * Interface for email produce. It declares method for enqueue email messages for sending.
 * 
 * @author Stas Norochevskiy
 *
 */
public interface EmailMessageProducer {

    /**
     * Put new JMS message for sending email to message queue.
     *
     * @param recipients
     *          email's recipient
     * @param subject
     *          email's subject
     * @param content
     *          email's content
     * @param emailType
     *          email type
     * @param notificationType
     *          notification type
     * @param sendBy
     *          id of user
     * @param loadId
     *          load id
     */
    void sendEmail(List<String> recipients, String subject, String content, EmailType emailType,
            NotificationTypeEnum notificationType, Long sendBy, Collection<Long> loadId);

    /**
     * Put new JMS message for sending email to message queue.
     * 
     * @param emailInfo
     *              emailInfo
     */
    void sendEmail(EmailInfo emailInfo);

    /**
     * Put new JMS message for sending email to message queue.
     * 
     * @param emailInfo
     *              emailInfo
     * @param notSplitRecipients
     *            if <code>false</code>, then each recipient will get its own copy of email, otherwise one email for all recipients will be sent
     */
    void sendEmail(EmailInfo emailInfo, boolean notSplitRecipients);
}
