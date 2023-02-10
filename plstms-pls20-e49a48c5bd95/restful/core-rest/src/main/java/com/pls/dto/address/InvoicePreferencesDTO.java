package com.pls.dto.address;

import java.util.List;

import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceProcessingType;
import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.core.domain.enums.WeekDays;

/**
 * DTO for customer's invoice preferences.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class InvoicePreferencesDTO {
    private Long id;
    private InvoiceType invoiceType = InvoiceType.TRANSACTIONAL;
    private InvoiceProcessingType processingType = InvoiceProcessingType.AUTOMATIC;
    private int processingTimeInMinutes = 300; // 5:00 AM
    private boolean gainshareOnly;
    private boolean ediInvoice;
    private WeekDays processingDayOfWeek;
    private TimezoneDTO processingTimezone = new TimezoneDTO();
    private ProcessingPeriod processingPeriod;
    private InvoiceSortType sortType;
    private List<InvoiceDocument> documents;
    private boolean notSplitRecipients;
    private CbiInvoiceType cbiInvoiceType;
    private WeekDays releaseDayOfWeek;
    private boolean noInvoiceDocument;

    private List<RequiredDocumentDTO> requiredDocuments;

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

    public int getProcessingTimeInMinutes() {
        return processingTimeInMinutes;
    }

    public void setProcessingTimeInMinutes(int processingTimeInMinutesInMinutes) {
        this.processingTimeInMinutes = processingTimeInMinutesInMinutes;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RequiredDocumentDTO> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<RequiredDocumentDTO> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    public TimezoneDTO getProcessingTimezone() {
        return processingTimezone;
    }

    public void setProcessingTimezone(TimezoneDTO processingTimezone) {
        this.processingTimezone = processingTimezone;
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

    public boolean isNotSplitRecipients() {
        return notSplitRecipients;
    }

    public void setNotSplitRecipients(boolean notSplitRecipients) {
        this.notSplitRecipients = notSplitRecipients;
    }

    public List<InvoiceDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<InvoiceDocument> documents) {
        this.documents = documents;
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

    public boolean isNoInvoiceDocument() {
        return noInvoiceDocument;
    }

    public void setNoInvoiceDocument(boolean noInvoiceDocument) {
        this.noInvoiceDocument = noInvoiceDocument;
    }

}
