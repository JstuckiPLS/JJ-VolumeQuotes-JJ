package com.pls.core.domain.organization;

import java.util.List;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceProcessingType;
import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.core.domain.enums.WeekDays;

/**
 * Entity for invoice settings.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@Table(name = "INVOICE_SETTINGS")
public class InvoiceSettingsEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 1259612796174112702L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_settings_sequence")
    @SequenceGenerator(name = "invoice_settings_sequence", sequenceName = "INVOICE_SETTINGS_SEQ", allocationSize = 1)
    @Column(name = "INVOICE_SETTINGS_ID")
    private Long id;

    @Column(name = "INVOICE_FORMAT_ID")
    private Long invoiceFormatId;

    @Column(name = "INVOICE_TYPE")
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Column(name = "PROCESSING_TYPE")
    @Enumerated(EnumType.STRING)
    private InvoiceProcessingType processingType;

    @Column(name = "PROCESSING_TIME")
    private Integer processingTime;

    @Type(type = "yes_no")
    @Column(name = "GAINSHARE_ONLY")
    private boolean gainshareOnly;

    @Type(type = "yes_no")
    @Column(name = "EDI_INVOICE")
    private boolean ediInvoice;

    @Column(name = "PROCESSING_DAY_OF_WEEK")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.WeekDays"),
            @Parameter(name = "identifierMethod", value = "getCalendarCode"),
            @Parameter(name = "valueOfMethod", value = "getWeekDayByCalendarWeekDay") })
    private WeekDays processingDayOfWeek;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROCESSING_TIME_TZ")
    private TimeZoneEntity processingTimeTimezone;

    @Column(name = "PROCESSING_PERIOD")
    @Enumerated(EnumType.STRING)
    private ProcessingPeriod processingPeriod;

    @Column(name = "SORT_TYPE")
    @Enumerated(EnumType.STRING)
    private InvoiceSortType sortType;

    @Column(name = "DOCUMENTS")
    @Type(type = "com.pls.core.domain.usertype.EnumListToStringUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.InvoiceDocument")})
    private List<InvoiceDocument> documents;

    @Type(type = "yes_no")
    @Column(name = "NOT_SPLIT_RECIPIENTS")
    private boolean notSplitRecipients;

    @Type(type = "yes_no")
    @Column(name = "NO_INVOICE_DOCUMENT")
    private boolean noInvoiceDocument;

    @Column(name = "CBI_INVOICE_TYPE")
    @Enumerated(EnumType.STRING)
    private CbiInvoiceType cbiInvoiceType = CbiInvoiceType.PLS;

    @Column(name = "RELEASE_DAY_OF_WEEK")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.WeekDays"),
            @Parameter(name = "identifierMethod", value = "getCalendarCode"),
            @Parameter(name = "valueOfMethod", value = "getWeekDayByCalendarWeekDay") })
    private WeekDays releaseDayOfWeek;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    private int version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceFormatId() {
        return invoiceFormatId;
    }

    public void setInvoiceFormatId(Long invoiceFormatId) {
        this.invoiceFormatId = invoiceFormatId;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public InvoiceProcessingType getProcessingType() {
        return processingType;
    }

    public void setProcessingType(InvoiceProcessingType processingType) {
        this.processingType = processingType;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Integer getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Integer processingTime) {
        this.processingTime = processingTime;
    }

    public boolean isGainshareOnly() {
        return gainshareOnly;
    }

    public void setGainshareOnly(boolean gainshareOnly) {
        this.gainshareOnly = gainshareOnly;
    }

    public boolean isEdiInvoice() {
        return ediInvoice;
    }

    public void setEdiInvoice(boolean ediInvoice) {
        this.ediInvoice = ediInvoice;
    }

    public WeekDays getProcessingDayOfWeek() {
        return processingDayOfWeek;
    }

    public void setProcessingDayOfWeek(WeekDays processingDayOfWeek) {
        this.processingDayOfWeek = processingDayOfWeek;
    }

    public TimeZoneEntity getProcessingTimeTimezone() {
        return processingTimeTimezone;
    }

    public void setProcessingTimeTimezone(TimeZoneEntity processingTimeTimezone) {
        this.processingTimeTimezone = processingTimeTimezone;
    }

    public ProcessingPeriod getProcessingPeriod() {
        return processingPeriod;
    }

    public void setProcessingPeriod(ProcessingPeriod processingPeriod) {
        this.processingPeriod = processingPeriod;
    }

    public InvoiceSortType getSortType() {
        return sortType;
    }

    public void setSortType(InvoiceSortType sortType) {
        this.sortType = sortType;
    }

    public List<InvoiceDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<InvoiceDocument> documents) {
        this.documents = documents;
    }

    public boolean isNotSplitRecipients() {
        return notSplitRecipients;
    }

    public void setNotSplitRecipients(boolean notSplitRecipients) {
        this.notSplitRecipients = notSplitRecipients;
    }

    public CbiInvoiceType getCbiInvoiceType() {
        return cbiInvoiceType;
    }

    public WeekDays getReleaseDayOfWeek() {
        return releaseDayOfWeek;
    }

    public void setCbiInvoiceType(CbiInvoiceType cbiInvoiceType) {
        this.cbiInvoiceType = cbiInvoiceType;
    }

    public void setReleaseDayOfWeek(WeekDays releaseDayOfWeek) {
        this.releaseDayOfWeek = releaseDayOfWeek;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isNoInvoiceDocument() {
        return noInvoiceDocument;
    }

    public void setNoInvoiceDocument(boolean noInvoiceDocument) {
        this.noInvoiceDocument = noInvoiceDocument;
    }

}
