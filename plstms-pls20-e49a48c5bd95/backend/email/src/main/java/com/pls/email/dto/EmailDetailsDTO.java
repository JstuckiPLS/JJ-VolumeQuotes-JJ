package com.pls.email.dto;

import java.util.List;

/**
 * Details for sending shipment details email.
 * 
 * @author Aleksandr Leshchenko
 */
public class EmailDetailsDTO {
    private String recipients;
    private String subject;
    private String content;
    private List<EmailAttachmentDTO> documents;
    private Long loadId;
    private String emailType;
    private String notificationType;

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
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

    public List<EmailAttachmentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<EmailAttachmentDTO> documents) {
        this.documents = documents;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

}