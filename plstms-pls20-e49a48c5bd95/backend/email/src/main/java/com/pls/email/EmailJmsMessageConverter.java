package com.pls.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.hawtbuf.UTF8Buffer;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.email.producer.EmailInfo;

/**
 * Provides methods to convert {@link EmailMessage} to Map that can be send to JMS, and to decode
 * {@link EmailMessage} from Map receiver from JMS.
 * 
 * @author Stas Norochevskiy
 */
public final class EmailJmsMessageConverter {
    private static final String FROM = "FROM";
    private static final String TO = "TO";
    private static final String SUBJECT = "SUBJECT";
    private static final String PAYLOAD = "PAYLOAD";
    private static final String BCC = "BCC";
    private static final String EMAIL_TYPE = "EMAIL_TYPE";
    private static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    private static final String SEND_BY = "SEND_BY";

    private static final String ATTACHMENTS_COUNT = "ATTACHMENTS_COUNT";
    private static final String ATTACHMENT_ID = "ATTACHMENT_ID";
    private static final String LOADS_COUNT = "LOADS_COUNT";
    private static final String LOAD_ID = "LOAD_ID";
    private static final String ATTACHMENT_FILE_NAME = "ATTACHMENT_FILE_NAME";

    private static final long DELAY = 2 * 1000; //Delay the message for 2 seconds to ensure the session has already been flushed

    private EmailJmsMessageConverter() {
    }

    /**
     * Converts {@link EmailMessage} to Map that can be send to JMS.
     * 
     * @param emailInfo
     *            email info
     * 
     * @return map that can be sent via JMS
     */
    public static Map<String, Object> encodeToJmsMap(EmailInfo emailInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(FROM, emailInfo.getFrom());
        map.put(TO, emailInfo.getRecipients());
        map.put(SUBJECT, emailInfo.getSubject());
        map.put(PAYLOAD, emailInfo.getContent());
        map.put(BCC, emailInfo.getBCC());
        map.put(SEND_BY, emailInfo.getSendBy());
        map.put(EMAIL_TYPE, emailInfo.getEmailType().name());
        if (emailInfo.getNotificationType() != null) {
            map.put(NOTIFICATION_TYPE, emailInfo.getNotificationType().name());
        }
        map.put(ScheduledMessage.AMQ_SCHEDULED_DELAY, DELAY);

        if (emailInfo.getAttachments() != null) {
            synchronized (emailInfo.getAttachments()) {
                for (int i = 0; i < emailInfo.getAttachments().size(); i++) {
                    map.put(ATTACHMENT_ID + i, emailInfo.getAttachments().get(i).getImageMetadataId());
                    map.put(ATTACHMENT_FILE_NAME + i, emailInfo.getAttachments().get(i).getAttachmentFileName());
                }
                map.put(ATTACHMENTS_COUNT, emailInfo.getAttachments().size());
            }
        }

        if (emailInfo.getLoadIds() != null) {
            int i = 0;
            for (Long id : emailInfo.getLoadIds()) {
                map.put(LOAD_ID + i, id);
                i++;
            }
            map.put(LOADS_COUNT, emailInfo.getLoadIds().size());
        }
        return map;
    }

    /**
     * Decode {@link EmailMessage} from Map receiver from JMS.
     * 
     * @param properties
     *            Map received from JMS
     * @return decoded {@link EmailMessage}
     * @throws MessagingException
     *             exception
     */
    public static EmailMessage decodeFromJmsMap(Map<String, Object> properties) throws MessagingException {

        EmailMessage emailMessage = new EmailMessage();

        String from = getStringFromMap(properties, FROM);
        emailMessage.setFrom(from);

        @SuppressWarnings("unchecked")
        List<String> recipientsList = (List<String>) properties.get(TO);
        emailMessage.addRecipients(recipientsList);

        String subject = getStringFromMap(properties, SUBJECT);
        emailMessage.setSubject(subject);

        boolean hasAttachment = properties.containsKey(ATTACHMENTS_COUNT);

        String payload = getStringFromMap(properties, PAYLOAD);
        emailMessage.setContent(payload);

        String notificationType = getStringFromMap(properties, NOTIFICATION_TYPE);
        if (notificationType != null) {
            emailMessage.setNotificationType(NotificationTypeEnum.valueOf(notificationType));
        }

        String emailType = getStringFromMap(properties, EMAIL_TYPE);
        if (emailType != null) {
            emailMessage.setEmailType(EmailType.valueOf(emailType));
        }
        String sendFinanceBcc = getStringFromMap(properties, BCC);
        emailMessage.setBCC(sendFinanceBcc);

        Long sendBy = (Long) properties.get(SEND_BY);
        emailMessage.setSendBy(sendBy);

        if (hasAttachment) {
            int attachmentsCount = (Integer) properties.get(ATTACHMENTS_COUNT);
            for (int i = 0; i < attachmentsCount; i++) {
                EmailAttachmentDTO attachment = new EmailAttachmentDTO((Long) properties.get(ATTACHMENT_ID + i),
                        getStringFromMap(properties, ATTACHMENT_FILE_NAME + i));
                emailMessage.addAttachment(attachment);
            }
        }
        Integer loadsCount = (Integer) properties.get(LOADS_COUNT);
        if (loadsCount != null) {
            emailMessage.setLoadId(new ArrayList<Long>(loadsCount));
            for (int i = 0; i < loadsCount; i++) {
                emailMessage.getLoadIds().add((Long) properties.get(LOAD_ID + i));
            }
        }
        return emailMessage;
    }

    private static String getStringFromMap(Map<String, Object> properties, String key) {
        return StringUtils.trimToNull(ObjectUtils.defaultIfNull((UTF8Buffer) properties.get(key), new UTF8Buffer("")).toString());
    }
}
