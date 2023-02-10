package com.pls.invoice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.pls.core.domain.Identifiable;

/**
 * The entity mapped to FINAN_INT_RESPONSES.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "FINAN_INT_RESPONSES")
public class FinanIntResponsesEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 7730030516109852082L;

    public static final String I_LOADS = "com.pls.invoice.domain.FinanIntResponsesEntity.I_LOADS";
    public static final String I_ADJUSTMENTS = "com.pls.invoice.domain.FinanIntResponsesEntity.I_ADJUSTMENTS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "finan_int_responses_sequence")
    @SequenceGenerator(name = "finan_int_responses_sequence", sequenceName = "finan_int_responses_seq", allocationSize = 1)
    @Column(name = "FINAN_INT_RESP_ID")
    private Long id;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "FAA_DETAIL_ID")
    private Long adjustmentId;

    @Column(name = "SENT_AP_TO_FINANCE")
    private String sentAP;

    @Column(name = "SENT_AR_TO_FINANCE")
    private String sentAR;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public String getSentAP() {
        return sentAP;
    }

    public void setSentAP(String sentAP) {
        this.sentAP = sentAP;
    }

    public String getSentAR() {
        return sentAR;
    }

    public void setSentAR(String sentAR) {
        this.sentAR = sentAR;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
