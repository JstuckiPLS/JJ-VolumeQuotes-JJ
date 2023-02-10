package com.pls.extint.shared;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.common.MimeTypes;
import com.pls.documentmanagement.domain.LoadDocumentEntity;

/**
 * VO for returning the documents for the load from the API responses.
 * 
 * @author Pavani Challa
 * 
 */
public class DocumentResponseVO extends ApiResponseVO {

    private static final long serialVersionUID = 3243523487533241423L;

    private List<LoadDocumentEntity> loadDocuments;

    private String shipperRefNum;

    private String bol;

    private LoadDocumentEntity currentDoc;

    /**
     * Constructor that sets the properties like loadId, apiTypeId from request object.
     * 
     * @param requestVO
     *            request object from which properties are set.
     */
    public DocumentResponseVO(ApiRequestVO requestVO) {
        super(requestVO);
        setOrgId(requestVO.getCarrierOrgId());
        this.shipperRefNum = requestVO.getShipperRefNum();
        this.bol = requestVO.getBol();
    }

    public List<LoadDocumentEntity> getLoadDocuments() {
        return loadDocuments;
    }

    /**
     * Adds a document to the list of documents for the load and sets the new document as current document.
     * 
     * @param document
     *            document to be added
     */
    public void addDocument(LoadDocumentEntity document) {
        document.setLoadId(getLoadId());
        document.setApiTypeId(getApiTypeId());
        document.setShipperRefNum(this.shipperRefNum);
        document.setBol(this.bol);

        if (loadDocuments == null) {
            loadDocuments = new ArrayList<LoadDocumentEntity>();
        }

        loadDocuments.add(document);
        currentDoc = document;
    }

    /**
     * Loads the document for the document type if existing and sets it as current document to be updated. Else creates a new document to be updated
     * and sets the document type.
     * 
     * @param documentType
     *            type of the document. For e.g., BOL, POD, INVOICE
     */
    public void setDocumentType(String documentType) {
        this.currentDoc = null;
        if (documentType != null && loadDocuments != null && !loadDocuments.isEmpty()) {
            for (LoadDocumentEntity loadDoc : loadDocuments) {
                if (loadDoc.getDocumentType().equals(documentType)) {
                    currentDoc = loadDoc;
                    break;
                }
            }
        }

        if (currentDoc == null && documentType != null) {
            addDocument(new LoadDocumentEntity());
            currentDoc.setDocumentType(documentType);
        }
    }

    /**
     * Sets the format of the document in the current load document. The current load document is opened when the document type is set. Hence the
     * first property set must be the document type.
     * 
     * @param documentFormat
     *            format of the document received from API like PDF, TIFF, JPG etc.
     */
    public void setDocumentFormat(String documentFormat) {
        if (currentDoc != null) {
            MimeTypes mimeType = MimeTypes.getByName(StringUtils.upperCase(documentFormat));
            currentDoc.setFileType(mimeType);
        }
    }

    /**
     * Get mime type of current document.
     * 
     * @return mime type of current document
     */
    public MimeTypes getFileType() {
        if (currentDoc != null) {
            return currentDoc.getFileType();
        }

        return null;
    }

    /**
     * Sets the actual byte of the document in the current load document. The current load document is opened when the document type is set. Hence the
     * first property set must be the document type.
     * 
     * @param bytes
     *            Document Bytes received from API.
     */
    public void setDocBytes(String bytes) {
        if (currentDoc != null) {
            currentDoc.setDocBytes(bytes);
        }
    }

    /**
     * These methods are called using PropertyUtils methods. PropertyUtils doesn't allow overloading. Hence the method name is different even though
     * is has the same functionality as setDocBytes() method. This should be called by setting the pls field name as "bytes" in the api metadata.
     * 
     * @param bytes
     *            Document Bytes received from URL.
     */
    public void setBytes(byte[] bytes) {
        if (currentDoc != null) {
            currentDoc.setDocBytes(bytes);
        }
    }

    /**
     * Sets the content for the current document.
     * 
     * @param content
     *            content to set
     */
    public void setContent(byte[] content) {
        if (currentDoc != null) {
            currentDoc.setContent(content);
        }
    }
}
