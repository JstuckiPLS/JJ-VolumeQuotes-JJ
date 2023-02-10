package com.pls.dto.address;

import com.pls.core.domain.enums.DocRequestType;

/**
 * DTO for required documents.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class RequiredDocumentDTO {
    private Long id;
    private String documentType;
    private String documentTypeDescription;
    private DocRequestType customerRequestType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeDescription() {
        return documentTypeDescription;
    }

    public void setDocumentTypeDescription(String documentTypeDescription) {
        this.documentTypeDescription = documentTypeDescription;
    }

    public DocRequestType getCustomerRequestType() {
        return customerRequestType;
    }

    public void setCustomerRequestType(DocRequestType customerRequestType) {
        this.customerRequestType = customerRequestType;
    }
}
