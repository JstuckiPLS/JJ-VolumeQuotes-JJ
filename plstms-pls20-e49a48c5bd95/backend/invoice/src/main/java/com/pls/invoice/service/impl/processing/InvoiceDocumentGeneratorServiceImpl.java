package com.pls.invoice.service.impl.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.MimeTypes;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.invoice.service.pdf.InvoicePdfDocumentParameter;
import com.pls.invoice.service.pdf.InvoicePdfDocumentWriter;
import com.pls.invoice.service.processing.InvoiceDocumentGeneratorService;
import com.pls.invoice.service.xsl.CBICustomizedExcelReportBuilder;
import com.pls.invoice.service.xsl.CBIFlexVersionExcelReportBuilder;
import com.pls.invoice.service.xsl.CBIGroupedExcelReportBuilder;
import com.pls.invoice.service.xsl.CBIStandardExcelReportBuilder;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Implementation of {@link InvoiceDocumentGeneratorService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class InvoiceDocumentGeneratorServiceImpl implements InvoiceDocumentGeneratorService {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Autowired
    private InvoicePdfDocumentWriter invoicePdfDocumentWriter;


    @Value("/templates/CBI_standard_common.xlsx")
    private ClassPathResource standardCommonReport;

    @Value("/templates/CBI_standard_gainshare.xlsx")
    private ClassPathResource standardGainShareReport;

    @Value("/templates/CBI_grouped.xlsx")
    private ClassPathResource invoiceReport;

    @Value("/templates/CBI_flex_version.xlsx")
    private ClassPathResource flexVersionReport;

    @Value("/templates/CBI_customized.xlsx")
    private ClassPathResource customizedReport;

    @Override
    public Map<InvoiceDocument, LoadDocumentEntity> generateInvoiceDocuments(BillToEntity billTo, List<LoadAdjustmentBO> invoices)
            throws ApplicationException {
        if (invoices.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<InvoiceDocument, LoadDocumentEntity>();
        for (InvoiceDocument invoiceDocumentType : InvoiceDocument.values()) {
            if (isDocumentShouldBeGenerated(invoiceDocumentType, billTo)) {
                LoadDocumentEntity invoice = generateDocument(invoiceDocumentType, invoices, billTo);
                if (invoice != null) {
                    invoiceDocuments.put(invoiceDocumentType, invoice);
                }
            }
        }
        return invoiceDocuments;
    }

    private boolean isDocumentShouldBeGenerated(InvoiceDocument invoiceDocumentType, BillToEntity billTo) {
        if (invoiceDocumentType == InvoiceDocument.PDF) {
            return billTo.getInvoiceSettings().getInvoiceType() == InvoiceType.TRANSACTIONAL
                    || isInvoiceFormatSelected(billTo.getInvoiceSettings(), InvoiceDocument.PDF);
        } else if (invoiceDocumentType == InvoiceDocument.STANDARD_EXCEL) {
            return billTo.getInvoiceSettings().getInvoiceType() == InvoiceType.CBI
                    && isInvoiceFormatSelected(billTo.getInvoiceSettings(), InvoiceDocument.STANDARD_EXCEL);
        } else if (invoiceDocumentType == InvoiceDocument.GROUPED_BY_GL_NUMBER_EXCEL) {
            return billTo.getInvoiceSettings().getInvoiceType() == InvoiceType.CBI
                    && isInvoiceFormatSelected(billTo.getInvoiceSettings(), InvoiceDocument.GROUPED_BY_GL_NUMBER_EXCEL);
        } else if (invoiceDocumentType == InvoiceDocument.FLEX_VERSION_EXCEL) {
            return billTo.getInvoiceSettings().getInvoiceType() == InvoiceType.CBI
                    && isInvoiceFormatSelected(billTo.getInvoiceSettings(), InvoiceDocument.FLEX_VERSION_EXCEL);
        } else if (invoiceDocumentType == InvoiceDocument.CUSTOMIZED_EXCEL) {
            return billTo.getInvoiceSettings().getInvoiceType() == InvoiceType.CBI
                    && isInvoiceFormatSelected(billTo.getInvoiceSettings(), InvoiceDocument.CUSTOMIZED_EXCEL);
        }
        return false;
    }

    private boolean isInvoiceFormatSelected(InvoiceSettingsEntity invoiceSettings, InvoiceDocument invoiceDocument) {
        return invoiceSettings != null && invoiceSettings.getDocuments() != null
                && invoiceSettings.getDocuments().contains(invoiceDocument);
    }

    private LoadDocumentEntity generateDocument(InvoiceDocument invoiceDocumentType, List<LoadAdjustmentBO> invoices,
            BillToEntity billTo) throws ApplicationException {
        if (invoiceDocumentType == InvoiceDocument.PDF) {
            return getPDFInvoiceAttachment(invoices);
        } else if (invoiceDocumentType == InvoiceDocument.STANDARD_EXCEL) {
            return getStandardExcelAttachment(invoices, billTo);
        } else if (invoiceDocumentType == InvoiceDocument.GROUPED_BY_GL_NUMBER_EXCEL) {
            return getGroupedExcelAttachment(invoices);
        } else if (invoiceDocumentType == InvoiceDocument.FLEX_VERSION_EXCEL) {
            return getFlexVersionAttachment(invoices, billTo);
        } else if (invoiceDocumentType == InvoiceDocument.CUSTOMIZED_EXCEL) {
            return getCustomizedExcelAttachment(invoices, billTo);
        }
        return null;
    }

    private LoadDocumentEntity getGroupedExcelAttachment(List<LoadAdjustmentBO> invoices)
            throws ApplicationException {
        LoadDocumentEntity document = createDocument(MimeTypes.XLSX);
        File docFile = new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());

        String invoiceNumber = getGroupInvoiceNumber(invoices.get(0));

        try {
            new CBIGroupedExcelReportBuilder(invoiceReport).generateReport(invoices, invoiceNumber, new FileOutputStream(docFile));
        } catch (IOException e) {
            FileUtils.deleteQuietly(docFile);
            throw new ApplicationException("Error generating CBI Report. " + e.getMessage(), e);
        }

        return documentService.savePreparedDocument(document);
    }

    private LoadDocumentEntity getStandardExcelAttachment(List<LoadAdjustmentBO> invoices, BillToEntity billTo) throws ApplicationException {
        InvoiceSettingsEntity invoiceSettings = billTo.getInvoiceSettings();

        LoadDocumentEntity document = createDocument(MimeTypes.XLSX);
        File docFile = new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());

        Date invoiceDate = getInvoiceDate(invoices.get(0));
        String invoiceNumber = getGroupInvoiceNumber(invoices.get(0));

        try {
            new CBIStandardExcelReportBuilder(invoiceSettings.isGainshareOnly(), standardCommonReport, standardGainShareReport)
                    .generateReport(billTo, invoices, invoiceDate, invoiceNumber, new FileOutputStream(docFile));
        } catch (IOException e) {
            FileUtils.deleteQuietly(docFile);
            throw new ApplicationException("Error generating CBI Report. " + e.getMessage(), e);
        }

        return documentService.savePreparedDocument(document);
    }

    private LoadDocumentEntity getCustomizedExcelAttachment(List<LoadAdjustmentBO> invoices, BillToEntity billTo) throws ApplicationException {
        LoadDocumentEntity document = createDocument(MimeTypes.XLSX);
        File docFile = new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());

        Date invoiceDate = getInvoiceDate(invoices.get(0));
        String invoiceNumber = getGroupInvoiceNumber(invoices.get(0));

        try {
            new CBICustomizedExcelReportBuilder(customizedReport).generateReport(billTo, invoices, invoiceDate, invoiceNumber,
                    new FileOutputStream(docFile));
        } catch (IOException e) {
            FileUtils.deleteQuietly(docFile);
            throw new ApplicationException("Error generating CBI Customized Report. " + e.getMessage(), e);
        }

        return documentService.savePreparedDocument(document);
    }

    private LoadDocumentEntity getFlexVersionAttachment(List<LoadAdjustmentBO> invoices, BillToEntity billTo) throws ApplicationException {
        LoadDocumentEntity document = createDocument(MimeTypes.XLSX);
        File docFile = new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());
        String invoiceNumber = getGroupInvoiceNumber(invoices.get(0));
        Date invoiceDate = getInvoiceDate(invoices.get(0));

        try {
            new CBIFlexVersionExcelReportBuilder(flexVersionReport).generateReport(billTo, invoices, invoiceDate, invoiceNumber,
                    new FileOutputStream(docFile));
        } catch (IOException e) {
            FileUtils.deleteQuietly(docFile);
            throw new ApplicationException("Error generating CBI Flex Version Report. " + e.getMessage(), e);
        }
        return documentService.savePreparedDocument(document);
    }

    private LoadDocumentEntity getPDFInvoiceAttachment(List<LoadAdjustmentBO> invoices) throws ApplicationException {
        LoadDocumentEntity document = createDocument(MimeTypes.PDF);

        File file = new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());
        try {
            invoicePdfDocumentWriter.write(new InvoicePdfDocumentParameter(invoices), new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            FileUtils.deleteQuietly(file);
            throw new ApplicationException("Error generating invoice. " + e.getMessage(), e);
        }

        documentService.savePreparedDocument(document);
        return document;
    }

    private LoadDocumentEntity createDocument(MimeTypes mimeType) throws DocumentSaveException {
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setDocumentType(DocumentTypes.INVOICE.name());
        document.setStatus(Status.ACTIVE);
        document.setFileType(mimeType);
        documentService.prepareDocument(document);
        return document;
    }

    private String getInvoiceNumber(LoadAdjustmentBO invoice) {
        return invoice.getAdjustment() == null ? invoice.getLoad().getActiveCostDetail().getInvoiceNumber() : invoice
                .getAdjustment().getInvoiceNumber();
    }

    private String getGroupInvoiceNumber(LoadAdjustmentBO invoice) {
        String invoiceNumber = invoice.getAdjustment() == null ? invoice.getLoad().getActiveCostDetail()
                .getGroupInvoiceNumber() : invoice.getAdjustment().getGroupInvoiceNumber();
        return invoiceNumber == null ? getInvoiceNumber(invoice) : invoiceNumber;
    }

    private Date getInvoiceDate(LoadAdjustmentBO invoice) {
        Date glDate = null;
        if (invoice.getAdjustment() == null) {
            glDate = invoice.getLoad().getActiveCostDetail().getGeneralLedgerDate();
        } else {
            glDate = invoice.getAdjustment().getGeneralLedgerDate();
        }
        return glDate == null ? new Date() : glDate;
    }
}
