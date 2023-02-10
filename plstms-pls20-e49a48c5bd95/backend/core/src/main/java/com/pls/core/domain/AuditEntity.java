package com.pls.core.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.sterling.EDIMessageType;

/**
 * Represents the object holding the information for auditing the data received by EDI Server.
 * 
 * @author Yasaman Palumbo
 *
 */
@Entity
@Table(name = "INT_AUDIT")
public class AuditEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 1244929034700924303L;
    public static final String Q_GET_INTEGRATION_LOGS = "com.pls.core.domain.AuditEntity.Q_GET_INTEGRATION_LOGS";
    public static final String Q_GET_EDI_204_XML = "com.pls.core.domain.AuditEntity.Q_GET_EDI_204_XML";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_sequence")
    @SequenceGenerator(name = "audit_sequence", sequenceName = "INT_AUDIT_SEQ", allocationSize = 1)
    @Column(name = "AUDIT_ID")
    private Long id;

    @Column(name = "BOL")
    private String bol;

    @Column(name = "VIEWED_BY")
    private Long viewedBy;

    @Column(name = "VIEWED_DATE")
    private Date viewedDate;

    @Column(name = "INB_OTB")
    private char inbOtb;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "MESSAGE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private EDIMessageType messageType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SCAC")
    private String scac;

    @Column(name = "SHIPMENT_NUM")
    private String shipmentNum;

    @Column(name = "SHIPPER_ORG_ID")
    private Long shipperOrgId;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @OneToOne(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    private AuditDetailEntity auditDetail;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public AuditDetailEntity getAuditDetail() {
        return auditDetail;
    }

    public void setAuditDetail(AuditDetailEntity detail) {
        auditDetail = detail;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public Long getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(Long viewedBy) {
        this.viewedBy = viewedBy;
    }

    public Date getViewedDate() {
        return viewedDate;
    }

    public void setViewedDate(Date viewedDate) {
        this.viewedDate = viewedDate;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadID(Long loadId) {
        this.loadId = loadId;
    }

    public EDIMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(EDIMessageType messageType) {
        this.messageType = messageType;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getShipmentNum() {
        return shipmentNum;
    }

    public void setShipmentNum(String shipmentNum) {
        this.shipmentNum = shipmentNum;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public void setInbOtb(char inbOtb) {
        this.inbOtb = inbOtb;
    }

    public char getInbOtb() {
        return inbOtb;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
