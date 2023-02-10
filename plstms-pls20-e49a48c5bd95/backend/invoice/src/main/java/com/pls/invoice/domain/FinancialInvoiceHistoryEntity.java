package com.pls.invoice.domain;

import java.util.Date;

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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.invoice.domain.bo.enums.InvoiceReleaseStatus;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Entity for history of invoicing loads to financial system.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "INVOICE_HISTORY")
public class FinancialInvoiceHistoryEntity implements HasModificationInfo, Identifiable<Long> {
    private static final long serialVersionUID = -6377206909048230607L;

    public static final String Q_NEXT_INVOICE_ID_SEQ = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_NEXT_INVOICE_ID_SEQ";
    public static final String Q_NEXT_CBI_SEQ = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_NEXT_CBI_SEQ";
    public static final String Q_LOAD_IDS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_LOAD_IDS";
    public static final String Q_ADJUSTMENT_IDS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_ADJUSTMENT_IDS";
    public static final String Q_GET_LOADS_FOR_PROCESSING = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_LOADS_FOR_PROCESSING";
    public static final String Q_GET_ADJ_FOR_PROCESSING = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_ADJ_FOR_PROCESSING";
    public static final String I_LOADS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.I_LOADS";
    public static final String I_ADJUSTMENTS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.I_ADJUSTMENTS";
    public static final String U_LOADS_INVOICE_NUMBERS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.U_LOADS_INVOICE_NUMBERS";
    public static final String U_ADJUSTMENTS_INVOICE_NUMBERS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.U_ADJUSTMENTS_INVOICE_NUMBERS";
    public static final String Q_INVOICE_RESULTS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_INVOICE_RESULTS";
    public static final String Q_ALL_LOADS_ID = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_ALL_LOADS_ID";
    public static final String Q_GROUP_INVOICE_NUMBERS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GROUP_INVOICE_NUMBERS";
    public static final String I_CBI_LOADS_HIST = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.I_CBI_LOADS_HIST";
    public static final String I_CBI_ADJUSTMENTS_HIST = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.I_CBI_ADJUSTMENTS_HIST";
    public static final String Q_BILL_TO_BY_INVOICE_ID = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_BILL_TO_BY_INVOICE_ID";
    public static final String U_LOADS_INVOICED_IN_FIN = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.U_LOADS_INVOICED_IN_FIN";
    public static final String U_ADJ_INVOICED_IN_FIN = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.U_ADJ_INVOICED_IN_FIN";
    public static final String Q_GET_LOADS_BY_INVOICE_ID = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_LOADS_BY_INVOICE_ID";
    public static final String Q_GET_INVOICE_HISTORY = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_INVOICE_HISTORY";
    public static final String Q_GET_INVOICE_HISTORY_CBI_DETAILS_LOADS =
            "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_INVOICE_HISTORY_CBI_DETAILS_LOADS";
    public static final String Q_GET_AR_BY_INVOICE_NUMBERS_ADJ =
            "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_AR_BY_INVOICE_NUMBERS_ADJ";
    public static final String Q_GET_AR_BY_INVOICE_NUMBERS =
            "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_AR_BY_INVOICE_NUMBERS";
    public static final String Q_GET_ADJUSTMENTS_BY_INVOICE_ID =
            "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_ADJUSTMENTS_BY_INVOICE_ID";
    public static final String Q_GET_INVOICE_HISTORY_CBI_DETAILS_ADJ =
            "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_GET_INVOICE_HISTORY_CBI_DETAILS_ADJ";
    public static final String Q_ADJ_REASONS = "com.pls.invoice.domain.FinancialInvoiceHistoryEntity.Q_ADJ_REASONS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_history_sequence")
    @SequenceGenerator(name = "invoice_history_sequence", sequenceName = "INVOICE_HISTORY_SEQ", allocationSize = 1)
    @Column(name = "INVOICE_HISTORY_ID")
    private Long id;

    @Column(name = "INVOICE_ID", nullable = false)
    private Long invoiceId;

    @Column(name = "INVOICE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", insertable = false, updatable = false)
    private LoadEntity load;

    @Column(name = "FAA_DETAIL_ID")
    private Long adjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAA_DETAIL_ID", insertable = false, updatable = false)
    private FinancialAccessorialsEntity adjustment;

    @Column(name = "release_status", nullable = false)
    @Type(type = "com.pls.invoice.domain.usertype.InvoiceReleaseStatusUserType")
    private InvoiceReleaseStatus releaseStatus;

    @ManyToOne
    @JoinColumn(name = "billing_status_reason_code")
    private BillingStatusReasonEntity errorMessage;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", insertable = false, updatable = false)
    private Date createdDate;

    @Column(name = "CREATED_BY", insertable = false, updatable = false)
    private Long createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIFIED", insertable = false, updatable = false)
    private Date modifiedDate;

    @Column(name = "MODIFIED_BY", insertable = false, updatable = false)
    private Long modifiedBy;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public FinancialAccessorialsEntity getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(FinancialAccessorialsEntity adjustment) {
        this.adjustment = adjustment;
    }

    public InvoiceReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(InvoiceReleaseStatus releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public BillingStatusReasonEntity getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(BillingStatusReasonEntity errorMessage) {
        this.errorMessage = errorMessage;
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

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public PlainModificationObject getModification() {
        return modification;
    }
}
