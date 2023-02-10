package com.pls.shipment.domain;

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

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * BILLING AUDIT REASONS Entity.
 * 
 * @author Brichak Aleksandr
 */
@Entity
@Table(name = "LD_BILLING_AUDIT_REASONS")
public class LdBillingAuditReasonsEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String Q_DEACTIVATE_ADJUSTMENT_REASONS =
            "com.pls.shipment.domain.LdBillingAuditReasonsEntity.Q_DEACTIVATE_ADJUSTMENT_REASONS";

    public static final String Q_DEACTIVATE_LOAD_REASONS = "com.pls.shipment.domain.LdBillingAuditReasonsEntity.Q_DEACTIVATE_LOAD_REASONS";

    public static final String Q_DEACTIVATE_MANUAL_LOAD_REASONS =
            "com.pls.shipment.domain.LdBillingAuditReasonsEntity.Q_DEACTIVATE_MANUAL_LOAD_REASONS";

    private static final long serialVersionUID = -742687389750534561L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ld_billing_audit_reason_sequence")
    @SequenceGenerator(name = "ld_billing_audit_reason_sequence", sequenceName = "LD_BILLING_AUDIT_REASONS_SEQ", allocationSize = 1)
    @Column(name = "LD_BILL_AUDIT_RSN_ID")
    private Long ldBillAuditRsnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false, insertable = false, updatable = false)
    private LoadEntity load;

    @Column(name = "LOAD_ID", nullable = false, insertable = true, updatable = false)
    private Long loadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAA_DETAIL_ID", insertable = false, updatable = false)
    private FinancialAccessorialsEntity financialAccessorialEntity;

    @Column(name = "FAA_DETAIL_ID", nullable = true, insertable = true, updatable = false)
    private Long financialAccessorialDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REASON_CD", nullable = false, insertable = false, updatable = false)
    private LdBillAuditReasonCodeEntity billAuditReasonCodeEntity;

    @Column(name = "REASON_CD", nullable = false, insertable = true, updatable = false)
    private String reasonCd;

    @Column(name = "COMMENTS", length = 200)
    private String comment;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public LoadEntity getLoadEntity() {
        return load;
    }

    public void setLoadEntity(LoadEntity loadEntity) {
        this.load = loadEntity;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public FinancialAccessorialsEntity getFinancialAccessorialEntity() {
        return financialAccessorialEntity;
    }

    public void setFinancialAccessorialEntity(FinancialAccessorialsEntity financialAccessorialEntity) {
        this.financialAccessorialEntity = financialAccessorialEntity;
    }

    public Long getFinancialAccessorialDetailId() {
        return financialAccessorialDetailId;
    }

    public void setFinancialAccessorialDetailId(Long financialAccessorialDetailId) {
        this.financialAccessorialDetailId = financialAccessorialDetailId;
    }

    public LdBillAuditReasonCodeEntity getBillAuditReasonCodeEntity() {
        return billAuditReasonCodeEntity;
    }

    public void setBillAuditReasonCodeEntity(LdBillAuditReasonCodeEntity billAuditReasonCodeEntity) {
        this.billAuditReasonCodeEntity = billAuditReasonCodeEntity;
    }

    public String getReasonCd() {
        return reasonCd;
    }

    public void setReasonCd(String reasonCd) {
        this.reasonCd = reasonCd;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Long getId() {
        return ldBillAuditRsnId;
    }

    @Override
    public void setId(Long id) {
        this.ldBillAuditRsnId = id;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

}
