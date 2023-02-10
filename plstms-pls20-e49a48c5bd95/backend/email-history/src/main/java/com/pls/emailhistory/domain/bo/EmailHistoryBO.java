package com.pls.emailhistory.domain.bo;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;

/**
 * Email history BO.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public class EmailHistoryBO {
    private Long id;
    private EmailType emailType;
    private String sendTo;
    private String subject;
    private String text;
    private NotificationTypeEnum notificationType;
    private String sendBy;
    private ZonedDateTime sendTime;
    private List<EmailHistoryAttachmentBO> attachments = new LinkedList<EmailHistoryAttachmentBO>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public String getSendTo() {
        return sendTo;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public String getSendBy() {
        return sendBy;
    }

    public ZonedDateTime getSendTime() {
        return sendTime;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = ZonedDateTime.ofInstant(sendTime.toInstant(), ZoneOffset.UTC);
    }

    public List<EmailHistoryAttachmentBO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmailHistoryAttachmentBO> attachments) {
        this.attachments = attachments;
    }

}
