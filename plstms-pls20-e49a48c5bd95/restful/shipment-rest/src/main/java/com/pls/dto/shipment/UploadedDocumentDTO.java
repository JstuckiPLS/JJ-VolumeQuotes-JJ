package com.pls.dto.shipment;

/**
 * DTO for document that was previously uploaded.
 * 
 * @author Denis Zhupinsky (Team International)
 */
public class UploadedDocumentDTO {
    private Long id;
    private String fileName;
    private String docType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}
