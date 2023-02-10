package com.pls.documentmanagement.domain.bo;

import java.util.Date;

import com.pls.core.common.utils.DateUtility;

/**
 * Brief info for shipment order document.
 * 
 * @author Maxim Medvedev
 */
public class ShipmentDocumentInfoBO {

    public static final String QUERY_BY_LOAD_ID = "com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO.QUERY_BY_LOAD_ID";
    public static final String QUERY_BY_MANUAL_BOL_ID = "com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO.QUERY_BY_MANUAL_BOL_ID";

    public static final String LOAD_ID_PARAM = "loadId";

    public static final String DATE_COLUMN = "modifiedDate";

    public static final String ID_COLUMN = "id";

    public static final String SHIPMENT_ID_COLUMN = "shipmentId";

    public static final String NAME_COLUMN = "docName";

    public static final String FIRST_NAME_COLUMN = "createdByFirstName";

    public static final String LAST_NAME_COLUMN = "createdByLastName";

    public static final String DOC_FILE_TYPE = "docFileType";

    public static final String CREATED_DATE_COLUMN = "createdDate";

    public static final String FILE_NAME_COLUMN = "fileName";

    private Long id;

    private Date modifiedDate;

    private String docName;

    private Long shipmentId;

    private String createdByFirstName;

    private String createdByLastName;

    private String docFileType;

    private Date createdDate;

    private String fileName;

    // private String manualBol;

    /**
     * Get docName value.
     * 
     * @return the docName.
     */
    public String getDocName() {
        return docName;
    }

    /**
     * Set docName value.
     * 
     * @param docName
     *            value to set.
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }


    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Set date value.
     * 
     * @param modifiedDate
     *            value to set.
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = DateUtility.truncateMilliseconds(modifiedDate);
    }

    /**
     * Get shipmentId value.
     * 
     * @return the shipmentId.
     */
    public Long getShipmentId() {
        return shipmentId;
    }

    /**
     * Set shipmentId value.
     * 
     * @param shipmentId
     *            value to set.
     */
    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getCreatedByFirstName() {
        return createdByFirstName;
    }

    public void setCreatedByFirstName(String createdByFirstName) {
        this.createdByFirstName = createdByFirstName;
    }

    public String getCreatedByLastName() {
        return createdByLastName;
    }

    public void setCreatedByLastName(String createdByLastName) {
        this.createdByLastName = createdByLastName;
    }

    public String getDocFileType() {
        return docFileType;
    }

    public void setDocFileType(String docFileType) {
        this.docFileType = docFileType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = DateUtility.truncateMilliseconds(createdDate);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
