package com.pls.email;

import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.email.dto.EmailAttachmentDTO;

/**
 * Represents email message used by PLS 2.0 application.
 * 
 * @author Stas Norochevskiy
 */
public class EmailMessage {

    private String from;

    private List<String> recipients = new ArrayList<String>();

    private String subject;

    private String content;

    private String bCC;

    private List<EmailAttachmentDTO> attachments = new ArrayList<EmailAttachmentDTO>();

    private EmailType emailType;

    private NotificationTypeEnum notificationType;

    private List<Long> loadIds;

    private Long sendBy;

    /**
     * Empty constructor.
     */
    public EmailMessage() {
    }

    /**
     * Constructor.
     * 
     * @param recipients
     *            list of email recipients
     * @param subject
     *            email subject
     * @param content
     *            email text body
     * @param attachments
     *            attachments
     * @param bCC
     *            BCC email
     * @param emailType
     *            document, invoice, notification etc email
     * @param notificationType
     *            status of load
     * @param sendBy
     *            id of current logged user
     * @param loadIds
     *            id of loads for email
     */
    public EmailMessage(List<String> recipients, String subject, String content, List<EmailAttachmentDTO> attachments, String bCC,
            EmailType emailType, NotificationTypeEnum notificationType, List<Long> loadIds, Long sendBy) {
        addRecipients(recipients);
        this.subject = subject;
        this.content = content;
        addAttachments(attachments);
        this.bCC = bCC;
        this.emailType = emailType;
        this.notificationType = notificationType;
        this.loadIds = loadIds;
        this.sendBy = sendBy;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public List<Long> getLoadIds() {
        return loadIds;
    }

    public void setLoadId(List<Long> loadIds) {
        this.loadIds = loadIds;
    }

    public Long getSendBy() {
        return sendBy;
    }

    public void setSendBy(Long sendBy) {
        this.sendBy = sendBy;
    }

    /**
     * Add recipient.
     * 
     * @param recipient
     *            email recipient. Can be <code>null</code>
     */
    public void addRecipient(String recipient) {
        if (recipient != null) {
            this.recipients.add(recipient);
        }
    }

    /**
     * Add list of recipients.
     * 
     * @param recipients
     *            email recipients
     */
    public final void addRecipients(List<String> recipients) {
        if (recipients != null) {
            this.recipients.addAll(recipients);
        }
    }

    public List<EmailAttachmentDTO> getAttachments() {
        return attachments;
    }

    /**
     * Add one more attachment.
     * 
     * @param attachments
     *            attachment. Can be <code>null</code>.
     */
    public final void addAttachments(List<EmailAttachmentDTO> attachments) {
        if (attachments != null) {
            this.attachments.addAll(attachments);
        }
    }

    /**
     * Add attachment.
     * 
     * @param attachment
     *            attachments
     */
    public void addAttachment(EmailAttachmentDTO attachment) {
        if (attachment != null) {
            this.attachments.add(attachment);
        }
    }

    public String getBCC() {
        return bCC;
    }

    public void setBCC(String bCC) {
        this.bCC = bCC;
    }
}
