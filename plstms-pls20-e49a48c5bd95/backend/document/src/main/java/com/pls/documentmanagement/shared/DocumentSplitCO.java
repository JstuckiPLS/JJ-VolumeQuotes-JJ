package com.pls.documentmanagement.shared;

import java.io.Serializable;

/**
 * Criteria for creating a pdf document from another document. The criteria object contains the start and end page numbers of the other document from
 * which this document has to be created.
 * 
 * @author Pavani Challa
 * 
 */
public class DocumentSplitCO implements Serializable {

    private static final long serialVersionUID = 7862245381217694620L;

    private Long loadId;

    private String documentType;

    private int fromPage;

    private int toPage;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getFromPage() {
        return fromPage;
    }

    public void setFromPage(int fromPage) {
        this.fromPage = fromPage;
    }

    public int getToPage() {
        return toPage;
    }

    public void setToPage(int toPage) {
        this.toPage = toPage;
    }
}
