package com.pls.email.producer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.email.EmailJmsMessageConverter;
import com.pls.email.dto.EmailAttachmentDTO;

/**
 * Base class for email senders.
 * 
 * @author Stas Norochevskiy
 */
@Repository
public class EmailJmsMessageProducer implements EmailMessageProducer {
    private static final Logger LOG = LoggerFactory.getLogger(EmailJmsMessageProducer.class);

    @Autowired
    @Qualifier("emailTemplate")
    protected JmsTemplate jmsTemplate;

    @Override
    public void sendEmail(List<String> recipients, String subject, String content, EmailType emailType,
            NotificationTypeEnum notificationType, Long sendBy, Collection<Long> loadIds) {
        sendEmail(new EmailInfo(null, recipients, subject, content, null, emailType, notificationType, sendBy, loadIds), false);
    }

    @Override
    public void sendEmail(EmailInfo emailInfo) {
        sendEmail(emailInfo, false);
    }

    @Override
    public void sendEmail(EmailInfo emailInfo, boolean notSplitRecipients) {
        LOG.info("Sending EMAIL to JMS");
        emailInfo.setAttachments(getSyncronizedAttachments(emailInfo.getAttachments()));
        if (notSplitRecipients || emailInfo.getRecipients().size() == 1) {
            Map<String, Object> map = EmailJmsMessageConverter.encodeToJmsMap(emailInfo);
            jmsTemplate.convertAndSend(map);
        } else {
            sendSeparateEmailsToRecipients(emailInfo);
        }
    }

    private void sendSeparateEmailsToRecipients(EmailInfo emailInfo) {
        emailInfo.getRecipients().forEach(recipient -> {
            // Prepare email message as MAP that can be sent via JMS
            List<String> singletonRecipient = Collections.singletonList(recipient);
            emailInfo.setRecipients(singletonRecipient);
            Map<String, Object> map = EmailJmsMessageConverter.encodeToJmsMap(emailInfo);
            jmsTemplate.convertAndSend(map);
        });
    }

    private List<EmailAttachmentDTO> getSyncronizedAttachments(List<EmailAttachmentDTO> attachments) {
        if (attachments != null) {
            return Collections.synchronizedList(attachments);
        }
        return null;
    }

}
