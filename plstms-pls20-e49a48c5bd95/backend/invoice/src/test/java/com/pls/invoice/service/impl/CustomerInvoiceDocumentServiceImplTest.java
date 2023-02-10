package com.pls.invoice.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.invoice.service.SharedDriveService;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;
import com.pls.shipment.service.ShipmentDocumentService;

/**
 * test for {@link CustomerInvoiceDocumentServiceImpl}.
 * 
 * @author Alexander Nalapko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerInvoiceDocumentServiceImplTest {

    @InjectMocks
    private CustomerInvoiceDocumentServiceImpl service;

    @Mock
    private ShipmentDocumentService documentService;

    @Mock
    private SharedDriveService sharedDriveService;

    private static final Long INVOICE_ID = 725L;
    private static final Long LOAD_ID = 1L;
    private static final String OUTPUT_DIRECTORY = "invoiceDocuments";
    private static final String DOCUMENTS_PATH = "..";

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(service, "documentsPath", DOCUMENTS_PATH);
    }

    @Test
    public void convertAndSendDocumentTest() throws ApplicationException, IOException {

        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>();
        LoadAdjustmentBO invoice = new LoadAdjustmentBO(new LoadEntity());
        invoice.getLoad().setId(LOAD_ID);
        invoices.add(invoice);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();

        List<LoadDocumentEntity> documents = new ArrayList<LoadDocumentEntity>();
        when(documentService.findReqDocumentsForLoad(LOAD_ID)).thenReturn(documents);

        String fileDir = DOCUMENTS_PATH + File.separator + OUTPUT_DIRECTORY + File.separator + INVOICE_ID;
        when(sharedDriveService.connectAndStoreFilesFromDir(fileDir)).thenReturn(new HashMap<String, Long>());

        BillToEntity billTo = new BillToEntity();
        billTo.setPaymentMethod("PREPAID_ONLY");
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setCbiInvoiceType(CbiInvoiceType.FIN);
        billTo.setInvoiceSettings(invoiceSettings);
        service.convertAndSendDocument(INVOICE_ID, billTo, invoices, invoiceDocuments);

        verify(sharedDriveService).connectAndStoreFilesFromDir(fileDir);
        verify(documentService).findReqDocumentsForLoad(LOAD_ID);

    }
}
