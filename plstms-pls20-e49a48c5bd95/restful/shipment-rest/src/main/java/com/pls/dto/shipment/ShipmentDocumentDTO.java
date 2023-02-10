package com.pls.dto.shipment;

import java.util.Date;

/**
 * Brief information for shipment order documents.
 * 
 * @author Andrey Kachur
 */
public class ShipmentDocumentDTO {

    private Long id;

    private Date date;

    private String name;

    private String createdByName;

    private Long shipmentId;

    private String docFileType;

    private Date createdDate;

    private String fileName;

    /**
     * Default constructor with all fields set to default values.
     */
    public ShipmentDocumentDTO() {
    }

    /**
     * Constructor.
     * 
     * @param id
     *            Document ID.
     * @param name
     *            Document name.
     * @param date
     *            Document date.
     * @param shipmentId
     *            ID of related shipment order.
     * @param createdByName
     *            Name of user who created that document
     * @param docFileType
     *            type of document file
     * @param createdDate
     *            Date Date the document is created
     * @param fileName
     *            File name of the document in file system
     */
    public ShipmentDocumentDTO(Long id, String name, Date date, Long shipmentId, String createdByName,
            String docFileType, Date createdDate, String fileName) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.shipmentId = shipmentId;
        this.createdByName = createdByName;
        this.docFileType = docFileType;
        this.createdDate = createdDate;
        this.fileName = fileName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getDocFileType() {
        return docFileType;
    }

    public void setDocFileType(String docFileType) {
        this.docFileType = docFileType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o instanceof ShipmentDocumentDTO) {
            final ShipmentDocumentDTO other = (ShipmentDocumentDTO) o;
            return other.getId() != null && other.getId().equals(this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id == null ? super.hashCode() : id.hashCode();
    }
}
