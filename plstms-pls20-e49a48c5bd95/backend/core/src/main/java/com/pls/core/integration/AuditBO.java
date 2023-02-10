package com.pls.core.integration;

import java.util.Date;

import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.sterling.EDIMessageType;

/**
 * The business object for the log of EDI integration entity.
 * 
 * @author Yasaman Honarvar
 *
 */
public class AuditBO {

    private char inbOtb;

    private EDIMessageType messageType;

    private Long auditId;

    private String bol;

    private String scac;

    private Long shipperOrgId;

    private String shipmentNumber;

    private Date createdDate;

    private Long createdBy;

    private Date viewedDate;

    private Long viewedBy;

    private String customerName;

    private String carrierName;

    private String status;

    /**
     * receives an Audit Entity and sets up the values in the Business object.
     * 
     * @param audit
     *            the Entity used for setting the variables.
     */
    public void setAuditEntity(AuditEntity audit) {
        setMessageType(audit.getMessageType());
        setAuditId(audit.getId());
        setBol(audit.getBol());
        setShipmentNumber(audit.getShipmentNum());
        setScac(audit.getScac());
        setInbOtb(audit.getInbOtb());
        setShipperOrgId(audit.getShipperOrgId());
        setCreatedDate(new Date());
        setStatus(audit.getStatus());
    }

    public EDIMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(EDIMessageType messageType) {
        this.messageType = messageType;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public char getInbOtb() {
        return inbOtb;
    }

    public void setInbOtb(char inbOtb) {
        this.inbOtb = inbOtb;
    }

    public Date getViewedDate() {
        return viewedDate;
    }

    public void setViewedDate(Date viewedDate) {
        this.viewedDate = viewedDate;
    }

    public Long getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(Long viewedBy) {
        this.viewedBy = viewedBy;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
