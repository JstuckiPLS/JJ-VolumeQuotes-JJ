package com.pls.email.dto;

import java.io.Serializable;

import com.pls.documentmanagement.domain.LoadDocumentEntity;

/**
 * Represents email attachment.
 * 
 * @author Stas Norochevskiy
 */
public class EmailAttachmentDTO implements Serializable {
    private static final long serialVersionUID = 4200394920942595296L;

    private Long imageMetadataId;
    private String attachmentFileName;

    /**
     * Default Constructor for serialization.
     */
    public EmailAttachmentDTO() {
    }

    /**
     * Constructor.
     * 
     * @param imageMetadataId
     *            {@link LoadDocumentEntity#getId()}
     */
    public EmailAttachmentDTO(Long imageMetadataId) {
        this.imageMetadataId = imageMetadataId;
    }

    /**
     * Constructor.
     * 
     * @param imageMetadataId
     *            {@link LoadDocumentEntity#getId()}
     * @param attachmentFileName
     *            name which will be used for attached file. can be <code>null</code>
     */
    public EmailAttachmentDTO(Long imageMetadataId, String attachmentFileName) {
        this.imageMetadataId = imageMetadataId;
        this.attachmentFileName = attachmentFileName;
    }

    public Long getImageMetadataId() {
        return imageMetadataId;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }
}
