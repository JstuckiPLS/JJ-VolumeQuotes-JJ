package com.pls.core.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * This Entity is holding the details of the message received from the EDI for auditing.
 * 
 * @author Yasaman Palumbo
 *
 */

@Entity
@Table(name = "INT_AUDIT_DETAILS")
public class AuditDetailEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 6932530521528824735L;
    public static final String Q_GET_INTEGRATION_LOG_DETAILS = "com.pls.core.domain.AuditDetailEntity.Q_GET_INTEGRATION_LOG_DETAILS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_details_sequence")
    @SequenceGenerator(name = "audit_details_sequence", sequenceName = "INT_AUDIT_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "AUDIT_DETAIL_ID")
    private Long id;

    @Column(name = "MESSAGE")
    private String message;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_ID")
    private AuditEntity audit;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public AuditEntity getAudit() {
        return audit;
    }

    public void setAudit(AuditEntity audit) {
        this.audit = audit;
    }
}
