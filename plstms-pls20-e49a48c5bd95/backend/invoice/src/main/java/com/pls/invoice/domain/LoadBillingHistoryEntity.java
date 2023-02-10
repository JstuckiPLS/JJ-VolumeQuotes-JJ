package com.pls.invoice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.invoice.domain.bo.enums.InvoiceErrorCode;

/**
 * Entity for load billing history.
 *
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "LOAD_BILLING_HISTORY")
public class LoadBillingHistoryEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 1262854118011866560L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_billing_history_sequence")
    @SequenceGenerator(name = "load_billing_history_sequence", sequenceName = "LBY_SEQ", allocationSize = 1)
    @Column(name = "HISTORY_ID")
    private Long id;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "COST_DETAIL_ID")
    private Long costDetailId;

    @Column(name = "FAA_DETAIL_ID")
    private Long adjustmentId;

    @Column(name = "OLD_FINAN_STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus oldFinalizationStatus;

    @Column(name = "NEW_FINAN_STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus newFinalizationStatus;

    @Column(name = "STATUS_REASON")
    @Enumerated(EnumType.STRING)
    private InvoiceErrorCode statusReason;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getCostDetailId() {
        return costDetailId;
    }

    public void setCostDetailId(Long costDetailId) {
        this.costDetailId = costDetailId;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public ShipmentFinancialStatus getOldFinalizationStatus() {
        return oldFinalizationStatus;
    }

    public void setOldFinalizationStatus(ShipmentFinancialStatus oldFinalizationStatus) {
        this.oldFinalizationStatus = oldFinalizationStatus;
    }

    public ShipmentFinancialStatus getNewFinalizationStatus() {
        return newFinalizationStatus;
    }

    public void setNewFinalizationStatus(ShipmentFinancialStatus newFinalizationStatus) {
        this.newFinalizationStatus = newFinalizationStatus;
    }

    public InvoiceErrorCode getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(InvoiceErrorCode statusReason) {
        this.statusReason = statusReason;
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
}
