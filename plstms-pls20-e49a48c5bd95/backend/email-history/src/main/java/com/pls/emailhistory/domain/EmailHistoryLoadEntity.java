package com.pls.emailhistory.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Domain class for linking email audit record with loads.
 * @author Sergii Belodon
 */
@Entity
@Table(name = "EMAIL_HISTORY_LOAD")
public class EmailHistoryLoadEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 1144029030700924305L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_history_load_sequence")
    @SequenceGenerator(name = "email_history_load_sequence", sequenceName = "EMAIL_HISTORY_LOAD_SEQ", allocationSize = 1)
    @Column(name = "EMAIL_HISTORY_LOAD_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EMAIL_HISTORY_ID")
    private EmailHistoryEntity emailHistory;

    @Column(name = "LOAD_ID")
    private Long loadId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmailHistoryEntity getEmailHistory() {
        return emailHistory;
    }

    public void setEmailHistory(EmailHistoryEntity emailHistory) {
        this.emailHistory = emailHistory;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

}
