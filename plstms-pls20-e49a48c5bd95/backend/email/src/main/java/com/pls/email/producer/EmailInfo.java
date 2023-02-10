package com.pls.email.producer;

import java.util.Collection;
import java.util.List;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.email.dto.EmailAttachmentDTO;

/**
 * Information about email.
 * @author Sergii Belodon
 */
public class EmailInfo {
    private String from;
    private List<String> recipients;
    private String subject;
    private String content;
    private List<EmailAttachmentDTO> attachments;
    private EmailType emailType;
    private NotificationTypeEnum notificationType;
    private Long sendBy;
    private Collection<Long> loadIds;
    private String bCC;

    /**
     * Instantiates a new email info.
     *
     * @param from
     *              from field
     * @param recipients
     *              recipients field
     * @param subject
     *              subject of email
     * @param content
     *              content of email
     * @param attachments
     *              email attachments
     * @param emailType
     *              document, invoice, notification etc
     * @param notificationType
     *              load status
     * @param sendBy
     *              id of user
     * @param loadIds
     *              load ids
     */
    public EmailInfo(String from, List<String> recipients, String subject, String content, List<EmailAttachmentDTO> attachments, EmailType emailType,
            NotificationTypeEnum notificationType, Long sendBy, Collection<Long> loadIds) {
        this.from = from;
        this.recipients = recipients;
        this.subject = subject;
        this.content = content;
        this.attachments = attachments;
        this.emailType = emailType;
        this.notificationType = notificationType;
        this.sendBy = sendBy;
        this.loadIds = loadIds;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public List<EmailAttachmentDTO> getAttachments() {
        return attachments;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public Long getSendBy() {
        return sendBy;
    }

    public Collection<Long> getLoadIds() {
        return loadIds;
    }

    public void setAttachments(List<EmailAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getBCC() {
        return bCC;
    }

    public void setBCC(String bCC) {
        this.bCC = bCC;
    }

}