package com.pls.invoice.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.pdf.TiffConverter;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.impl.DocumentServiceImpl;
import com.pls.invoice.service.CustomerInvoiceDocumentService;
import com.pls.invoice.service.SharedDriveService;
import com.pls.invoice.service.processing.InvoiceDocumentGeneratorService;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;
import com.pls.shipment.service.ShipmentDocumentService;

/**
 * Implementation of {@link CustomerInvoiceDocumentService}.
 * 
 * @author Alexander Nalapko
 *
 */
@Service
@Transactional
public class CustomerInvoiceDocumentServiceImpl implements CustomerInvoiceDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerInvoiceDocumentServiceImpl.class);

    private static final String OUTPUT_DIRECTORY = "invoiceDocuments";

    @Value("${documents.path}")
    private String documentsPath;

    @Autowired
    private ShipmentDocumentService documentService;

    @Autowired
    private SharedDriveService sharedDriveService;

    @Autowired
    private LoadDocumentDao loadDocumentDao;

    @Autowired
    private InvoiceDocumentGeneratorService documentGeneratorService;

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Autowired
    private DocumentServiceImpl documentServiceImpl;

    @Override
    public void convertAndSendDocument(Long invoiceId, BillToEntity billTo, List<LoadAdjustmentBO> invoices,
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments) throws ApplicationException, IOException {
        if (billTo.getInvoiceSettings().getCbiInvoiceType() == CbiInvoiceType.FIN) {
            LOGGER.info("Converting and sendging documents for invoice ID: {}", invoiceId);
            convertDocuments(invoiceId, invoices);
            LOGGER.info("Finished converting documents for invoice ID: {}", invoiceId);
            sharedDriveService.connectAndStoreFilesFromDir(documentsPath + File.separator + OUTPUT_DIRECTORY + File.separator + invoiceId);
            LOGGER.info("Finished sending documents for invoice ID: {}", invoiceId);
        }
        if (invoiceDocuments.containsKey(InvoiceDocument.PDF)) {
            storePdf(invoices, invoiceDocuments, billTo);
            return;
        }
        for (LoadDocumentEntity doc : invoiceDocuments.values()) {
            storeExcel(invoices, doc);
        }
        LOGGER.info("Finished copying documents for invoice ID: {}", invoiceId);
    }

    private void convertDocuments(Long invoiceId, List<LoadAdjustmentBO> invoices) throws ApplicationException {
        for (LoadAdjustmentBO invoice : invoices) {
            if (invoice.getAdjustment() == null || invoice.getAdjustment().getId() == null) {
                List<LoadDocumentEntity> documents = documentService.findReqDocumentsForLoad(invoice.getLoad().getId());
                for (LoadDocumentEntity document : documents) {
                    TiffConverter.convert(documentsPath, invoiceId, document);
                }
            }
        }
    }

    private void storeExcel(List<LoadAdjustmentBO> invoices, LoadDocumentEntity doc) throws ApplicationException, IOException {
        String filename = String.format("ExcelFile_%s.xls", getGroupInvoiceNumber(invoices.get(0)));
        File docFile = getFileFromLoadDocumentEntity(doc);
        sharedDriveService.storeInvoiceCopy(filename, docFile);
    }

    private void storePdf(List<LoadAdjustmentBO> invoices, Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments,
            BillToEntity billTo) throws ApplicationException, IOException {
        if (InvoiceType.CBI.equals(billTo.getInvoiceSettings().getInvoiceType()) || invoices.size() == 1) {
            LoadDocumentEntity loadDocumentEntity = invoiceDocuments.get(InvoiceDocument.PDF);
            String filename = getFileName(InvoiceType.TRANSACTIONAL.equals(billTo.getInvoiceSettings().getInvoiceType()), invoices.get(0));
            File file = getFileFromLoadDocumentEntity(loadDocumentEntity);
            sharedDriveService.storeInvoiceCopy(filename, file);
        } else {
            for (LoadAdjustmentBO invoice : invoices) {
                String filename = getFileName(true, invoice);
                LoadDocumentEntity loadDocumentEntity = documentGeneratorService.generateInvoiceDocuments(billTo,
                            Collections.singletonList(invoice)).get(InvoiceDocument.PDF);
                File file = null;
                try {
                    file = getFileFromLoadDocumentEntity(loadDocumentEntity);
                    sharedDriveService.storeInvoiceCopy(filename, file);
                } finally {
                    documentServiceImpl.deleteFromFileSystem(loadDocumentEntity);
                }
                loadDocumentDao.deleteDocuments(Collections.singletonList(loadDocumentEntity.getId()));
            }
        }
    }

    private String getFileName(boolean isTransactional, LoadAdjustmentBO invoice) {
        if (isTransactional) {
            return String.format("Transactional_%s.pdf", getInvoiceNumber(invoice));
        }
        return String.format("MultiTransactional_%s.pdf", getInvoiceNumber(invoice));
    }

    private File getFileFromLoadDocumentEntity(LoadDocumentEntity loadDocumentEntity) {
        return new File(docFileNamesResolver.buildDirectPath(loadDocumentEntity.getDocumentPath()), loadDocumentEntity.getDocFileName());
    }

    private String getInvoiceNumber(LoadAdjustmentBO invoice) {
        return invoice.getAdjustment() == null ? invoice.getLoad().getActiveCostDetail().getInvoiceNumber() : invoice.getAdjustment()
            .getInvoiceNumber();
    }

    private String getGroupInvoiceNumber(LoadAdjustmentBO invoice) {
        String invoiceNumber = invoice.getAdjustment() == null ? invoice.getLoad().getActiveCostDetail()
            .getGroupInvoiceNumber() : invoice.getAdjustment().getGroupInvoiceNumber();
            return invoiceNumber == null ? getInvoiceNumber(invoice) : invoiceNumber;
    }
}
