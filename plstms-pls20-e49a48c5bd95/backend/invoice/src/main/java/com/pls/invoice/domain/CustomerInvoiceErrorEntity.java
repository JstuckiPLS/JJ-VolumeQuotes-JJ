package com.pls.invoice.domain;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * Entity for customer invoice errors.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@Table(name = "CUSTOMER_INVOICE_ERRORS")
public class CustomerInvoiceErrorEntity implements Identifiable<Long>, HasModificationInfo {
    public static final String Q_ACTIVE_ERRORS_QUERY = "com.pls.invoice.domain.CustomerInvoiceErrorEntity.Q_ACTIVE_ERRORS_QUERY";
    public static final String Q_ACTIVE_ERRORS_COUNT_QUERY = "com.pls.invoice.domain.CustomerInvoiceErrorEntity.Q_ACTIVE_ERRORS_COUNT_QUERY";
    public static final String Q_INVOICE_DETAILS = "com.pls.invoice.domain.CustomerInvoiceErrorEntity.Q_INVOICE_DETAILS";

    private static final long serialVersionUID = -700877690703203225L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_invoice_error_sequence")
    @SequenceGenerator(name = "customer_invoice_error_sequence", sequenceName = "CUST_INVOICE_ERR_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "INVOICE_NUM", length = 20)
    private String invoiceNumber;

    @Column(name = "INVOICE_ID")
    private Long invoiceId;

    @Column(name = "MESSAGE", length = 200)
    private String message;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "STACK_TRACE")
    private String stackTrace;

    @Column(name = "SENT_EMAIL")
    @Type(type = "yes_no")
    private Boolean sentEmail;

    @Column(name = "SENT_EDI")
    @Type(type = "yes_no")
    private Boolean sentEdi;

    @Column(name = "SENT_TO_FINANCE")
    @Type(type = "yes_no")
    private Boolean sentToFinance;

    @Column(name = "SENT_DOCUMENTS")
    @Type(type = "yes_no")
    private Boolean sentDocuments;

    @Column
    @Enumerated(EnumType.STRING)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "INVOICE_ID", insertable = false, updatable = false)
    private Set<FinancialInvoiceHistoryEntity> invoiceHistoryItems;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Version
    private int version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Boolean getSentEmail() {
        return sentEmail;
    }

    public void setSentEmail(Boolean sentEmail) {
        this.sentEmail = sentEmail;
    }

    public Boolean getSentEdi() {
        return sentEdi;
    }

    public void setSentEdi(Boolean sentEdi) {
        this.sentEdi = sentEdi;
    }

    public Boolean getSentToFinance() {
        return sentToFinance;
    }

    public void setSentToFinance(Boolean sentToFinance) {
        this.sentToFinance = sentToFinance;
    }

    public Boolean getSentDocuments() {
        return sentDocuments;
    }

    public void setSentDocuments(Boolean sentDocuments) {
        this.sentDocuments = sentDocuments;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<FinancialInvoiceHistoryEntity> getInvoiceHistoryItems() {
        return invoiceHistoryItems;
    }

    public void setInvoiceHistoryItems(Set<FinancialInvoiceHistoryEntity> invoiceHistoryItems) {
        this.invoiceHistoryItems = invoiceHistoryItems;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
