package com.pls.emailhistory.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Domain class for linking email audit record with attachment.
 * @author Sergii Belodon
 */
@Entity
@Table(name = "EMAIL_HISTORY_ATTACHMENT")
public class EmailHistoryAttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_history_attachment_sequence")
    @SequenceGenerator(name = "email_history_attachment_sequence", sequenceName = "EMAIL_HISTORY_ATTACHMENT_SEQ", allocationSize = 1)
    @Column(name = "EMAIL_HISTORY_ATTACHMENT_ID")
    private Long id;

    @Column(name = "IMAGE_METADATA_ID")
    private Long imageMetadataId;

    @ManyToOne
    @JoinColumn(name = "EMAIL_HISTORY_ID")
    private EmailHistoryEntity emailHistory;


    @Column(name = "FILENAME_FOR_USER")
    private String filenameForUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getImageMetadataId() {
        return imageMetadataId;
    }

    public void setImageMetadataId(Long imageMetadataId) {
        this.imageMetadataId = imageMetadataId;
    }

    public EmailHistoryEntity getEmailHistory() {
        return emailHistory;
    }

    public void setEmailHistory(EmailHistoryEntity emailHistory) {
        this.emailHistory = emailHistory;
    }

    public String getFilenameForUser() {
        return filenameForUser;
    }

    public void setFilenameForUser(String filenameForUser) {
        this.filenameForUser = filenameForUser;
    }
}
