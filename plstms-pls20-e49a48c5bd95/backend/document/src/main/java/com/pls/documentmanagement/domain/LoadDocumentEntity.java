package com.pls.documentmanagement.domain;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.common.MimeTypes;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.document.DocumentTypeEntity;
import com.pls.core.shared.Status;

/**
 * Entity class for LOD_DOCUMENTS table. Stores the metadata about the document like file name, path, document type.
 * 
 * Note: setDocBytes() method should be used only when downloading documents using API. Sometimes, the API send each page of the document as separate
 * set of bytes. Once the complete document is downloaded, all these pages are converted to single pdf and then set to content field. Please use
 * setContent() method everywhere else.
 * 
 * @author Pavani Challa
 * 
 */
@Entity
@Table(name = "IMAGE_METADATA")
public class LoadDocumentEntity implements Identifiable<Long>, HasModificationInfo {
    public static final String Q_BY_LOAD_ID = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_BY_LOAD_ID";
    public static final String Q_BY_EARLIER_CREATED_DATE = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_BY_EARLIER_CREATED_DATE";
    public static final String Q_UPDATE_STATUS_BY_DOC_IDS = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_UPDATE_STATUS_BY_DOC_IDS";
    public static final String Q_BY_LOAD_ID_AND_DOC_TYPE = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_BY_LOAD_ID_AND_DOC_TYPE";
    public static final String Q_DELETE_TEMP_DOC = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_DELETE_TEMP_DOC";
    public static final String Q_GET_CUSTOMER_LOGO_FOR_BOL = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_GET_CUSTOMER_LOGO_FOR_BOL";
    public static final String Q_GET_CUSTOMER_LOGO_SHIP_LABEL = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_GET_CUSTOMER_LOGO_SHIP_LABEL";
    public static final String Q_GET_DOCUMENT_BY_ID_AND_TOKEN = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_GET_DOCUMENT_BY_ID_AND_TOKEN";
    public static final String LOAD_ID_PARAM = "loadId";
    public static final String MANUAL_BOL_ID_PARAM = "manualBolId";
    public static final String ID_PARAM = "id";
    public static final String CREATED_DATE_PARAM = "createdDate";
    public static final String DOC_IDS_PARAM = "docIds";
    public static final String STATUS_PARAM = "status";
    public static final String DOCUMENT_TYPE_PARAM = "documentType";
    public static final String Q_GET_REQUIRED_AND_AVAILABLE_DOCUMENTS =
            "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_GET_REQUIRED_AND_AVAILABLE_DOCUMENTS";
    public static final String Q_BY_MANUAL_BOL_ID_AND_DOC_TYPE =
            "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_BY_MANUAL_BOL_ID_AND_DOC_TYPE";
    public static final String Q_REQ_BY_LOAD_ID = "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_REQ_BY_LOAD_ID";
    public static final String Q_GET_CREATED_DATE_FOR_REQ_DOCS =
            "com.pls.documentmanagement.domain.LoadDocumentEntity.Q_GET_CREATED_DATE_FOR_REQ_DOCS";
    private static final long serialVersionUID = 8076923414146218376L;

    /**
     * default constructor.
     */
    public LoadDocumentEntity() {
        super();
    }

    /**
     * constructor from id.
     * @param id - id
     */
    public LoadDocumentEntity(Long id) {
        super();
        this.id = id;
    }

    @Id
    @Column(name = "IMAGE_META_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_metadata_sequence")
    @SequenceGenerator(name = "image_metadata_sequence", sequenceName = "IMAGE_METADATA_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "MANUAL_BOL_ID")
    private Long manualBol;

    @Column(name = "IMAGE_FILE_NAME")
    private String docFileName;

    @Column(name = "IMAGE_FILE_PATH")
    private String documentPath;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_TYPE", referencedColumnName = "DOCUMENT_TYPE", insertable = false, updatable = false)
    private DocumentTypeEntity documentTypeEntity;

    @Column(name = "API_TYPE_ID")
    private Long apiTypeId;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "SHIPMENT_NUMBER")
    private String shipperRefNum;

    @Column(name = "BOL")
    private String bol;

    @Column(name = "IMAGE_FILE_TYPE")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.common.MimeTypes"), @Parameter(name = "identifierMethod", value = "getMimeString"),
            @Parameter(name = "valueOfMethod", value = "getByValue") })
    private MimeTypes fileType = MimeTypes.PDF;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Version
    private int version = 1;

    @Transient
    private byte[] content;

    @Transient
    private List<byte[]> pages;

    @Transient
    private InputStream streamContent;

    @Transient
    private long streamLength;

    @Column(name = "DOWNLOAD_TOKEN", updatable = false)
    private String downloadToken = UUID.randomUUID().toString();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getDocFileName() {
        return docFileName;
    }

    public void setDocFileName(String docFileName) {
        this.docFileName = docFileName;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public DocumentTypeEntity getDocumentTypeEntity() {
        return documentTypeEntity;
    }

    public void setDocumentTypeEntity(DocumentTypeEntity documentTypeEntity) {
        this.documentTypeEntity = documentTypeEntity;
    }

    public Long getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(Long apiTypeId) {
        this.apiTypeId = apiTypeId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getShipperRefNum() {
        return shipperRefNum;
    }

    public void setShipperRefNum(String shipperRefNum) {
        this.shipperRefNum = shipperRefNum;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public MimeTypes getFileType() {
        return fileType;
    }

    public void setFileType(MimeTypes fileType) {
        this.fileType = fileType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getManualBolId() {
        return manualBol;
    }

    public void setManualBol(Long manualBol) {
        this.manualBol = manualBol;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;

    }

    public byte[] getContent() {
        return content;
    }

    /**
     * Set the content of the document.
     * 
     * @param pContent
     *            bytes for the pdf page
     */
    public void setContent(String pContent) {
        content = pContent.getBytes();
    }

    /**
     * Set the content of the document.
     * 
     * @param pContent
     *            bytes for the pdf page
     */
    public void setContent(byte[] pContent) {
        if (pContent != null) {
            content = Arrays.copyOf(pContent, pContent.length);
        } else {
            content = null;
        }

    }

    public List<byte[]> getPages() {
        return pages;
    }

    /**
     * Sometimes there could be more than one page returned. In that case, the pages need to be aggregated and saved as single document rather than
     * multiple documents.
     * 
     * @param pDocBytes
     *            bytes for the pdf page
     */
    public void setDocBytes(String pDocBytes) {
        if (pages == null || pDocBytes == null) {
            pages = new ArrayList<byte[]>();
        }

        pages.add(pDocBytes.getBytes());
    }

    /**
     * Sometimes there could be more than one page returned. In that case, the pages need to be aggregated and saved as single document rather than
     * multiple documents.
     * 
     * @param pDocBytes
     *            bytes for the pdf page
     */
    public void setDocBytes(byte[] pDocBytes) {
        if (pages == null || pDocBytes == null) {
            pages = new ArrayList<byte[]>();
        }

        pages.add(Arrays.copyOf(pDocBytes, pDocBytes.length));
    }

    public InputStream getStreamContent() {
        return streamContent;
    }

    public void setStreamContent(InputStream streamContent) {
        this.streamContent = streamContent;
    }

    public long getStreamLength() {
        return streamLength;
    }

    public void setStreamLength(long streamLength) {
        this.streamLength = streamLength;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getApiTypeId()).append(getDocFileName()).append(getDocumentPath()).append(getDocumentType())
                .append(getLoadId()).append(getStatus()).append(getFileType()).append(getShipperRefNum()).append(getBol()).append(getDownloadToken())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LoadDocumentEntity) {
            if (obj == this) {
                result = true;
            } else {
                LoadDocumentEntity other = (LoadDocumentEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getApiTypeId(), other.getApiTypeId()).append(getDocFileName(), other.getDocFileName())
                        .append(getDocumentPath(), other.getDocumentPath()).append(getDocumentType(), other.getDocumentType())
                        .append(getLoadId(), other.getLoadId()).append(getModification(), other.getModification())
                        .append(getStatus(), other.getStatus()).append(getFileType(), other.getFileType())
                        .append(getShipperRefNum(), other.getShipperRefNum()).append(getBol(), other.getBol())
                        .append(getDownloadToken(), other.getDownloadToken());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("apiTypeId", getApiTypeId()).append("loadId", getLoadId()).append("docFileName", getDocFileName())
                .append("documentPath", getDocumentPath()).append("documentType", getDocumentType()).append("modification", getModification())
                .append("status", getStatus()).append("fileType", getFileType()).append("shipmentNumber", getShipperRefNum()).append("bol", getBol());

        return builder.toString();
    }
}
