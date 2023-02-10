package com.pls.emailhistory.domain.bo;

/**
 * The Class EmailHistoryAttachmentBO.
 * @author Sergii Belodon
 */
public class EmailHistoryAttachmentBO {
    private Long emailHistoryId;
    private Long attachmentId;
    private String attachmentName;

    public Long getEmailHistoryId() {
        return emailHistoryId;
    }
    public void setEmailHistoryId(Long emailHistoryId) {
        this.emailHistoryId = emailHistoryId;
    }
    public Long getAttachmentId() {
        return attachmentId;
    }
    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
    public String getAttachmentName() {
        return attachmentName;
    }
    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }
}
